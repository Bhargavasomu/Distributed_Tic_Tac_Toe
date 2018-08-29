import java.rmi.RemoteException;
import java.util.Random;

public class Server implements RemoteInterface
{
	// All the functions listed here are the functions belonging to the server
	
	int numClientsConnected;
	int prevAllocatedPlayerNum;
	// Stores 1 or 2, meaning that it is playerx (x=1 or x=2) chance to play
	int playerTurn;
	// winner = 1, means that player1 won the game
	int winner;
	Random rand;
	// cellOccupied[i] = 1 means that (i+1) cell is occupied by player1 (values can be only 1 or 2 or 3 -> 3 means draw)
	int[] cellOccupied = new int[9];
	
	public Server()
	{
		this.numClientsConnected = 0;
		this.prevAllocatedPlayerNum = -1;
		this.rand = new Random();
		this.playerTurn = 1;
		this.winner = 0;
		for (int i=0; i<cellOccupied.length; i++)
			this.cellOccupied[i] = 0;
	}
	
	@Override
	public int getNumberPlayers()
	{
		return numClientsConnected;
	}
	
	@Override
	public int getPlayerTurnNumber()
	{
		return playerTurn;
	}
	
	@Override
	public int[] getOccupiedCells()
	{
		return cellOccupied;
	}
	
	@Override
	public int getWinner()
	{
		return winner;
	}

	@Override
	public String connect() throws RemoteException 
	{
		if (numClientsConnected < 2)
		{
			numClientsConnected += 1;
			return "Welcome";
		}
		else
			return "Busy";
	}
	
	@Override
	public int assignPlayerNumber()
	{
		int playerNum;
		if (prevAllocatedPlayerNum == -1)
		{
			// Number to the first joined player
			playerNum = rand.nextInt(2) + 1;
			prevAllocatedPlayerNum = playerNum;
		}
		else
		{
			// Number to the second joined player
			if (prevAllocatedPlayerNum == 1)
				playerNum = 2;
			else
				playerNum = 1;
		}
		
		return playerNum;
	}
	
	@Override
	public int checkRows()
	{
		boolean rowWon = false;
		int playerWon = 0;
		for (int i=1; i<8; i+=3)
		{
			rowWon = true;
			playerWon = cellOccupied[i-1];
			if (playerWon == 0)
			{
				rowWon = false;
				continue;
			}
			for (int j=(i+1); j<(i+3); j++)
			{
				if (cellOccupied[j-1] != playerWon)
				{
					rowWon = false;
					break;
				}
			}
			if (rowWon == true)
				return playerWon;
		}
		
		// No wins in row wise
		return 0;
	}
	
	@Override
	public int checkCols()
	{
		boolean colWon = false;
		int playerWon = 0;
		for (int i=1; i<4; i++)
		{
			colWon = true;
			playerWon = cellOccupied[i-1];
			if (playerWon == 0)
			{
				colWon = false;
				continue;
			}
			for (int j=(i+3); j<=(i+6); j+=3)
			{
				if (cellOccupied[j-1] != playerWon)
				{
					colWon = false;
					break;
				}
			}
			if (colWon == true)
				return playerWon;
		}
		
		// No wins in col wise
		return 0;
	}
	
	@Override
	public int checkDiagnols()
	{	
		// Check the first diagnol (1,5,9)
		if ((cellOccupied[0] == cellOccupied[4]) && (cellOccupied[4] == cellOccupied[8]) && (cellOccupied[0] != 0))
			return cellOccupied[0];
		
		// Check the second diagnol (3,5,7)
		if ((cellOccupied[2] == cellOccupied[4]) && (cellOccupied[4] == cellOccupied[6]) && (cellOccupied[2] != 0))
			return cellOccupied[0];
		
		// No wins in diagnol wise
		return 0;
	}
	
	@Override
	public void checkWinner()
	{
		int winnerNum;
		winnerNum = checkRows();
		if (winnerNum != 0)
		{
			this.winner = winnerNum;
			return;
		}
		
		winnerNum = checkCols();
		if (winnerNum != 0)
		{
			this.winner = winnerNum;
			return;
		}
		
		winnerNum = checkDiagnols();
		if (winnerNum != 0)
		{
			this.winner = winnerNum;
			return;
		}
		
		// If reached here means that, there might be a possibility of draw
		boolean draw = true; 
		for (int i=1; i<=9; i++)
		{
			if (cellOccupied[i - 1] == 0)
			{
				draw = false;
				break;
			}
		}
		if (draw == true)
			this.winner = 3;
		
		return;
	}
	
	@Override
	public String makeMove(int playerNum, int cellNumber) throws RemoteException 
	{
		// First check if this cellNumber was already occupied in the past
		if (cellOccupied[cellNumber - 1] != 0)
			return "Invalid Step";
		
		// If all clear, then insert the cellNumber
		cellOccupied[cellNumber - 1] = playerNum;
		// Give the chance to the other player
		if (playerTurn == 1)
			playerTurn = 2;
		else
			playerTurn = 1;
		
		checkWinner();
		
		return "Valid Step";
	}
	
}
