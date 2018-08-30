import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote 
{
	public String getFreeModerator() throws RemoteException;
	public void killModerator(String moderatorName) throws RemoteException;
}