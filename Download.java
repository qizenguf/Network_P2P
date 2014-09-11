import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

public class Download implements Callable<Object> {
	
	private Record record;
	private BitField myBitField;
	private Socket socket;
	private FileManager fileManager;
	private Record[] neighbors;
	private MyLogger logger;
	private int myID;
	
	public Download(int myID, Record record, Record[] neighbors, BitField myBitField, FileManager fileManager, MyLogger logger) {
		
		this.myID = myID;
		this.neighbors = neighbors;
		this.record = record;
		this.myBitField = myBitField;
		this.socket = record.getDownloadSocket();
		this.fileManager = fileManager;
		this.logger = logger;
		
	}
	
	public Object call() throws IOException {
		
		Message msg = new Message();
//		System.out.println("Download:30: peer " + this.myID + " prepares to download from peer " + record.getID());
		//jump out the while loop only when receiving STOP message
		while (true) {
//			System.out.println("Download:33: peer " + this.myID + " before receives a message from peer " + record.getID());
			msg.read(socket);
//			System.out.println("Download:35: peer " + this.myID + " receives a message + from peer " + record.getID());
			if (msg.getType() == Message.STOP) {
				for (int i = 0; i < neighbors.length; i++) {
					msg.send(neighbors[i].getHaveSocket());
				}
				break;
			}
			
			else if (msg.getType() == Message.UNCHOKE) {
				//jump out the while loop only when receiving CHOKE message or not interests to the sender
				logger.unchokingLog(record.getID());
				while (true) {
					int interestedPiece = myBitField.getInterestingIndex(record.getBitField());
//					System.out.println("Download:48:get a interested Piece:" + interestedPiece);
					if (interestedPiece == -1) {
						msg.setType(Message.NOTINTERESTED);
						msg.setPayLoad(null);
						msg.send(socket);
						break;
					} else {
						//send REQUEST message
						msg.setType(Message.REQUEST);
						msg.setPayLoad(Conversion.IntToBytes(interestedPiece));
						msg.send(socket);
//						System.out.println("Download:59: sends REQUEST message");
						//download a piece
						msg.read(socket);
//						System.out.println("Download:62:receives a message: type =" + msg.getType());
						
						if (msg.getType() == Message.CHOKE) {
							logger.choking(record.getID());
							break;
						}
						
						Piece downloadPiece = new Piece(interestedPiece, msg.getPayLoad());
						fileManager.writePiece(downloadPiece);
						myBitField.turnOnBit(interestedPiece);
//						System.out.println("Download:64:download a piece: bitField = " + myBitField.getText());
						record.incDownload();
						logger.downloadLog(record.getID(), interestedPiece);
						if (myBitField.isFinished()) {
							logger.compDownloadLog();
						}
								
						//send HAVE message
						msg.setType(Message.HAVE);
						msg.setPayLoad(Conversion.IntToBytes(interestedPiece));
						for (int i = 0; i < neighbors.length; i++) {
							msg.send(neighbors[i].getHaveSocket());
						}
					}
				}
			} 
			
		}
//		System.out.println("Downlaod:90:downlaod quit: peer: " + this.myID);
		return new Object();
	}
}
