import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

public class Unchoked implements Callable<Object>{ 
	
	private Record record;
	private Socket socket;
	private FileManager fileManager;
	private int time;
	private MyLogger logger;
	
	public Unchoked(Record record, FileManager fileManager, int time, MyLogger logger) {
		
		this.record = record;
		this.socket = record.getUploadSocket();
		this.fileManager = fileManager;
		this.time = time;
		this.logger = logger;
		
	}
	
	public Object call() throws IOException {
		
		Message msg = new Message();
		msg.setPayLoad(null);
		msg.setType(Message.UNCHOKE);
		msg.send(socket);
		long start = System.currentTimeMillis();
		
		while (true) {
			
			msg.read(socket);
			if (msg.getType() == Message.NOTINTERESTED) {
				logger.notInterestedLog(record.getID());
				break;
			}
			
			if (msg.getType() == Message.REQUEST) {
				int index = Conversion.BytesToInt(msg.getPayLoad());
				Piece uploadPiece = fileManager.readPiece(index);
				msg.setType(Message.PIECE);
				msg.setPayLoad(uploadPiece.getPieceBytes());
				msg.send(socket);
			}
			
			//check if timeout
			if ((System.currentTimeMillis() - start) > time * 1000) {
				msg.setType(Message.CHOKE);
				msg.setPayLoad(null);
				msg.send(socket);
				break;
			} 
		}
//		System.out.println("Unchoked:55:quit");
		return new Object();
	}

}
