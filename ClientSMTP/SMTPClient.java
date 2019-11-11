package za.ac.uj.acsse.csc2b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SMTPClient{
	
	Socket socket = null;
	PrintWriter outS = null;
	BufferedReader inS = null;
	InetAddress host;
	
	public SMTPClient(String sender, String reciever, String msg)
	{
		String message;
		try {
			//locals machine's IP address
			host = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	
		try {
			
			socket = new Socket(host,25);
			//binding the sockets to the streams
			outS = new PrintWriter(socket.getOutputStream(),true);
			inS = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//SMTP messages sending
			message = inS.readLine();
			System.out.println(message);
			
			outS.println("HELO");
			outS.flush();
			
			outS.println("MAIL From:<" + sender + "@csc2b.uj.ac.za>");
			outS.flush();
			
			message = inS.readLine();
			System.out.println(message);
			
			outS.println("RCPT To:<" + reciever + "@csc2b.uj.ac.za>");
			outS.flush();
			
			message = inS.readLine();
			System.out.println(message);
			
			outS.println("DATA");
			outS.flush();
			
			message = inS.readLine();
			System.out.println(message);
			
			outS.println("From:" + sender + "");
			outS.flush();
			outS.println("To:" + reciever);
			outS.flush();
			
			outS.println("");
			outS.flush();
			
			outS.println(msg);
			outS.flush();
			
			//ending the message sent
			outS.println("\r\n.\r\n.");
			outS.flush();
			
			message = inS.readLine();
			System.out.println(message);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
