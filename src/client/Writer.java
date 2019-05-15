package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import fileSystem.FileContent;
import fileSystem.MessageNotFoundException;
import fileSystem.WriteMsg;
import master.MasterServerClientInterface;
import replica.ReplicaLoc;
import replica.ReplicaServerClientInterface;

public class Writer {
	/**
	 * 
	 * @param args
	 *            : 0 is the host name of the master server <String> <br>
	 *            : 1 is the port number of the master server <integer> <br>
	 *            : 2 is the File name to be read. <br>
	 *            : 3 is the content to be written in the file. : 4 is the number of
	 *            write then read.
	 * @throws NumberFormatException
	 * @throws NotBoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws NumberFormatException, NotBoundException, IOException {
		if (args.length < 5) {
			System.out.println(
					"start command has to be <host name> <port number> <file name> <content> < number of write then read>");
			System.exit(-1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String fileName = args[2];
		String content = args[3];
		int iterations = Integer.parseInt(args[4]);

		Random rnd = new Random(0);

		FileContent wFile = new FileContent(fileName, content);
		int msgSeqNum = 1;

		new File(System.getProperty("user.dir") + "/Writers/").mkdirs();
		String path = System.getProperty("user.dir") + "/Writers/" + fileName + ".txt";
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(path));

		Registry masterRegistry = LocateRegistry.getRegistry(host, port);
		MasterServerClientInterface masterStub = (MasterServerClientInterface) masterRegistry.lookup("masterAPI");

		try {
			WriteMsg wMsg = masterStub.write(fileName);
			ReplicaLoc primaryReplica = wMsg.getLoc();

			fileWriter.append("Connecting to Replica " + primaryReplica);

			Registry ReplicaRegistry = LocateRegistry.getRegistry(primaryReplica.getHost(), primaryReplica.getPort());
			ReplicaServerClientInterface replicaStub = (ReplicaServerClientInterface) ReplicaRegistry
					.lookup("replicaAPI");

			while (iterations-- > 0) {
				replicaStub.write(wMsg.getTransactionId(), msgSeqNum++, wFile);
				Boolean commitDone = replicaStub.commit(wMsg.getTransactionId(), msgSeqNum - 1);
				if (commitDone) {
					Thread.sleep(rnd.nextInt(1000) + 1000);
					FileContent fileContent = replicaStub.read(wMsg.getTransactionId(), wFile.getFileName());
					fileWriter.append(fileContent.getContent() + "\n");
				} else {
					fileWriter.append("\n..........................\nFailed ya mokooos");
					break;
				}
			}
		} catch (FileNotFoundException e) { // The file is not on the DFS.
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MessageNotFoundException e) {
			e.printStackTrace();
		}
		fileWriter.close();
	}
}
