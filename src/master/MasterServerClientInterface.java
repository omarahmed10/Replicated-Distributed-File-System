package master;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import fileSystem.ReadMsg;
import fileSystem.WriteMsg;

public interface MasterServerClientInterface extends Remote {
	/**
	 * Read file from server
	 * 
	 * @param fileName
	 * @return the required info
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws RemoteException
	 */
	public ReadMsg read(String fileName) throws FileNotFoundException, IOException, RemoteException;

	/**
	 * Start a new write transaction
	 * 
	 * @param fileName
	 * @return the required info
	 * @throws RemoteException
	 * @throws IOException
	 */
	public WriteMsg write(String fileName) throws RemoteException;

}
