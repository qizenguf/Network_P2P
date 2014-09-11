class test {
	public static void main(String args[]) throws Exception {
		peerProcess p1 = new peerProcess(1001);
		peerProcess p2 = new peerProcess(1002);
		Thread t1 = new Thread(p1);
		Thread t2 = new Thread(p2);
		t1.start();
		t2.start();
	}
}
