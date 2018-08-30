import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ClientMain
{
	// This class implements the actual main method of the Client
	
	static Scanner sc = new Scanner(System.in);
	static volatile String inp;
	// timeoutTime in milliseconds
	static long timeoutTime = 10 * 1000;
	
	static class TakingInput extends Thread
	{
	    public void run()
	    {
	    	// Getting the input from the user
	    	System.out.print("Enter Cell Number : ");
        	inp = sc.nextLine();
	    }
	}
	
	public static String getInp()
	{
		return inp;
	}
	
	public static void printBoard(int[] a)
	{
		/**
		 * Prints the boards with the entries of certain cells replaced with 'x' or 'o'
		 * For example if a[i] has 1 or 2, then cellNumber=i+1 is to be replaced with 'x' or 'o'
		 */
		
		int cellVal = 0;
		String toBePrinted;
		
		System.out.println("");
		for (int i=0; i<3; i++)
		{
			for (int j=0; j<3; j++)
			{
				cellVal += 1;
				if (a[cellVal - 1] == 1)
					toBePrinted = "x";
				else if (a[cellVal - 1] == 2)
					toBePrinted = "o";
				else
					toBePrinted = Integer.toString(cellVal);
				
				if (j==2)
					System.out.print(" " + toBePrinted);
				else
					System.out.print(" " + toBePrinted + " " + "|");
			}
			
			if (i != 2)
				System.out.println("\n-----------");
			else
				System.out.println("\n");
		}
	}
	
	
	public static void main(String[] args)
	{
		int playerNum;
		String moderatorName;
		String userResponse;
		long startTime;
		boolean timeout;
        
		try
		{
			// Getting the Registry
			Registry registry = LocateRegistry.getRegistry(null);
			
			// Connecting to Server via RMI
			ServerInterface stubServer = (ServerInterface) registry.lookup("ServerInterface");
			
			while (true)
			{	
				// Get the free moderator, if no free moderator then server creates new moderator and returns the name
				moderatorName = stubServer.getFreeModerator(); 
				
				// Looking up the registry for the remote object
				ModeratorInterface stub = (ModeratorInterface) registry.lookup(moderatorName);
				
				// Trying to connect to the server
				String connectionResponseMsg = stub.connect();
				if (connectionResponseMsg.equals("Busy"))
				{
					System.err.print("Server is Busy, please try later");
					return;
				}
				else
				{
					playerNum = stub.assignPlayerNumber();
					System.out.println("You are player" + Integer.toString(playerNum));
				}
				
				// Waiting till 2 players join the game
				int numPlayers;
				boolean oncePrinted = false;
				while (true)
				{
					numPlayers = stub.getNumberPlayers();
					if ((numPlayers < 2) && (oncePrinted == false))
					{
						oncePrinted = true;
						System.out.println("Waiting for another Player\n");
					}
					else if (numPlayers == 2)
					{
						System.out.println("Another Player Joined");
						System.out.println("Game about to start\n");
						break;
					}
				}
				
				int cellNumber;
				String responseMsg;
				
				// Gameplay starts
				if (playerNum == 1)
				{
					// player1 code
					while (true)
					{
						// Check if anyone is winner
						if (stub.getWinner() != 0)
							break;
						
						// Print board
						printBoard(stub.getOccupiedCells());
						
						// Make Move by player1
						responseMsg = "";
						while (!responseMsg.equals("Valid Step"))
						{	
							inp = null;
							timeout = false;
							
							// Start the input thread
							Thread t = new TakingInput();
							t.start();
							startTime = System.currentTimeMillis(); //fetch starting time
							while (getInp() == null) 
							{
								// Break if time is 10 seconds
								if ((System.currentTimeMillis()-startTime) >= timeoutTime)
								{
									timeout = true;
									break;
								}
							}
							if (timeout == true)
							{
								System.out.println();
								System.out.println("Time Up Fucker");
								break;
							}
							
							cellNumber = Integer.parseInt(inp);
							
							responseMsg = stub.makeMove(playerNum, cellNumber);
							if (!responseMsg.equals("Valid Step"))
								System.out.println(responseMsg);
						}
						
						// Check if anyone is winner
						if (stub.getWinner() != 0)
							break;
						
						// Wait till player2 makes his/her step
						System.out.println("Waiting for the other player to make his move...");
						startTime = System.currentTimeMillis(); //fetch starting time
						while (stub.getPlayerTurnNumber() == 2) 
						{
							// Break if time is 10 seconds
							if ((System.currentTimeMillis()-startTime) >= timeoutTime)
							{
								stub.setWinner(playerNum);
								break;
							}
						}
					}
					
					System.out.println("\n");
					printBoard(stub.getOccupiedCells());
					int winner = stub.getWinner();
					if (playerNum == winner)
						System.out.println("You Won");
					else if (winner == 3)
						System.out.println("Draw");
					else
						System.out.println("You Lost");
					
					// Kill the moderator (Both the players should not kill the moderator, only player1 should kill)
					stubServer.killModerator(moderatorName);
					
				}
				else
				{
					// player2 code
					while (true)
					{
						// Wait till player1 makes his/her step
						System.out.println("Waiting for the other player to make his move...");
						startTime = System.currentTimeMillis(); //fetch starting time
						while (stub.getPlayerTurnNumber() == 1) 
						{
							// Break if time is 10 seconds
							if ((System.currentTimeMillis()-startTime) >= timeoutTime)
							{
								stub.setWinner(playerNum);
								break;
							}
						}
						
						// Check if anyone is winner
						if (stub.getWinner() != 0)
							break;
						
						// Print board
						printBoard(stub.getOccupiedCells());
						
						// Make Move by player2
						responseMsg = "";
						while (!responseMsg.equals("Valid Step"))
						{	
							inp = null;
							timeout = false;
							
							// Start the input thread
							Thread t = new TakingInput();
							t.start();
							startTime = System.currentTimeMillis(); //fetch starting time
							while (getInp() == null) 
							{
								// Break if time is 10 seconds
								if ((System.currentTimeMillis()-startTime) >= timeoutTime)
								{
									timeout = true;
									break;
								}
							}
							if (timeout == true)
							{
								System.out.println();
								System.out.println("Time Up Fucker");
								break;
							}
							
							cellNumber = Integer.parseInt(inp);
							
							responseMsg = stub.makeMove(playerNum, cellNumber);
							if (!responseMsg.equals("Valid Step"))
								System.out.println(responseMsg);
							
						}
						
						// Check if anyone is winner
						if (stub.getWinner() != 0)
							break;
					}
					System.out.println("\n");
					printBoard(stub.getOccupiedCells());
					int winner = stub.getWinner();
					if (playerNum == winner)
						System.out.println("You Won");
					else if (winner == 3)
						System.out.println("Draw Match");
					else
						System.out.println("You Lost");
				}
				
				System.out.print("Wanna play another game (y/n) : ");
				userResponse = sc.nextLine();
				if (userResponse.equals("n"))
					break;
			}
			sc.close();
			
		}
		catch (Exception e)
		{
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}
	
}