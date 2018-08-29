import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerMain
{
	// This class implements the actual main method of the Server
	public static void main(String args[]) 
	{
		try
		{
			// Instantiating the Server 
	        Server server = new Server();
	        
	        // Exporting the object of implementation class
	        // Here we are exporting the remote object (server object) to the stub 
	        RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(server, 0);
	        
	        // Binding the remote object (stub) in the registry 
	        Registry registry = LocateRegistry.getRegistry();
	        
	        registry.bind("RemoteInterface", stub);
	        System.err.println("Server ready");
		}
		catch (Exception e)
		{
			System.err.println("Server Exception: " + e.toString());
			e.printStackTrace();
		}
	}
}