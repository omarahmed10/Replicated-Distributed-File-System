package client;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import fileSytem.FileContent;
import fileSytem.MessageNotFoundException;
import fileSytem.WriteMsg;
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
	 *            : 3 is the content to be written in the file.
	 * @throws NumberFormatException
	 * @throws NotBoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws NumberFormatException, NotBoundException, IOException {
		if (args.length < 4) {
			System.out.println("start command has to be <host name> <port number> <file name> <content>");
			System.exit(-1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String fileName = args[2];
		String content = args[3];
		FileContent wFile = new FileContent(fileName, content);
		int msgSeqNum = 1;

		String path = System.getProperty("user.dir") + "/Readers/" + fileName + ".txt";
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(path));

		Registry masterRegistry = LocateRegistry.getRegistry(host, port);
		MasterServerClientInterface masterStub = (MasterServerClientInterface) masterRegistry.lookup("masterAPI");

		try {
			WriteMsg wMsg = masterStub.write(fileName);
			ReplicaLoc primaryReplica = wMsg.getLoc();

			Registry ReplicaRegistry = LocateRegistry.getRegistry(primaryReplica.getHost(), primaryReplica.getPort());
			ReplicaServerClientInterface replicaStub = (ReplicaServerClientInterface) ReplicaRegistry
					.lookup("replicaAPI");

			replicaStub.write(wMsg.getTransactionId(), msgSeqNum++, wFile);
			try {
				Boolean commitDone =  replicaStub.commit(wMsg.getTransactionId(), msgSeqNum - 1);
				if(commitDone) {
				}else {
					
				}
			} catch (MessageNotFoundException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) { // The file is not on the DFS.
			e.printStackTrace();
		}
		fileWriter.close();
	}
}
