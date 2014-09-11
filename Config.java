import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Config {
	
	//common config
	private final int numPreferredNeighbors;
	private final int unChokingInterval;
	private final int optUnChokingInterval;
	private final String fileName;
	private final int fileSize;
	private final int pieceSize;
	
	private final int numPieces;
	
	//peer info
	private final ArrayList<Integer> IDs;
	private final ArrayList<String> addresses;
	private final ArrayList<Integer> downloadPorts;
	private final ArrayList<Boolean> flags;
	
	private final int numPeers;
	private final ArrayList<Integer> uploadPorts;
	private final ArrayList<Integer> havePorts;


	public int getFileSize() {
		return fileSize;
	}


	public int getNumPieces() {
		return numPieces;
	}


	public int getPieceSize() {
		return pieceSize;
	}


	public int getNumPreferredNeighbors() {
		return numPreferredNeighbors;
	}


	public int getUnChokingInterval() {
		return unChokingInterval;
	}


	public int getOptUnChokingInterval() {
		return optUnChokingInterval;
	}


	public String getFileName() {
		return fileName;
	}


	public int getNumPeers() {
		return numPeers;
	}

	
	public int getDownloadPort(int index) {
		return downloadPorts.get(index);
	}


	public int getUploadPort(int index) {
		return uploadPorts.get(index);
	}


	public int getHavePort(int index) {
		return havePorts.get(index);
	}


	public ArrayList<Integer> getIDs() {
		return IDs;
	}


	public ArrayList<String> getAddresses() {
		return addresses;
	}


	public ArrayList<Boolean> getFlags() {
		return flags;
	}


	public Config(String commonCfg, String peerInfo) throws FileNotFoundException {
		
		//read common config
		Scanner in1= new Scanner(new FileReader(commonCfg));
		this.numPreferredNeighbors = Integer.parseInt(in1.nextLine().trim());
		this.unChokingInterval = Integer.parseInt(in1.nextLine().trim());
		this.optUnChokingInterval = Integer.parseInt(in1.nextLine().trim());
		this.fileName = in1.nextLine().trim();
		this.fileSize = Integer.parseInt(in1.nextLine().trim());
		this.pieceSize = Integer.parseInt(in1.nextLine().trim());
		
		if (this.fileSize%this.pieceSize == 0) {
			this.numPieces = this.fileSize/this.pieceSize;
 		} else {
 			this.numPieces = this.fileSize/this.pieceSize + 1;
 		}
		
		in1.close();
		
		//read peer info
		Scanner in2 = new Scanner(new FileReader(peerInfo));
		
		IDs = new ArrayList<Integer>();
		addresses = new ArrayList<String>();
		downloadPorts = new ArrayList<Integer>();
		flags = new ArrayList<Boolean>();
		
		uploadPorts = new ArrayList<Integer>();
		havePorts = new ArrayList<Integer>();
		
		int count = 0;
		while (in2.hasNextLine()) {

			String s = in2.nextLine();
			String[] split = s.split(" ");
			this.IDs.add(Integer.parseInt(split[0].trim()));
			this.addresses.add(split[1].trim());
			this.downloadPorts.add(Integer.parseInt(split[2].trim()));
			this.uploadPorts.add(Integer.parseInt(split[2].trim()) + 1);
			this.havePorts.add(Integer.parseInt(split[2].trim()) + 2);
			if (split[3].trim().equals("1")) {
				this.flags.add(true);
			} else {
				this.flags.add(false);
			}
			count++;
		}
		
		this.numPeers = count;

//		System.out.println("Config 151:config construction finished");
//		System.out.println("numPreferredNeighbors = " + this.numPreferredNeighbors);
//		System.out.println("unChokingInterval = " + this.unChokingInterval);
//		System.out.println("optUnChokingInterval = " + this.optUnChokingInterval );
//		System.out.println("fileName = " + this.fileName);
//		System.out.println("fileSize = " + this.fileSize);
//		System.out.println("pieceSize = " + this.pieceSize);
//		System.out.println("numPiece = " + this.numPieces);
//		System.out.println("numPeers = " + this.numPeers);
//		for (int i = 0; i < this.numPeers; i++) {
//			System.out.println("peer ID = " + this.IDs.get(i));
//			System.out.println("address = " + this.addresses.get(i));
//			System.out.println("download port = " + this.downloadPorts.get(i));
//			System.out.println("upload port = " + this.uploadPorts.get(i));
//			System.out.println("have port = " + this.havePorts.get(i));
//			System.out.println("flag = " + this.flags.get(i));
//		}
		
	}
}
