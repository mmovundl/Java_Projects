package za.ac.uj.acsse.csc2b10;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JFrame;

public class ClientHandler extends JFrame implements Runnable {

		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Socket socket;
	private PrintWriter writer = null;
	private DataOutputStream output =  null;
	private DataInputStream input = null;
		
	public ClientHandler(Socket socket){
		this.socket = socket;
		}
	
		//For outputting messages to the console
		public void msgLog(String msg){
			System.out.print(msg);
		}
		
		//For outputting errors
		public void errors(DataOutputStream out, int code, String msg){
			
			try {
				msgLog("Error " + code + " " + msg);
				out.writeBytes("Error: Invalid Command\r\n");
				out.flush();
			} catch(IOException ex){
				ex.printStackTrace();
			}
		}
		
		@Override
		public void run() {
		
			try {
				
				BufferedReader txtInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
				output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				
				String line = null;
				msgLog("---------- SERVER LOG -----------------\n");
				msgLog("---------------------------------------\n");
				
				File fFolder = new File(".","File Folder");
				fFolder.mkdir();
				
				while((line = txtInput.readLine()) != null){
					
					//Handle requests
					if( !(line.contains("LIST") || line.contains("RET") || line.contains("SEND"))){
						errors(output, 37, "Command Not Supported " + line);
					} else {
						//If the line contains recognizable input, then analyze the string
						StringTokenizer getTokens = new StringTokenizer(line);
						if (getTokens.countTokens() == 0) {
							//If the string contains no tokens, then there must be a error
							errors(output, 500, "The Command Syntax Is Invalid");
						} else {
							String cmd = getTokens.nextToken();
							
							//Note: Command not required from practical ~ Used for convenience
							if (cmd.equals("CONNECT")){
								
								writer.write("SERVER: Welcome To The NSA Image Server!\r\n");
								writer.flush();
								msgLog("Client: " + cmd + "\n");
								msgLog("Welcome To The NSA Image Transfer!\n");
							}
							
							if(cmd.equals("SEND")){
								String fName = getTokens.nextToken();
								long size = Integer.parseInt(getTokens.nextToken());
								
								writer.write("SERVER: File Saved!\r\n");
								writer.flush();
								
								msgLog("Client: " + cmd + " " + fName + " " + size + "\n");
								
								File newFile = new File(fFolder, fName);
								FileOutputStream fout = new FileOutputStream(newFile);
								
								int count = 0;
								int dataLength = 0;
								byte[] data = new byte[1024];
								msgLog("Binary Data\n");
								
								//Whilst there is information to read in the data[] array
								while((dataLength = input.read(data)) > 0){
									
									count += dataLength;
									fout.write(data);
									msgLog("" + count + "\n");
									fout.flush();
									writer.write(count + "\r\n");
									writer.flush();
									
									if (count < 1024){
										break;
									}
								}
								
								if (count != dataLength){
									errors(output, 39, "File Size Does Not Match " + line);
								}
								
								writer.flush();
								fout.flush();
								fout.close();
							}
							
							if (cmd.equals("LIST")){ 
								
								String[] strFile = fFolder.list();
								writer.write("SERVER : Available " + strFile.length + "\r\n");
								for (String filename : strFile){
									
									writer.write(filename + "\r\n");
									writer.flush();
								}
								writer.flush();
								break;
							}
							
							//If the command is RET, display image
							if (cmd.equals("RET")){
								
								String filename = getTokens.nextToken();
								
								File newFile = new File(fFolder, filename);
								FileInputStream fInput = new FileInputStream(newFile);
								
								int size = 0;
								int dataLength = 0;
								byte[] data = new byte[1024];
								
								while((dataLength = fInput.read(data)) > 0){
									
									size += dataLength;
									output.write(data);
									msgLog("" + size + "\n");
									output.flush();
									writer.write(size + "\r\n");
									writer.flush();
									
									if(size < 1024){
										break;
									}
									
									fInput.close();
								}
							}
						}
					}
				}
			} catch (IOException ex){
				ex.printStackTrace();
			} finally {
				
				if (socket != null){
					
					try {
						
							socket.close();
						
						if (output != null){
							output.close();
						}
						
					} catch (IOException ex){
						ex.printStackTrace();
					}
				}
			}
		}


}
