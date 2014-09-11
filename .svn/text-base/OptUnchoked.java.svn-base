import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;

public class OptUnchoked implements Callable<Object>{ 
	
	private BitField myBitField;
	private Record[] neighbors;
	private FileManager fileManager;
	private int time;
	private MyLogger logger;
	private static final int TIMEOFTRY = 10;
	private int myID;

	
	public OptUnchoked(int myID, BitField myBitField, Record[] neighbors, FileManager fileManager, int time, MyLogger logger ) {
		
		this.myID = myID;
		this.myBitField = myBitField;
		this.neighbors = neighbors;
		this.fileManager = fileManager;
        this.time=time;
        this.logger = logger;
	}
	
	public Object call() throws IOException, InterruptedException {
		
//		System.out.println("OptUnchoke:28: peer " + this.myID + " starts opt upload");
		while (true) {
			
			boolean allFinished = true;
			for (int i = 0; i < neighbors.length; i++) {
				if (!neighbors[i].getBitField().isFinished()) {
					allFinished = false;
					break;
				}
			}
			
			if (allFinished) {
				break;
			}
			int optIndex = -1;
			boolean find = false;
			for (int i = 0; i < TIMEOFTRY; i++) {
				optIndex=(int)(Math.random()*neighbors.length);
				if ((neighbors[optIndex].getBitField().getInterestingIndex(myBitField) != -1) && neighbors[optIndex].getState().compareAndSet(0, 2)) {
					find = true; 
					break;
				}
			}
			if (!find) {
				Thread.sleep(1000);
				continue;
			}
			
			
			logger.changeOptLog(neighbors[optIndex].getID());
			Record record = neighbors[optIndex];
			Socket socket = record.getUploadSocket();
			Message msg = new Message();
			msg.setType(Message.UNCHOKE);
			msg.setPayLoad(null);
			msg.send(socket);
//			System.out.println("OptUnchoke:64: peer " + this.myID + " sends unchoke message to the peer " + record.getID());
			long start = System.currentTimeMillis();
			
			while (true) {
				
				msg.read(socket);
//				System.out.println("OptUnchoke:70:peer " + this.myID + " receives a message from peer " + record.getID() + ", message type = " + msg.getType());
				if (msg.getType() == Message.NOTINTERESTED) {
					logger.notInterestedLog(record.getID());
					break;
				}
				
				if (msg.getType() == Message.REQUEST) {
					int index = Conversion.BytesToInt(msg.getPayLoad());
					Piece uploadPiece = fileManager.readPiece(index);
//					System.out.println("OptUnchoke:79: peer " + this.myID + " reads one piece from disk, piece index = " + index);
					msg.setType(Message.PIECE);
					msg.setPayLoad(uploadPiece.getPieceBytes());
					msg.send(socket);
				}
				
				//check if timeout
				if ((System.currentTimeMillis() - start) > time * 1000) {
//					System.out.println("OptUnchoke:87:time out!");
					msg.setType(Message.CHOKE);
					msg.setPayLoad(null);
					msg.send(socket);
					break;
				} 
			}
			
			neighbors[optIndex].getState().compareAndSet(2, 0);
	
 		}
//		System.out.println("OptUnchoke:98:quit:peer " + this.myID);
		return new Object();
		}

}
