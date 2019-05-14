import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MasterServerClientImpl extends UnicastRemoteObject implements MasterServerClientInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, FileReplicaServer> filesMap;

	private long transactionId = 0;
	private long timeStamp = 0;
	private int numberFileReplica;
	private Random rnd;
	private ReplicaLoc[] allAvailReplicas;

	protected MasterServerClientImpl(String[] hosts, int[] ports, int numberFileReplica) throws RemoteException {
		super();
		
		assert numberFileReplica < hosts.length;
		
		allAvailReplicas = new ReplicaLoc[hosts.length];
		for (int i = 0; i < hosts.length; i++) {
			allAvailReplicas[i] = new ReplicaLoc(hosts[i], ports[i]);
		}
		this.numberFileReplica = numberFileReplica;
		rnd = new Random(0);
		filesMap = new HashMap<>();
	}

	@Override
	public FileReplicaServer read(String fileName) throws FileNotFoundException, IOException, RemoteException {
		if (!filesMap.containsKey(fileName)) {
			throw new FileNotFoundException();
		}
		return filesMap.get(fileName);
	}

	@Override
	public synchronized WriteMsg write(FileContent data) throws RemoteException, IOException {
		String fileName = data.getFileName();
		if (filesMap.containsKey(fileName)) {
			FileReplicaServer fileRep = filesMap.get(fileName);
			ReplicaLoc primaryLoc = fileRep.getFileReplicas()[fileRep.getPrimaryReplica()];
			return new WriteMsg(getTransactionID(), getTimeStamp(), primaryLoc);
		}

		filesMap.put(fileName, new FileReplicaServer(rnd.nextInt(numberFileReplica), getAvailReplica()));
		return null;
	}

	private ReplicaLoc[] getAvailReplica() {
		ReplicaLoc[] availReplicas = new ReplicaLoc[numberFileReplica];
		Set<Integer> visitedRnd = new HashSet<>();
		for (int i = 0; i < numberFileReplica; i++) {
			int randIdx = rnd.nextInt(allAvailReplicas.length);
			while (visitedRnd.contains(randIdx)) {
				randIdx = rnd.nextInt(allAvailReplicas.length);
			}
			availReplicas[i] = allAvailReplicas[randIdx];
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
