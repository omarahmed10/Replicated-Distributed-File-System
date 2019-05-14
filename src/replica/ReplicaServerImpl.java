package replica;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Set;

import fileSystem.FileContent;
import fileSystem.MessageNotFoundException;
import fileSystem.WriteMsg;

public class ReplicaServerImpl implements ReplicaServerClientInterface {
	private ReplicaLoc replicaLoc;
	private Set<String> files;
	
	public ReplicaServerImpl(ReplicaLoc replicaLoc) {
		// we should start the server here
		this.replicaLoc = replicaLoc;
	}
	
	@Override
	public WriteMsg write(long txnID, long msgSeqNum, FileContent data) throws RemoteException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileContent read(String fileName) throws FileNotFoundException, IOException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean commit(long txnID, long numOfMsgs) throws MessageNotFoundException, RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean abort(long txnID) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}
	
	public ReplicaLoc getReplicaLoc() {
		return replicaLoc;
	}
	
	public Set<String> getFiles() {
		return files;
	}
	
	public void addFile(String fileName) {
//		files.add();
	}
	
	public void removeFile(String fileName) {
//		files.remove();
	}
}
