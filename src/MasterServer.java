import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MasterServer extends UnicastRemoteObject implements MasterServerClientInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, ReplicaServer[]> filesMap;

	private long transactionId = 0;
	private long timeStamp = 0;
	private int replicasNum;
	private Random rnd;
	private ReplicaServer[] allReplicaServers;

	protected MasterServer(String[] hosts, int[] ports, int replicasNum) throws RemoteException {
		super();

		assert replicasNum <= hosts.length;

		allReplicaServers = new ReplicaServer[hosts.length];
		for (int i = 0; i < hosts.length; i++) {
			allReplicaServers[i] = new ReplicaServer(new ReplicaLoc(hosts[i], ports[i]));
		}
		this.replicasNum = replicasNum;
		rnd = new Random(0);
		filesMap = new HashMap<>();
	}

	@Override
	public ReplicaLoc read(String fileName) throws FileNotFoundException, IOException, RemoteException {
		if (!filesMap.containsKey(fileName)) {
			throw new FileNotFoundException();
		}
		return filesMap.get(fileName)[0].getReplicaLoc();
	}

	@Override
	public synchronized WriteMsg write(String fileName) throws RemoteException, IOException {

		if (!filesMap.containsKey(fileName))
			filesMap.put(fileName, getAvailReplica());

		ReplicaLoc primaryLoc = filesMap.get(fileName)[0].getReplicaLoc();

		return new WriteMsg(getTransactionID(), getTimeStamp(), primaryLoc);
	}

	private ReplicaServer[] getAvailReplica() {
		ReplicaServer[] availReplicas = new ReplicaServer[replicasNum];
		Set<Integer> visitedRnd = new HashSet<>();
		for (int i = 0; i < replicasNum; i++) {
			int randIdx = rnd.nextInt(allReplicaServers.length);
			while (visitedRnd.contains(randIdx)) {
				randIdx = rnd.nextInt(allReplicaServers.length);
			}
			availReplicas[i] = allReplicaServers[randIdx];
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

}
