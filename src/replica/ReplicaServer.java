package replica;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ReplicaServer extends ReplicaServerImpl {

	public ReplicaServer(ReplicaLoc replicaLoc, String ID, String masterHost, int masterPort) throws IOException {
		super(replicaLoc, ID, masterHost, masterPort);
	}

	/**
	 * 
	 * @param args
	 *            : 0 is the replica ID <br>
	 *            : 1 is the replica host name <br>
	 *            : 2 is the replica port number <br>
	 *            : 3 is the master host name <br>
	 *            : 4 is the master port number <br>
	 * @throws NumberFormatException
	 * @throws NotBoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws NumberFormatException, NotBoundException, IOException {
		if (args.length != 5) {
			System.out.println(
					"Usage : java ReplicaServer <replica ID> <replica host name> <replica port number> <master host name> <master port number>");
			System.exit(-1);
		}
//		ReplicaLoc myLoc = new ReplicaLoc(args[1], Integer.parseInt(args[2]));
//		ReplicaServerClientInterface replicaAPI = new ReplicaServer(myLoc);
//
//		Registry registry = LocateRegistry.getRegistry(myLoc.getHost(), myLoc.getPort());
//		registry.rebind("replicaAPI", replicaAPI);
	}
}
