package za.ac.uj.acsse.csc2b10;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Socket client = null;
	
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	
	private JButton btnSend = null;
	private JButton btnList = null;
	private JButton btnUpload = null;
	private JTextField cmdField = null;
	private JPanel pnlSouth = null;
	private JScrollPane scrollPane = null;
	public JTextArea textPanel = null;
	
	

public Client(){
		
		//Components for gui window
	textPanel = new JTextArea();
	scrollPane = new JScrollPane(textPanel);
	setTitle("CLIENT");
	setSize(800, 600);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
				
	btnSend = new JButton("Send");
	btnSend.addActionListener(new ButtonListener());
	btnList = new JButton("List Images");
	btnList.addActionListener(new ButtonListener());
	btnUpload = new JButton("Upload Image");
	btnUpload.addActionListener(new ButtonListener());
				
	cmdField = new JTextField(40);
	cmdField.setText("Enter Command!");
		
	pnlSouth = new JPanel();
		
	JPanel sPanel = new JPanel();
	pnlSouth.add(cmdField);
	pnlSouth.add(btnSend);
	pnlSouth.add(btnList);
		
	sPanel.setLayout(new BorderLayout());
	sPanel.add(pnlSouth, BorderLayout.WEST);
	sPanel.add(btnUpload, BorderLayout.EAST);
		
	add(scrollPane, BorderLayout.CENTER);
	add(sPanel, BorderLayout.SOUTH);
		
	//Construct socket
	try{
		
		client = new Socket("127.0.0.1", 7455);
			
		//Bind the streams to the port
		writer = new PrintWriter(client.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		textPanel.append("Connected To Server : " + "127.0.0.1" + "\n");
	} catch(IOException ex){
		ex.printStackTrace();
	}
	
}

	public void logMsg(String msg){
		System.out.println(msg);
	}
	
private class ButtonListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent event) {
			
			//If the action is from the list button
			if (event.getSource() == btnList){
				
				try {
					writer.flush();
					//Send the command
					writer.write("LIST\r\n");
					writer.flush();
					//Display reply
					textPanel.append(reader.readLine() + "\n");
					
					//Carry out listing procedure
					String imgs = "";
					
					//If the reader is not empty, then run this
					while ((imgs = reader.readLine()) != null){
						
						writer.flush();
						textPanel.append("->" + imgs + "\n");
					}
				} catch (NumberFormatException ex){
					ex.printStackTrace();
				} catch (IOException ex){
					ex.printStackTrace();
				}
			}
			
			//If the action is from the btnSend button
			if (event.getSource() == btnSend){
				
				//Get the command to send, and sent it through the stream
				writer.write(cmdField.getText() + "\r\n");
				writer.flush();
				try {
					//Read the response and write it to the text window
					textPanel.append(reader.readLine() + "\n");
				} catch (IOException ex){
					ex.printStackTrace();
				}
			}
			
			//If the action is from the upload button
			if (event.getSource() == btnUpload){
				
				try{
					
					//DataInputStream dataInput = new DataInputStream(new BufferedInputStream(client.getInputStream()));
					String temp = null;
					String strFileName = null;
					
					JFileChooser choose = new JFileChooser(".");
					int result = choose.showOpenDialog(Client.this);
					
					if (result == JFileChooser.APPROVE_OPTION){
						
						temp = choose.getSelectedFile().getAbsolutePath();
						strFileName = choose.getSelectedFile().getName();
					}
					//int fileSize = Integer.parseInt(reader.readLine());
					int fileSize = Integer.parseInt(JOptionPane.showInputDialog("Enter ID"));
					writer.write("Upload" + " " + strFileName + fileSize + "\r\n");
					
					byte[] data = new byte[fileSize];
					int count = 0;
					
					//Create file
					File tempFile = new File(temp);
					FileInputStream fInput = new FileInputStream(tempFile);
					DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
					
					while ((count = fInput.read(data)) > 0){
						
						dataOutput.write(data);
						dataOutput.flush();
						
						if (count < fileSize){
							break;
						} else {
							writer.flush();
						}
						
						textPanel.append(reader.readLine() + "\n");
					}
					
					textPanel.append(reader.readLine() + "\n");
					writer.flush();
					dataOutput.flush();
					fInput.close();
					
				} catch (IOException ex){
					ex.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args){
		
		new Client().setVisible(true);
	}

}
