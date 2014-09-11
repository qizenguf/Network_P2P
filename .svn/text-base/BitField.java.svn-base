public class BitField {
	
	private boolean finished;
	private int numPiecesDowned;
	private boolean[] bitField;
	private final int numPieces;
	
	public BitField(int numPieces) {
		
		finished = false;
		numPiecesDowned = 0;
		this.numPieces = numPieces;
		bitField = new boolean[numPieces];
		for (int i = 0; i < numPieces; i++) {
			bitField[i] = false;
		}
	}
	public synchronized void turnOnBit(int which) {
		
		if (bitField[which] == false) {
			bitField[which] = true;
			numPiecesDowned++;
			if (numPiecesDowned == numPieces) {
				finished = true;
			}
		}

	}
	
	public synchronized void turnOnAll() {
		for (int i = 0; i < numPieces; i++) {
			bitField[i] = true;
		}
		numPiecesDowned = numPieces;
		finished = true;
	}
	
	public synchronized boolean isFinished() {
		return finished;
	}

	public synchronized byte[] toBytes() {
		
		int numBytes;
		if (numPieces%8 == 0 ) {
			numBytes = numPieces/8;
		} else {
			numBytes = numPieces/8 + 1;
		}
		
		byte[] bytes = new byte[numBytes];
		for (int i = 0; i < numBytes; i++) {
			bytes[i] = (byte)0;
		}
		for (int i = 0; i < numPieces; i++) {
			int whichByte = i/8;
			int whichBit = i%8;
			if (bitField[i] == true) {
				bytes[whichByte] = (byte) (bytes[whichByte] | (1 << whichBit));
			} else {
				bytes[whichByte] = (byte) (bytes[whichByte] & ~(1 << whichBit));
			}
		}
		
		return bytes;
	}
	
	public synchronized void setBitField(byte[] bytes) {
		
		numPiecesDowned = 0;
		for (int i = 0; i < numPieces; i++) {
			int whichByte = i/8;
			int whichBit = i%8;
			if ((bytes[whichByte] & (1 << whichBit)) == 0) {
				bitField[i] = false;
			} else {
				bitField[i] = true;
				numPiecesDowned++;
			}
		}
		
		if (numPiecesDowned == numPieces) {
			finished = true;
		}
	} 
 	//return - 1 if not interested
	public synchronized int getInterestingIndex(BitField b) {
		int index = -1 ;
		for (int i = 0; i < numPieces; i++) {
			if ((bitField[i] == false) && b.bitField[i] == true) {
				return i;
			} 
		}
		return index;
	}

	public String getText() {
		StringBuffer text = new StringBuffer();
		for (int i = 0 ; i < this.numPieces; i++) {
			if (bitField[i]) {
				text.append("1");
			} else {
				text.append("0");
			}
		}
		return text.toString();
	}
}
