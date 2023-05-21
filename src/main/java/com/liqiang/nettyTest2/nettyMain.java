package com.liqiang.nettyTest2;

public class nettyMain {
	public static void main(String[] args) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
<<<<<<< HEAD
//				Server server = new Server(8089);
//				server.startServer();
=======
				Server server = new Server(8089);
				server.startServer();
>>>>>>> origin/master

			}
		}).start();
	
	}

}
