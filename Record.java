import java.net.*;
import java.util.concurrent.atomic.*;

public class Record {
	
	private final int ID;
	private final Socket downloadSocket;
	private final Socket uploadSocket;
	private final Socket haveSocket;
	private volatile int download;
	//state = 0: choke
	//state = 1: unchoke
	//state = 2: optUnchoke
	private AtomicInteger state;
	private BitField bitField;
	
	//all the method of Record is atomic
	//when using two or more methods of Record at the same time
	//those methods needs to be synchronized in a block using the 
	//Record instance as the lock
	public Record(int numPiece, int ID, Socket socket1, Socket socket2, Socket socket3) {
		
		this.ID = ID;
		downloadSocket = socket1;
		uploadSocket = socket2;
		haveSocket = socket3;
		download = 0;
		state = new AtomicInteger(0);
		bitField = new BitField(numPiece);
		
	}
	
	public boolean isFinished() {
		return bitField.isFinished();
	}
	
	public int getID() {
		return ID;
	}
	
	//increase the download data amount
	public void incDownload() {
		download++;
	}
	
	public void clearDownload() {
		download = 0;
	}
	
	public int getDownload() {
		return download;
	}

	public void turnOnBit(int which) {
		
		bitField.turnOnBit(which);
	}
	
	public BitField getBitField() {
		return bitField;
	}

	public void setBitField(BitField bitField) {
		this.bitField = bitField;
	}
	
	public Socket getDownloadSocket() {
		return downloadSocket;
	}
	
	public Socket getUploadSocket() {
		return uploadSocket;
	}
	
	public Socket getHaveSocket() {
		return haveSocket;
	}

	public AtomicInteger getState() {
		return state;
	}
	
}
