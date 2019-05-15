package master;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fileSytem.ReadMsg;
import fileSytem.WriteMsg;
import replica.ReplicaLoc;

public class MasterServerClientImpl extends UnicastRemoteObject
		implements MasterServerClientInterface, MasterPrimaryServersInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, ReplicaLoc[]> filesMap;

	private long transactionId = 0;
	private long timeStamp = 0;
	private int replicasNum;
	private Random rnd;
	private ReplicaLoc[] allReplicaLocation;

	protected MasterServerClientImpl(ReplicaLoc[] allReplicaLocation,
			int replicasNum) throws RemoteException {
		super();

		assert replicasNum <= allReplicaLocation.length;

		this.allReplicaLocation = allReplicaLocation;
		this.replicasNum = replicasNum;
		rnd = new Random(0);
		filesMap = new HashMap<>();
	}

	@Override
	public ReadMsg read(String fileName)
			throws FileNotFoundException, IOException, RemoteException {
		if (!filesMap.containsKey(fileName)) {
			throw new FileNotFoundException();
		}
		return new ReadMsg(getTransactionID(), filesMap.get(fileName)[0]);
	}

	@Override
	public synchronized WriteMsg write(String fileName)
			throws RemoteException, IOException {

		if (!filesMap.containsKey(fileName))
			filesMap.put(fileName, getAvailReplica());

		ReplicaLoc primaryLoc = filesMap.get(fileName)[0];

		return new WriteMsg(getTransactionID(), getTimeStamp(), primaryLoc);
	}

	private ReplicaLoc[] getAvailReplica() {
		ReplicaLoc[] availReplicas = new ReplicaLoc[replicasNum];
		Set<Integer> visitedRnd = new HashSet<>();
		for (int i = 0; i < replicasNum; i++) {
			int randIdx = rnd.nextInt(allReplicaLocation.length);
			while (visitedRnd.contains(randIdx)) {
				randIdx = rnd.nextInt(allReplicaLocation.length);
			}
			availReplicas[i] = allReplicaLocation[randIdx];
			visitedRnd.add(randIdx);
		}
		return availReplicas;
	}

	private long getTransactionID() {
		return ++transactionId;
	}

	private long getTimeStamp() {
		return ++timeStamp;
	}

	@Override
	public ReplicaLoc[] getReplicaServersLocs(String fileName)
			throws RemoteException {
		return filesMap.get(fileName);
	}

}
