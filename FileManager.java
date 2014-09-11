import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class FileManager {

	private Config myConfig;
	private RandomAccessFile file;

	public FileManager(Config myConfig ,int peerID) throws FileNotFoundException {
		
		this.myConfig = myConfig;
		String directory =  "peer_" + peerID + "/";
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdirs();
			}
		file = new RandomAccessFile(directory + myConfig.getFileName(), "rw");	
	}

	//read a piece from the file, which starts from 0;
	public synchronized Piece readPiece(int which) throws IOException {
		int length = 0;
		if (which == myConfig.getNumPieces() - 1) {
			length = myConfig.getFileSize() - myConfig.getPieceSize() * which;
		} else {
			length = myConfig.getPieceSize();
		}
		int offSet = which*myConfig.getPieceSize();
		byte[] bytes = new byte[length];
//		System.out.println("FileManager:33:file length = " + file.length() + ", file pointer = " + file.getFilePointer());
//		System.out.println("FileManager:33:before read one piece from disk: piece index = " + which + ", length = " + length + ", offSet = " + offSet);
//		System.out.println("test read byte:"+ file.read());
		file.seek(offSet);
		for (int i = 0; i < length; i++) {
			bytes[i] = file.readByte();
		}
//		file.readFully(bytes, offSet, length);
//		System.out.println("FileManager:33:after read one piece from disk: piece index = " + which + ", length = " + length + ", offSet = " + offSet);
		Piece piece = new Piece(which, bytes);
		return piece;
		
		}
    //store the piece
	
	public synchronized void writePiece(Piece piece) throws IOException {
		
		int offSet = piece.getWhichPiece()*myConfig.getPieceSize();
		int length = piece.getPieceBytes().length;
//		System.out.println("FileManager:47:Before writing a piece: piece length = " + piece.getPieceBytes().length);
		byte[] tempByte = piece.getPieceBytes();
		file.seek(offSet);
		for (int i = 0; i < length; i++) {
			file.writeByte(tempByte[i]);
		}
//		System.out.println("FileManager:49:After writing a piece");
		}
	
	}

