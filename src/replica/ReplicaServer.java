package replica;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import fileSystem.Log;

public class ReplicaServer extends ReplicaServerImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	public static void main(String[] args) {

		if (args.length != 5) {
			System.out.println(
					"Usage : java ReplicaServer <replica ID> <replica host name> <replica port number> <master host name> <master port number>");
			System.exit(-1);
		}

		Handler fileHandler = null;
		SimpleFormatter simpleFormatter = null;
		try {
			new File(System.getProperty("user.dir") + "/logs/").mkdirs();
			fileHandler = new FileHandler("./logs/ReplicaServer_" + args[0] + ".log");
			simpleFormatter = new SimpleFormatter();

			// Assigning handlers to LOGGER object
			Log.LOGGER.addHandler(fileHandler);
			// Setting levels to handlers and LOGGER
			fileHandler.setLevel(Level.ALL);
			Log.LOGGER.setLevel(Level.ALL);
			// Setting formatter to the handler
			fileHandler.setFormatter(simpleFormatter);
			Log.LOGGER.config("Configuration done.");
		} catch (IOException exception) {
			Log.LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
		}

		Log.LOGGER.info("Logger Name: " + Log.LOGGER.getName() + "_" + args[0] + " working on port " + args[2]);

		ReplicaLoc myLoc = new ReplicaLoc(args[1], Integer.parseInt(args[2]));
		ReplicaServerClientInterface replicaAPI;
		try {
			replicaAPI = new ReplicaServer(myLoc, args[0], args[3], Integer.parseInt(args[4]));
			Registry registry = LocateRegistry.getRegistry(myLoc.getHost(), myLoc.getPort());
			registry.rebind("replicaAPI", replicaAPI);
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			Log.LOGGER.severe(e.getMessage());
		}

		Log.LOGGER.info("Binding the register is done");
	}
}
