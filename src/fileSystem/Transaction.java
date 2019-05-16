package fileSystem;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Transaction {
	private final long ID;
	private final String fileName;
	private final String directory;
	private StringBuilder buffer;
	private int numOfMsgs;

	public Transaction(long ID, String fileName, String directory) {
		this.ID = ID;
		this.fileName = fileName;
		this.directory = directory;
		numOfMsgs = 0;
		buffer = new StringBuilder(1024 * 8);
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
		buffer.append(data.getContent());
		return ++numOfMsgs;
	}

	public FileContent read(String fileName) throws IOException {
		if (!fileName.equals(fileName))
			throw new IOException("Not same file of the transaction");

		String data = "";
		if (Files.exists(Paths.get(directory + "/" + fileName)))
			data = new String(
					Files.readAllBytes(Paths.get(directory + "/" + fileName)));
		data += buffer;

		if (data.isEmpty())
			throw new FileNotFoundException("This file is not found");

		return new FileContent(fileName, data);
	}

	public boolean commit(long numOfMsgs) throws IOException {
		if (this.numOfMsgs != numOfMsgs)
			return false;

		Files.write(Paths.get(directory + "/" + fileName),
				buffer.toString().getBytes(), StandardOpenOption.CREATE,
				StandardOpenOption.APPEND, StandardOpenOption.WRITE);
		return true;
	}

	public boolean abort() throws IOException {
		return true;
	}

	public static void main(String[] args) throws IOException {
		String f = "test.txt", d = "server_1";
		new File(d).mkdirs();
		Transaction t = new Transaction(1, f, d);
		System.out.println(t.read(f).getContent());
		t.write(0, new FileContent(f, "abcd"));
		System.out.println(t.read(f).getContent());
		t.write(1, new FileContent(f, "efgh"));
		System.out.println(t.read(f).getContent());
		t.commit(2);
	}
}

