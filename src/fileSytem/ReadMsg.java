package fileSytem;

import java.io.Serializable;

import replica.ReplicaLoc;

public class ReadMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long transactionId;
	private ReplicaLoc loc;

	public ReadMsg(long transactionId, ReplicaLoc loc) {
		this.loc = loc;
		this.transactionId = transactionId;
	}

	/**
	 * @return the transactionId
	 */
	public long getTransactionId() {
		return transactionId;
	}

	/**
	 * @return the loc
	 */
	public ReplicaLoc getLoc() {
		return loc;
	}

}
