import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ModeratorInterface extends Remote 
{
	int getNumberPlayers() throws RemoteException;
	String connect() throws RemoteException;
	int assignPlayerNumber() throws RemoteException;
	int getPlayerTurnNumber() throws RemoteException;
	String makeMove(int playerNum, int cellNumber) throws RemoteException;
	int[] getOccupiedCells() throws RemoteException;
	int getWinner() throws RemoteException;
	void setWinner(int playerNum) throws RemoteException;
	void checkWinner() throws RemoteException;
	int checkRows() throws RemoteException;
	int checkCols() throws RemoteException;
	int checkDiagnols() throws RemoteException;
}