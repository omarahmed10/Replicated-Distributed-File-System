import java.io.Serializable;

public class WriteMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long transactionId;
	private long timeStamp;
	private ReplicaLoc loc;

	public WriteMsg(long transactionId, long timeStamp, ReplicaLoc loc) {
		this.setLoc(loc);
		this.setTimeStamp(timeStamp);
		this.setTransactionId(transactionId);
	}

	/**
	 * @return the transactionId
	 */
	public long getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId
	 *            the transactionId to set
	 */
	private void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return the timeStamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp
	 *            the timeStamp to set
	 */
	private void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the loc
	 */
	public ReplicaLoc getLoc() {
		return loc;
	}

	/**
	 * @param loc
	 *            the loc to set
	 */
	private void setLoc(ReplicaLoc loc) {
		this.loc = loc;
	}

}
