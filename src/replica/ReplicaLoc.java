package replica;

import java.io.Serializable;

public class ReplicaLoc implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String host;
	private int port;

	public ReplicaLoc(String host, int port) {
		this.setHost(host);
		this.setPort(port);
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	private void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	private void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return this.host + " " + this.port;
	}
}
