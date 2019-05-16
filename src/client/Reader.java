package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import fileSystem.FileContent;
import fileSystem.ReadMsg;
import master.MasterServerClientInterface;
import replica.ReplicaLoc;
import replica.ReplicaServerClientInterface;

public class Reader {

	/**
	 * 
	 * @param args
	 *            0 is the client ID <br>
	 *            1 is the host name of the master server <String> <br>
	 *            2 is the port number of the master server <integer><br>
	 *            3 is the File name to be read. <br>
	 *            4 is the number of times the client will try to read.
	 * @throws NumberFormatException
	 * @throws NotBoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args)
			throws NumberFormatException, IOException, NotBoundException, InterruptedException {
		if (args.length < 5) {
			System.out.println("start command has to be <host name> <port number> <file name> <iterations>");
			System.exit(-1);
		}
		String id = args[0];
		String host = args[1];
		int port = Integer.parseInt(args[2]);
		String fileName = args[3];
		int iterations = Integer.parseInt(args[4]);

		Random rnd = new Random(0);

		new File(System.getProperty("user.dir") + "/Readers/").mkdirs();
		String path = System.getProperty("user.dir") + "/Readers/" + fileName + "_" + id + ".txt";
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(path));

		Registry masterRegistry = LocateRegistry.getRegistry(host, port);
		MasterServerClientInterface masterStub = (MasterServerClientInterface) masterRegistry.lookup("masterAPI");

		try {
			ReadMsg readMsg = masterStub.read(fileName);
			ReplicaLoc primaryReplica = readMsg.getLoc();
			long txnID = readMsg.getTransactionId();

			System.out.println("Client " + id + " Connecting to Replica " + primaryReplica + "\n");

			Registry ReplicaRegistry = LocateRegistry.getRegistry(primaryReplica.getHost(), primaryReplica.getPort());
			ReplicaServerClientInterface replicaStub = (ReplicaServerClientInterface) ReplicaRegistry
					.lookup("replicaAPI");

			while (iterations-- > 0) {
				try {
					FileContent fileContent = replicaStub.read(txnID, fileName);
					fileWriter.append(fileContent.getContent() + "\n");
				} catch (IOException e) {
					fileWriter.append(
							"\n........................\nFile is not exist try again later." + e.getMessage() + "\n");
				}
				Thread.sleep(1000);
			}
		} catch (IOException e) {
			fileWriter.append("The file doesn't exist yet.");
		}
		fileWriter.close();
		System.out.println("Client " + id + " reading " + fileName + " is Done");
	}
}
