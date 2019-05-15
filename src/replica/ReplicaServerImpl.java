package replica;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fileSytem.FileContent;
import fileSytem.MessageNotFoundException;
import fileSytem.Transaction;
import fileSytem.WriteMsg;
import master.MasterPrimaryServersInterface;

public class ReplicaServerImpl implements ReplicaServerClientInterface {
	private ReplicaLoc replicaLoc;
	private final String ID;
	private String masterHost;
	private int masterPort;
	private Map<Long, Transaction> transactions;
	private String directory;

	public ReplicaServerImpl(ReplicaLoc replicaLoc, String ID,
			String masterHost, int masterPort) throws IOException {
		this.replicaLoc = replicaLoc;
		this.ID = ID;
		this.masterHost = masterHost;
		this.masterPort = masterPort;

		transactions = Collections.synchronizedMap(new HashMap<>());
		directory = "replica" + this.ID;
		new File(directory).mkdirs();
	}

	@Override
	public WriteMsg write(long txnID, long msgSeqNum, FileContent data)
			throws RemoteException, IOException, NotBoundException {
		if (!transactions.containsKey(txnID))
			transactions.put(txnID,
					new Transaction(txnID, data.getFileName(), directory));
		int numOfMsgs = transactions.get(txnID).write(msgSeqNum, data);

		writeToReplicaServers(getReplicaServersLocs(data.getFileName()), txnID,
				msgSeqNum, data);

		// timestamp has no meaning here, instead numOfMsgs is used
		// also we can return object other than WriteMsg
		return new WriteMsg(txnID, numOfMsgs, replicaLoc);
	}

	@Override
	public FileContent read(long txnID, String fileName)
			throws IOException, RemoteException {
		if (!transactions.containsKey(txnID))
			transactions.put(txnID,
					new Transaction(txnID, fileName, directory));

		return transactions.get(txnID).read(fileName);
	}

	// don't know what's the rule of MessageNotFoundException
	@Override
	public boolean commit(long txnID, long numOfMsgs)
			throws MessageNotFoundException, RemoteException, IOException,
			NotBoundException {
		if (!transactions.containsKey(txnID))
			return false;

		return transactions.remove(txnID).commit(numOfMsgs)
				&& commitReplicaServers(
						getReplicaServersLocs(
								transactions.get(txnID).getFileName()),
						txnID, numOfMsgs);
	}

	@Override
	public boolean abort(long txnID)
			throws RemoteException, IOException, NotBoundException {
		if (!transactions.containsKey(txnID))
			return false;

		return transactions.remove(txnID).abort() && abortReplicaServers(
				getReplicaServersLocs(transactions.get(txnID).getFileName()),
				txnID);
	}

	public ReplicaLoc[] getReplicaServersLocs(String fileName)
			throws RemoteException, NotBoundException {
		Registry masterRegistry = LocateRegistry.getRegistry(masterHost,
				masterPort);
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

	public String getID() {
		return ID;
	}

}
