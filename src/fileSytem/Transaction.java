package fileSytem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Transaction {
	private final long ID;
	private final String fileName;
	private BufferedWriter writer;
	private int numOfMsgs;
	private String hidden;

	public Transaction(long ID, String fileName) {
		this.ID = ID;
		this.fileName = fileName;
		numOfMsgs = 0;
		if (!Files.exists(Paths.get(fileName)))
			hidden = ".";
		else
			hidden = "";
	}

	public long getID() {
		return ID;
	}

	public String getFileName() {
		return fileName;
	}

	public int write(long msgSeqNum, FileContent data) throws IOException {
		if (!data.getFileName().equals(fileName))
			throw new IOException("Not same file of the transaction");
		if (writer == null)
			writer = new BufferedWriter(
					new FileWriter(hidden + fileName, true));
		writer.append(data.getContent());
		return ++numOfMsgs;
	}

	public FileContent read(String fileName) throws IOException {
		return new FileContent(fileName,
				new String(Files.readAllBytes(Paths.get(fileName))));
	}

	public boolean commit(long numOfMsgs) throws IOException {
		if (this.numOfMsgs != numOfMsgs)
			return false;

		writer.close();

		new File(fileName)
				.renameTo(new File(fileName.substring(hidden.length())));

		return true;
	}

	public boolean abort() throws IOException {
		if (!hidden.isEmpty())
			Files.delete(Paths.get(hidden + fileName));
		return true;
	}
}
