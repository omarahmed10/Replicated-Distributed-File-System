import java.io.Serializable;

public class FileReplicaServer implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int primaryReplica;
	private ReplicaLoc[] fileReplicas;

	public FileReplicaServer(int primaryReplica, ReplicaLoc[] fileReplicas) {
		this.setPrimaryReplica(primaryReplica);
		this.setFileReplicas(fileReplicas);
	}

	/**
	 * @return the primaryReplica
	 */
	public int getPrimaryReplica() {
		return primaryReplica;
	}

	/**
	 * @param primaryReplica the primaryReplica to set
	 */
	private void setPrimaryReplica(int primaryReplica) {
		this.primaryReplica = primaryReplica;
	}

	/**
	 * @return the fileReplicas
	 */
	public ReplicaLoc[] getFileReplicas() {
		return fileReplicas;
	}

	/**
	 * @param fileReplicas the fileReplicas to set
	 */
	private void setFileReplicas(ReplicaLoc[] fileReplicas) {
		this.fileReplicas = fileReplicas;
	}
}
