package replica;

import java.io.IOException;
import java.rmi.NotBoundException;

public class ReplicaServer extends ReplicaServerImpl{

	public ReplicaServer(ReplicaLoc replicaLoc) {
		super(replicaLoc);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param args
	 *            : 0 is the Replica ID <br>
	 *            : 1 is the host name <br>
	 *            : 2 is the port number <br>
	 * @throws NumberFormatException
	 * @throws NotBoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws NumberFormatException, NotBoundException, IOException {
		if (args.length < 3) {
			System.out.println("start command has to be <Replica ID> <host name> <port number>");
			System.exit(-1);
		}

	}
}
