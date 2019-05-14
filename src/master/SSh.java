package master;

import java.io.IOException;

public class SSh {
	private Runtime r;
	private String sshPrefix = "";

	// call with $user and 127.0.0.1 to run local command.
	public SSh(String user, String ip) {
		r = Runtime.getRuntime();
		sshPrefix = " ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no " + user + "@" + ip;
	}

	public void runCommand(String command) {
		try {
			// Logger.info("running: " + this.sshPrefix + " " + command);
			r.exec(this.sshPrefix + " " + command);
		} catch (IOException ex) {
			// Logger.error(ex.getLocalizedMessage());
		}
	}

}
