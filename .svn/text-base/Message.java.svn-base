import java.io.*;
import java.net.*;

public class Message {
	
	final static int CHOKE = 0;
	final static int UNCHOKE = 1;
	final static int INTERESTED = 2;
	final static int NOTINTERESTED = 3;
	final static int HAVE = 4;
	final static int BITFIELD = 5;
	final static int REQUEST = 6;
	final static int PIECE = 7;
	final static int STOP = 8;
	
	private int length;
	private int type;
	private byte[] payLoad;
	
	public Message() {
		length = 0;
		type = 0;
		payLoad = null;
	}
	
	public void read(Socket s ) throws IOException {
		
		InputStream in = s.getInputStream();
		byte[] lengthBytes = new byte[4];
		int bytesRcvd;
		int totalBytesRcvd = 0;
		while (totalBytesRcvd < 4) {
			bytesRcvd = in.read(lengthBytes, totalBytesRcvd, 4 - totalBytesRcvd);
			totalBytesRcvd += bytesRcvd;
		}
		length = Conversion.BytesToInt(lengthBytes);
		
		
		byte[] typeBytes = new byte[4];
		totalBytesRcvd = 0;
		while (totalBytesRcvd < 4) {
			bytesRcvd = in.read(typeBytes, totalBytesRcvd, 4 - totalBytesRcvd);
			totalBytesRcvd += bytesRcvd;
		}
		type = Conversion.BytesToInt(typeBytes);
//		System.out.println("Message:46:message receive: type = " + type + ", length = " + length);
		if (length > 4) {
			payLoad = new byte[length - 4];
		} else {
			payLoad = null;
		}
		totalBytesRcvd = 0;
		while (totalBytesRcvd < length - 4) {
			bytesRcvd = in.read(payLoad, totalBytesRcvd, length - 4 - totalBytesRcvd);
			totalBytesRcvd += bytesRcvd;
		}
	
	}
	
	public void send(Socket s) throws IOException {
		
		if (payLoad == null) {
			length = 4;
		} else {
			length = payLoad.length + 4;
		}
		
		OutputStream out = s.getOutputStream();
		out.write(Conversion.IntToBytes(length));
		out.write(Conversion.IntToBytes(type));
		if (payLoad != null) {
			out.write(payLoad);
		}
		out.flush();
		
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public byte[] getPayLoad() {
		return payLoad;
	}
	public void setPayLoad(byte[] payLoad) {
		this.payLoad = payLoad;
	}
	
}
