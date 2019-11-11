package za.ac.uj.acsse.csc2b10;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public void runServer(){
		
			try {
				ServerSocket socket = new ServerSocket(7455);
				System.out.println("SERVER STARTED...");
			
				while (true)
				{
					Socket serverSocket = socket.accept();
					new Thread(new ClientHandler(serverSocket)).start();
				}
			
			
				} catch (IOException ex){
						ex.printStackTrace();
				}
		
	}
	
	public static void main(String[] args){
		
		new Server().runServer();
	}

}
