
public class Conversion {
	
	public static int BytesToInt(byte[] bytes) {
		int b = 0;
		for (int j = 0; j < 4; j++)
			b = (b << 8) | (bytes[j] & 0xFF);
		return b;
	}
	
	public static byte[] IntToBytes(int i) {
		
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ((i & 0xff000000) >> 24);
		bytes[1] = (byte) ((i & 0xff0000) >> 16);
		bytes[2] = (byte) ((i & 0xff00) >> 8);
		bytes[3] = (byte) (i & 0xff);
		return bytes; 
		
		}

	
}
