import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;
public class HandShakeMsg {
	
	private int ID;
	
	public void setID(int ID) {
		this.ID = ID;
	}

	public void read(Socket s) throws IOException {
		Scanner in = new Scanner(s.getInputStream());
		ID = in.nextInt();
	}
	
	public void send(Socket s) throws IOException {
		PrintWriter out = new PrintWriter(s.getOutputStream());
		out.println(ID);
		out.flush();
	}
	
	public int getID() {
		return ID;
	}
	
}
