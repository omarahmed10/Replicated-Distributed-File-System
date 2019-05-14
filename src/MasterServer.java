

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MasterServer extends MasterServerClientImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PROPERTIES_FILENAME = System.getProperty("user.dir") + "/system.properties";
	private static Map<String, Object> prop;

	protected MasterServer(ReplicaLoc[] replicaServers, int replicasNum) throws RemoteException {
		super(replicaServers, replicasNum);
	}

	private static Map<String, Object> readPropertiesFile() {
		System.out.println(PROPERTIES_FILENAME);
		Queue<String> input;
		Map<String, Object> prop = new HashMap<String, Object>();
		// read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(PROPERTIES_FILENAME))) {
			input = stream.collect(Collectors.toCollection(LinkedList::new));
			String[] server = input.poll().split(":");
			prop.put("server name", server[0]);
			prop.put("serverIP", server[1]);
			prop.put("port", server[2]);

			int numberOfReplicas = Integer.parseInt(input.poll());
			prop.put("numberOfReplicas", numberOfReplicas);
			for (int i = 1; i < numberOfReplicas + 1; i++) {
				prop.put("R" + i, input.poll());
			}
			
			prop.put("numberOfFileReplicas", Integer.parseInt(input.poll()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return prop;
	}

	private static ReplicaLoc[] startReplicaServers() throws IOException {
		String path = System.getProperty("user.dir");
		int numberOfReplicas = (int) prop.get("numberOfReplicas");
		ReplicaLoc[] allReplicas = new ReplicaLoc[numberOfReplicas];

		for (int i = 1; i < numberOfReplicas + 1; i++) {
			String[] server = prop.get("R" + i).toString().split(":");

			int port = Integer.parseInt(server[2]);
//			SSh ssh = new SSh(server[0], server[1]);
//			ssh.runCommand(" cd " + path + "/bin/replica; java ReplicaServer " + i + " " + server[1] + " " + server[2]);

			allReplicas[i - 1] = new ReplicaLoc(server[1], port);
		}
		return allReplicas;
	}

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		prop = readPropertiesFile();
		ReplicaLoc[] allReplicas = startReplicaServers();

		MasterServerClientImpl masterAPI = new MasterServer(allReplicas, (int) prop.get("numberOfFileReplicas"));

		Registry registry = LocateRegistry.getRegistry(prop.get("serverIP").toString(),
				Integer.parseInt(prop.get("port").toString()));
		registry.rebind("masterAPI", masterAPI);
	}
}
