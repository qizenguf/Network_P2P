import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.*;

public class MyLogger {
	
	private FileHandler logFileHandler;
	private SimpleFormatter formatter;
	private Logger logger; 
	private int myID;
	
	public MyLogger(int peerID) throws SecurityException, IOException {
		myID = peerID;
		logger = Logger.getLogger("Peer" + myID);
		logger.setLevel(Level.INFO);
		logFileHandler = new FileHandler("log_peer_" + peerID + ".log");
		formatter = new SimpleFormatter();
		logFileHandler.setFormatter(formatter);
		logger.addHandler(logFileHandler);				
	}
//	private String currentTime() {
//		return new SimpleDateFormat("yyyyMMdd_HHmmsss").format(Calendar.getInstance().getTime());
//	}
	
	public void TCPConnToLog(int peerID) {
		logger.info(":Peer " + myID + " makes a connection to Peer " + peerID + "\n");
	}
	public void TCPConnFromLog(int peerID) {
		logger.info(":Peer " + myID + " is connected from Peer " + peerID + "\n"); 
	}
	
	public void changePrefLog(ArrayList<Integer> prefNeighbors) {
		
		StringBuffer s = new StringBuffer();
		s.append(":Peer " + myID + " has the preferred neighbores ");
		
		for (int i = 0; i < prefNeighbors.size(); i++) {
			if (i != (prefNeighbors.size() - 1)) {
				s.append(prefNeighbors.get(i) + ", ");
			} else {
				s.append(prefNeighbors.get(i) + "\n");
			}
		}
		
		logger.info(s.toString());
	}
	
	public void changeOptLog(int peerID) {
		logger.info(": Peer " + myID + " has the optimistically unchoked neighbor " + peerID + "\n");
	}
	
	public void unchokingLog(int peerID) {
		logger.info(": Peer " + myID + " is unchoked by " + peerID + "\n");
	}
	
	public void choking(int peerID) {
		logger.info(": Peer " + myID + " is choked by " + peerID + "\n");
	}
	
	public void haveLog(int peerID, int pieceIndex) {
		logger.info(": Peer " + myID + " receives a 'have' message from " + peerID + " for the piece " + pieceIndex + "\n");
	}
	
	public void interestedLog(int peerID) {
		logger.info(": Peer " + myID + " receives the 'interested' message from " + peerID + "\n");
	}
	
	public void notInterestedLog(int peerID) {
		logger.info(": Peer " + myID + " receives the 'not interested' message from " + peerID + "\n");
	}
	
	public void downloadLog(int peerID, int pieceIndex) {
		logger.info(": Peer " + myID + " has downloaded the piece " + pieceIndex + " from " + peerID + "\n"); 
	}
	
	public void compDownloadLog() {
		logger.info(": Peer " + myID + " has downloaded the complete file \n");
	}
}
