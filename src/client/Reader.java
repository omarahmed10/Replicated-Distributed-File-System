package client;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import fileSytem.FileContent;
import master.MasterServerClientInterface;
import replica.ReplicaLoc;
import replica.ReplicaServerClientInterface;

public class Reader {

	/**
	 * 
	 * @param args
	 *            0 is the host name of the master server <String> <br>
	 *            1 is the port number of the master server <integer><br>
	 *            2 is the File name to be read.
	 * @throws NumberFormatException
	 * @throws NotBoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws NumberFormatException, NotBoundException, IOException {
		if (args.length < 3) {
			System.out.println("start command has to be <host name> <port number> <file name>");
			System.exit(-1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String fileName = args[2];

		String path = System.getProperty("user.dir") + "/Readers/" + fileName + ".txt";
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(path));

		Registry masterRegistry = LocateRegistry.getRegistry(host, port);
		MasterServerClientInterface masterStub = (MasterServerClientInterface) masterRegistry.lookup("masterAPI");

		try {
			ReplicaLoc primaryReplica = masterStub.read(fileName);

			Registry ReplicaRegistry = LocateRegistry.getRegistry(primaryReplica.getHost(), primaryReplica.getPort());
			ReplicaServerClientInterface replicaStub = (ReplicaServerClientInterface) ReplicaRegistry
					.lookup("replicaAPI");

			FileContent fileContent = replicaStub.read(fileName);
			fileWriter.write(fileContent.getContent());

		} catch (FileNotFoundException e) { // The file is not on the DFS.
			e.printStackTrace();
		}
		fileWriter.close();
	}
}
