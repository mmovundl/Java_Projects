package za.ac.uj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {

	static ServerSocket ss = null;
	static Socket clientConnection = null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			ss = new ServerSocket(8888);//server object that connects on port 8888
			System.out.println("Waitng for Connection...");
			clientConnection = ss.accept();//connects the server to the client
			PrintWriter out = new PrintWriter(clientConnection.getOutputStream(),true);//output from server
			BufferedReader in = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));//input from user
			
			out.println("01 WELCOME - You may ask 5 questions");
			out.flush();
			
			String ready = in.readLine();
			int count = 0;//counts the number of questions asked
			if (ready.contains("READY"))
			{
				out.println("02 ASK your question or DONE");
				out.flush();
				for (int i = 0; i <  5; i++ )
				{
					String question = "";
					question = in.readLine();
					if(question.startsWith("ASK Why" ))
					{
						out.println("03 Because that’s the way it is");
						out.flush();
					}else if(question.startsWith("ASK Are"))
					{
						Random n = new Random();
						int ran = n.nextInt(3);//allows server to randomly give an answer
						switch(ran)
						{
						case 1:
							out.println("03 Yes");
							out.flush();
							count++;
							break;
						case 2:
							out.println("03 No");
							out.flush();
							count++;
						default:
							out.println("03 Maybe");
							out.flush();
							count++;
						}
					}else if (question.startsWith("DONE") ||question.startsWith("Done"))
					{
						out.println("0# GOODBYE - " + count + " questions answered");
						out.flush();
						ss.close();//closing the connection
						i += 5;
					}else
					{
						Random r = new Random();
						int num = r.nextInt(3);//allows server to randomly give an answer
						switch(num){
						case 1:
							out.println("03 It depends");
							out.flush();
							count++;
							break;
						case 2:
							out.println("03 Pleaase ask again later");
							out.flush();
							count++;
						default:
							out.println("03 Meh");
							out.flush();
							count++;
						}
					}
					
					
				}
				if (count == 5)
				{
					out.println("5 HAVE A NICE DAY - " + count + " Questions answered");
					out.flush();
					ss.close();
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if (clientConnection != null)
			{
				try {
					clientConnection.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
