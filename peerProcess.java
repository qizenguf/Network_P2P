import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public class peerProcess implements Runnable{
	
	//the peerIndex hash map maps the ID to the index 
	//of corresponding record in the neighbors table
	private Config config;
	private FileManager fileManager;
	private MyLogger logger;
	private BitField myBitField;
	private int myID;
	private Record[] neighbors;
	private int numNeighbors;

	public peerProcess(int myID) throws UnknownHostException, IOException {
		
		this.myID = myID;
		this.config = new Config("Common.cfg", "PeerInfo.cfg");
		this.fileManager = new FileManager(this.config, this.myID);
		this.logger = new MyLogger(myID);
		this.myBitField = new BitField(config.getNumPieces());
		this.numNeighbors = config.getNumPeers() - 1;
		neighbors = new Record[numNeighbors];
//		System.out.println("peerProecess 28: peerProcess construction finished");
	}
	
	public void initialization(Record record) throws Exception {
		
//		System.out.println("peerProcess:33: peer " + this.myID + " starts initialization");
		Socket socket = record.getUploadSocket();
		HandShakeMsg shake = new HandShakeMsg();
		shake.setID(myID);
		shake.send(socket);
		
		shake.read(socket);
		
		if (shake.getID() != record.getID()) {
			throw new Exception("Hand shaking fails");
		}
//		shake.read(record.getDownloadSocket());
//		System.out.println("peerProcess:45: peer " + this.myID + " receives test hand shake message");
		Message bitFieldMsg = new Message();
		//send bit field
		bitFieldMsg.setType(Message.BITFIELD);
		bitFieldMsg.setPayLoad(record.getBitField().toBytes());
		bitFieldMsg.send(socket);
		
		//receive bit field
		bitFieldMsg.read(socket);
		BitField bitField = new BitField(config.getNumPieces());
		bitField.setBitField(bitFieldMsg.getPayLoad());
		record.setBitField(bitField);
		
		Message interest = new Message();
		//send interest or not
		interest.setPayLoad(null);
		if (myBitField.getInterestingIndex(bitField) != -1) {
			interest.setType(Message.INTERESTED);
		} else {
			interest.setType(Message.NOTINTERESTED);
		}
		interest.send(socket);
		//receive interest or not
		interest.read(socket);
		if (interest.getType() == Message.INTERESTED) {
			logger.interestedLog(record.getID());
		} else {
			logger.notInterestedLog(record.getID());
		}
//		System.out.println("peerPrecess: 71:peer " + this.myID + " finishes initialization");
		
	}
	
	public void getInitialization(int nextIndex, ServerSocket downloadServSoc, ServerSocket uploadServSoc, 
			
			ServerSocket haveServSoc) throws IOException {
		
		
		Socket downloadSocket = downloadServSoc.accept(); 
		Socket uploadSocket = uploadServSoc.accept();
		Socket haveSocket = haveServSoc.accept();
		
		HandShakeMsg shake = new HandShakeMsg();
		
		shake.read(downloadSocket);
//		System.out.println("peerProcess: 89: peer " + this.myID + " receives hand shake message");
//		shake.send(uploadSocket);
//		System.out.println("peerProcess: 91: peer " + this.myID + " send hand shake message for test");
		int ID = shake.getID();
		neighbors[nextIndex] = new Record(config.getNumPieces(), ID, downloadSocket, uploadSocket, haveSocket);
		//send hand shake message
		shake.setID(myID);
		shake.send(downloadSocket);
		
		
		Message bitFieldMsg = new Message();
		
		//receive bit field
		bitFieldMsg.read(downloadSocket);
		BitField bitField = new BitField(config.getNumPieces());
		bitField.setBitField(bitFieldMsg.getPayLoad());
		neighbors[nextIndex].setBitField(bitField);
		
		//send bit field
		bitFieldMsg.setType(Message.BITFIELD);
		bitFieldMsg.setPayLoad(myBitField.toBytes());
		bitFieldMsg.send(downloadSocket);
		
		Message interest = new Message();
		//receive interest or not
		interest.read(downloadSocket);
		if (interest.getType() == Message.INTERESTED) {
			logger.interestedLog(ID);
		} else {
			logger.notInterestedLog(ID);	
		}
		//send interest or not
		interest.setPayLoad(null);
		if (myBitField.getInterestingIndex(neighbors[nextIndex].getBitField()) != -1) {
			interest.setType(Message.INTERESTED);
		} else {
			interest.setType(Message.NOTINTERESTED);
		}
		interest.send(downloadSocket);

		
	}
	
	public void startUpload() throws InterruptedException, ExecutionException, IOException {
//		System.out.println("peerProcess:130: peer " + this.myID + " starts to upload");
		ArrayList<Record> sortedInterestingPeers = new ArrayList<Record>();
		ExecutorService uploadThreadPool = Executors.newFixedThreadPool(config.getNumPreferredNeighbors());
		
		while (true) {
			
			sortedInterestingPeers.clear();
			int numFinished = 0;
			
			for (int i = 0; i < numNeighbors; i++) {
				neighbors[i].getState().compareAndSet(1, 0);
				if (neighbors[i].getBitField().isFinished()) {
//					System.out.println("peerProcess:142: peer " + this.myID + ": my neighbor peer " + neighbors[i].getID() + " is finished, bitField = " + neighbors[i].getBitField().getText());
					numFinished++;
				}
			}
			
			if (numFinished == numNeighbors) {
				Message msg = new Message();
				msg.setType(Message.STOP);
				msg.setPayLoad(null);
				for (int i = 0; i < numNeighbors; i++) {
					msg.send(neighbors[i].getUploadSocket());
				}
				break;
			}

//			System.out.println("peerPrcess:156: peer " + this.myID + " starts to find uploading neighbors");
			for (int i = 0; i < numNeighbors; i++) {
				
				if ((neighbors[i].getBitField().getInterestingIndex(myBitField) != -1)) {
//					System.out.println("peerProcess:160: peer " + this.myID + ": my neighbor peer " + neighbors[i].getID() + " is interested to me");
					sortedInterestingPeers.add(neighbors[i]);
				}
				if (sortedInterestingPeers.size() == 1) {
					continue;
				}
				int j = i - 1;
				int me = i;
				
				while (j >= 0 && sortedInterestingPeers.get(me).getDownload() > sortedInterestingPeers.get(j).getDownload()) {
					
					Record temp = sortedInterestingPeers.get(me);
					sortedInterestingPeers.set(me, sortedInterestingPeers.get(j));
					sortedInterestingPeers.set(j, temp);
					me = j;
					j--;
				}
				
			}
			if (sortedInterestingPeers.size() == 0) {
				Thread.sleep(1000);
				continue;
			}
			
			for (Record r : neighbors) {
				r.clearDownload();
			}
			
			int count = 0;
			ArrayList<Future<Object>> uploadResults = new ArrayList<Future<Object>>();
			ArrayList<Integer> prefN = new ArrayList<Integer>();
			for (Record r : sortedInterestingPeers) {
				count ++;
				if (count <= config.getNumPreferredNeighbors() && r.getState().compareAndSet(0, 1)) {
//					System.out.println("peerProcess:199: peer " + this.myID + " starts to upload to peer " + r.getID());
					Future<Object> f = uploadThreadPool.submit(new Unchoked(r, fileManager, config.getUnChokingInterval(), logger));
					uploadResults.add(f);
					prefN.add(r.getID());
				} 
				
			}
			
			if (prefN.size() > 0) {
				logger.changePrefLog(prefN);
			}
			for (Future<Object> f : uploadResults) {
				f.get();
			}
			
			
			
			
		}
		
//		uploadThreadPool.shutdownNow();
	}
	
	public void run() {
		try {
			int myIndex = -1;
			for (int i = 0; i < config.getNumPeers(); i ++) {
				myIndex++;
				if (config.getIDs().get(i) == myID) {
					if (config.getFlags().get(i)) {
//						System.out.println("peerPrcess:223: peer " + this.myID + " is finished");
						myBitField.turnOnAll();
					}
					break;
				}
//				System.out.println("peerProcess: 228: peer " + this.myID + " tries to connect");
				Socket socket1 = new Socket(config.getAddresses().get(i), config.getDownloadPort(i));		
				Socket socket2 = new Socket(config.getAddresses().get(i), config.getUploadPort(i));
				Socket socket3 = new Socket(config.getAddresses().get(i), config.getHavePort(i));
//				System.out.println("peerPrecess: 232: peer " + this.myID  + " finishes connecting to perivious peer " + config.getIDs().get(i));
				logger.TCPConnToLog(config.getIDs().get(i));
				neighbors[i] = new Record(config.getNumPieces(),  config.getIDs().get(i), socket2, socket1, socket3);
				initialization(neighbors[i]);
			}
			
			ServerSocket downloadServSoc = null;
			ServerSocket uploadServSoc = null;
			ServerSocket haveServSoc = null;
			if (myIndex != config.getNumPeers() - 1) {
				
//				System.out.println("peerProcess: 243: peer " + this.myID +  " starts to listern to the port");
				downloadServSoc = new ServerSocket(config.getDownloadPort(myIndex));
				uploadServSoc = new ServerSocket(config.getUploadPort(myIndex));
				haveServSoc = new ServerSocket(config.getHavePort(myIndex));
				
				for (int i = myIndex; i < config.getNumPeers() - 1; i++) {
					getInitialization(i, downloadServSoc, uploadServSoc, haveServSoc);
				}
//				System.out.println("peerProcess: 251: peer " + this.myID + " finishes getInitialization");
			}

			ExecutorService downloadThreadPool = Executors.newFixedThreadPool(numNeighbors);
			ArrayList<Future<Object>> downloadResults = new ArrayList<Future<Object>>();
			
			//start opt unchoking upload
			ExecutorService OptUpload = Executors.newSingleThreadExecutor();
			Future<Object> OptUploadResult = OptUpload.submit(new OptUnchoked(this.myID, myBitField, neighbors, fileManager, config.getOptUnChokingInterval(), logger));
			
			ExecutorService haveThreadPool = Executors.newFixedThreadPool(numNeighbors);
			ArrayList<Future<Object>> haveResults = new ArrayList<Future<Object>>();
			
			//start download and have listening
			for (int i = 0; i < numNeighbors; i++) {
				Record r = neighbors[i];
				Future<Object> f1 =  downloadThreadPool.submit(new Download(this.myID, r, neighbors, myBitField, fileManager, logger));
				downloadResults.add(f1);
				Future<Object> f2 = haveThreadPool.submit(new HaveListener(this.myID, r, logger));
				haveResults.add(f2);
			}
				
//			System.out.println("peerProcess:278: peer " + this.myID + " finishes all initialization:");
//			System.out.println("peer " + this.myID + ": bitField: " + this.myBitField.getText());
//			for (int i = 0; i < numNeighbors; i++) {
//				System.out.println("peer " + this.myID + ": neighbors: " + neighbors[i].getID() + ", download from port: " + neighbors[i].getDownloadSocket().getPort() + ", upload to port: " + neighbors[i].getUploadSocket().getPort());
//			}
			//start upload
			//blocking method
			//return when upload finishes
			startUpload();
//			System.out.println("peerProcess:287:finish upload: peer: " + this.myID);
			//waiting for all download stop
			for (int i = 0; i < numNeighbors; i++) {
				
				downloadResults.get(i).get();
				haveResults.get(i).get();
				
			}
			OptUploadResult.get();
//			System.out.println("peerProcess:297: before shutdown everything: peer " + this.myID);
//			downloadThreadPool.shutdownNow();
//			OptUpload.shutdownNow();
//			System.out.println("peerProcess:297: after shutdown everything: peer " + this.myID);
			if (myIndex != config.getNumPeers() - 1) {
				downloadServSoc.close();
				uploadServSoc.close();
				haveServSoc.close();
			} 

			for (int i = 0; i < numNeighbors; i++) {
				neighbors[i].getDownloadSocket().close();
				neighbors[i].getUploadSocket().close();
				neighbors[i].getHaveSocket().close();
			}
			
//			System.out.println("peerProecess:313: peerProcess quit: peer " + this.myID);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
//	public static void main(String args[]) throws Exception {
//		
//		peerProcess peer = new peerProcess(Integer.parseInt(args[0]));
//		peer.start();
//	}
}