import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Set;

public class Server implements ServerInterface
{
	// Basically stores the relation between Moderator Name and the Moderator Object
	static Hashtable <String, ModeratorInterface> moderatorsList = new Hashtable <String, ModeratorInterface>();
	
	static int moderatorsNum = 0;
	
	@Override
	public String getFreeModerator() throws RemoteException
	{
		// This function returns the free moderator name, so that the client can connect to that game
		// If no moderator is free, then should create another moderator and return that name
		Set<String> moderators = moderatorsList.keySet();
        for(String moderatorName : moderators)
        {
        	ModeratorInterface m = moderatorsList.get(moderatorName);
        	if (m.getNumberPlayers() < 2)
        	{
        		return moderatorName;
        	}
        }
        
        // If reached here, means that there is no free moderator. Hence create a moderator and return that name
        moderatorsNum += 1;
        String newModeratorName = "moderator" + Integer.toString(moderatorsNum);
        createModerator(newModeratorName);
        return newModeratorName;
	}
	
	// Creating a moderator
	public static void createModerator(String moderatorName)
	{
		try
		{
			// Instantiating the Moderator
	        Moderator moderator = new Moderator();
	        
	        // Exporting the object of implementation class
	        // Here we are exporting the moderator object to the stub 
	        ModeratorInterface stub = (ModeratorInterface) UnicastRemoteObject.exportObject(moderator, 0);
	        
	        // Locating the Registry
	        Registry registry = LocateRegistry.getRegistry();
	        
	        // Binding the moderator object to the registry
	        registry.bind(moderatorName, stub);
	        
	        // Adding the Moderator to the list of moderators
	        moderatorsList.put(moderatorName, stub);
		}
		catch (Exception e)
		{
			System.err.println("Server Exception: " + e.toString());
			e.printStackTrace();
		}
		
	}
	
	@Override
	// Killing a moderator
	public void killModerator(String moderatorName)
	{
        try
        {
        	// Locating the Registry
			Registry registry = LocateRegistry.getRegistry();
			
			// Unbinding the moderator from the registry
			registry.unbind(moderatorName);
			
			// Remove the name from the moderators list
			moderatorsList.remove(moderatorName);
		}
        catch (RemoteException e) 
        {
        	System.err.println("Registry not found");
		}
        catch (NotBoundException e)
        {
        	System.err.println("Moderator with that name not present");
		}
	}
	
	// This class implements the actual main method of the Server
	public static void main(String args[]) throws RemoteException, AlreadyBoundException
	{
		System.err.println("Server ready");
		
		// Instantiating the Server
        Server server = new Server();
        
        // Exporting the server object to the stub 
        ServerInterface stubServer = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
        
        // Locating the Registry
        Registry registry = LocateRegistry.getRegistry();
        
        // Binding the moderator object to the registry
        registry.bind("ServerInterface", stubServer);
	}
}