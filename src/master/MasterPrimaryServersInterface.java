package master;

import java.rmi.Remote;
import java.rmi.RemoteException;

import replica.ReplicaLoc;

public interface MasterPrimaryServersInterface extends Remote {
	public ReplicaLoc[] getReplicaServersLocs(String fileName)
			throws RemoteException;
}
