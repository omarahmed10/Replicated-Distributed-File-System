package replica;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import fileSytem.FileContent;
import fileSytem.MessageNotFoundException;
import fileSytem.Transaction;
import fileSytem.WriteMsg;
import master.MasterPrimaryServersInterface;

public class ReplicaServerImpl implements ReplicaServerClientInterface {
	private ReplicaLoc replicaLoc;
	// make it synchronized ?
	private Map<Long, Transaction> transactions;

	public ReplicaServerImpl(ReplicaLoc replicaLoc) {
		// we should start the server here
		this.replicaLoc = replicaLoc;
		transactions = new HashMap<>();
	}

	@Override
	public WriteMsg write(long txnID, long msgSeqNum, FileContent data)
			throws RemoteException, IOException {
		if (!transactions.containsKey(txnID))
			transactions.put(txnID, new Transaction(txnID, data.getFileName()));
		int numOfMsgs = transactions.get(txnID).write(msgSeqNum, data);

		// timestamp has no meaning here, instead numOfMsgs is used
		// also we can return object other than WriteMsg
		return new WriteMsg(txnID, numOfMsgs, replicaLoc);
	}

	@Override
	public FileContent read(long txnID, String fileName)
			throws IOException, RemoteException {
		if (!transactions.containsKey(txnID))
			transactions.put(txnID, new Transaction(txnID, fileName));

		return transactions.get(txnID).read(fileName);
	}

	// don't know what's the rule of MessageNotFoundException
	@Override
	public boolean commit(long txnID, long numOfMsgs)
			throws MessageNotFoundException, RemoteException, IOException {
		if (!transactions.containsKey(txnID))
			return false;

		return transactions.remove(txnID).commit(numOfMsgs);
	}

	@Override
	public boolean abort(long txnID) throws RemoteException, IOException {
		if (!transactions.containsKey(txnID))
			return false;

		return transactions.remove(txnID).abort();
	}

	public ReplicaLoc[] getReplicaServersLocs(String host, int port,
			String fileName) throws RemoteException, NotBoundException {
		Registry masterRegistry = LocateRegistry.getRegistry(host, port);
		MasterPrimaryServersInterface masterStub = (MasterPrimaryServersInterface) masterRegistry
				.lookup("masterAPI");
		return masterStub.getReplicaServersLocs(fileName);
	}

	public void writeToReplicaServers(ReplicaLoc[] replicaServersLocs,
			long txnID, long msgSeqNum, FileContent data)
			throws RemoteException, NotBoundException, IOException {

		if (replicaLoc.equals(replicaServersLocs[0])) {
			for (int i = 1; i < replicaServersLocs.length; i++) {
				Registry replicaRegistry = LocateRegistry.getRegistry(
						replicaServersLocs[i].getHost(),
						replicaServersLocs[i].getPort());
				ReplicaServerClientInterface replicaStub = (ReplicaServerClientInterface) replicaRegistry
						.lookup("replicaAPI");
				replicaStub.write(txnID, msgSeqNum, data);
			}
		}
	}

	public boolean commitReplicaServers(ReplicaLoc[] replicaServersLocs,
			long txnID, long numOfMsgs) throws RemoteException,
			NotBoundException, IOException, MessageNotFoundException {
		boolean ack = true;
		if (replicaLoc.equals(replicaServersLocs[0])) {
			for (int i = 1; i < replicaServersLocs.length; i++) {
				Registry replicaRegistry = LocateRegistry.getRegistry(
						replicaServersLocs[i].getHost(),
						replicaServersLocs[i].getPort());
				ReplicaServerClientInterface replicaStub = (ReplicaServerClientInterface) replicaRegistry
						.lookup("replicaAPI");
				ack = ack && replicaStub.commit(txnID, numOfMsgs);
			}
		}
		return ack;
	}

	public boolean abortReplicaServers(ReplicaLoc[] replicaServersLocs,
			long txnID) throws RemoteException, NotBoundException, IOException {
		boolean ack = true;
		if (replicaLoc.equals(replicaServersLocs[0])) {
			for (int i = 1; i < replicaServersLocs.length; i++) {
				Registry replicaRegistry = LocateRegistry.getRegistry(
						replicaServersLocs[i].getHost(),
						replicaServersLocs[i].getPort());
				ReplicaServerClientInterface replicaStub = (ReplicaServerClientInterface) replicaRegistry
						.lookup("replicaAPI");
				ack = ack && replicaStub.abort(txnID);
			}
		}
		return ack;
	}

	public ReplicaLoc getReplicaLoc() {
		return replicaLoc;
	}

}
