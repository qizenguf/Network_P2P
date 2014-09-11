import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
//stop listening only when receive STOP message
public class HaveListener implements Callable<Object>{
	
	private Record record;
	private MyLogger logger;
	private int myID;

	
	public HaveListener(int myID, Record record, MyLogger logger) {
		this.record = record;
		this.logger = logger;
		this.myID = myID;
	}
	
	public Object call() throws IOException {
		
//		System.out.println("HaveListener:20: peer " + this.myID + " starts to listen have message from peer " + record.getID());
		Socket socket = record.getHaveSocket();
		Message Msg = new Message();
		
		while (true) {
			
			Msg.read(socket);
			if (Msg.getType() == Message.STOP) {
				break;
			} else {
				if (Msg.getType() == Message.HAVE) {
					
					byte[] payLoad = Msg.getPayLoad();
					int index = Conversion.BytesToInt(payLoad);
					record.getBitField().turnOnBit(index);
					logger.haveLog(record.getID(), index);
				}
			}

		}
	
//		System.out.println("HaveListener:41:quit:peer " + this.myID);
		return new Object();
	}
	
	
}
