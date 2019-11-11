/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication2;

/**
 *
 * @author Madimetja
 */
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClientFrame extends JFrame{
        //socket for connections
	private Socket socket =  null;
        
        //Streams for input
	private InputStream is = null;
	private BufferedReader br = null;
        
        //Streams for output
	private OutputStream os = null;
	private BufferedOutputStream bos = null;
	private DataOutputStream dos = null;
        
        //strings for funtion calls to the api
	private String grayURL = "/api/GrayScale";
        private String cannyURL = "/api/FastFeatures";
        
        //selected filepath
	private File filePath = null;
	private BufferedImage img= null;
	
        //GUI Components
	private JButton btnConnection = null;
	private JButton btnSend = null;
	private ImagePanel imageSent= null;
	private ImagePanel imageBack = null;
	private JButton btnSelectImage = null;
	private JFileChooser selectfile= null;
	private JTextField messages = null;
        
        private ImagePanel imgCanny = null;
        private JButton btnCanny = null;
	
	public ClientFrame()
	{
                //setting up the gui
		btnConnection = new JButton("Connect");
                btnCanny = new JButton("Trace Out");
		btnSend = new JButton("GrayScale");
		btnSelectImage = new JButton("Select Image");
		messages = new JTextField(20);
		
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(1,4));
		btnPanel.add(btnConnection);
		btnPanel.add(btnSelectImage);
		btnPanel.add(btnSend);
                btnPanel.add(btnCanny);
                
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new GridLayout(1,2));
		bottom.add(messages);
		bottom.add(btnPanel);
		
		
		imageSent = new ImagePanel();
		JLabel original = new JLabel("      Original");
		imageBack = new ImagePanel();
		JLabel results = new JLabel("       GrayScaled");
                imgCanny = new ImagePanel();
                JLabel lblCanny = new JLabel("      Canny");
		
		JPanel images = new JPanel();
		images.setLayout(new GridLayout(1,6));
		images.add(original);
		images.add(imageSent);
		images.add(results);
		images.add(imageBack);
                images.add(lblCanny);
                images.add(imgCanny);
		add(images);
		add(bottom,BorderLayout.SOUTH);
		
		btnSelectImage.setEnabled(false);
		btnSend.setEnabled(false);
		
		btnConnection.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e) 
                    {
			try {
                                //connecting to the server at port 5000
                                socket = new Socket("localhost",5000);
				messages.setText("Client connected to the server\r\n");
                                                        
                                //binding the output and inputstreams
				is = socket.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));
				os = socket.getOutputStream();
				bos = new BufferedOutputStream(os);
				dos = new DataOutputStream(bos);
                                                        
				btnSelectImage.setEnabled(true);
				btnSend.setEnabled(true);
                        } catch(IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				}
                    }
                });
	btnSelectImage.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
						
            //String userhome = System.getProperty("user.home");
            selectfile = new JFileChooser();
            int returnVal = selectfile.showOpenDialog(selectfile);
            if (returnVal == JFileChooser.APPROVE_OPTION) 
            {
                filePath = selectfile.getSelectedFile();
                /* System.out.println("tttttttttttttttttttttttttt");
                System.out.println(filePath.getName());
                System.out.println("tttttttttttttttttttttttttt");*/
		try {
                    img = ImageIO.read(filePath);
                    imageSent.setImage(img);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            }
        });
	btnCanny.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                String encodedFile = null;
                try {
                        //DOS(BOS(OS))
			//Create a File handle
                        //assigning path to choosen path
                        String choosen = filePath.getName();
			File imgFile = new File("data", choosen);
			//read the File into a FileInputStream
			FileInputStream fsr = new FileInputStream(imgFile);
			//Put the file contents into a byte[]
			byte[] bytes = new byte[(int)imgFile.length()];
			fsr.read(bytes);
			//Encode the bytes into a base64 format string
			encodedFile = new String(Base64.getEncoder().encodeToString(bytes));
			//get the bytes of this encoded string
			byte[] bytesToSend = encodedFile.getBytes();
			dos.write(("POST " + cannyURL +" HTTP/1.1\r\n").getBytes());
			dos.write(("Content-Type: " +"application/text\r\n").getBytes());
			dos.write(("Content-Length: " + encodedFile.length()+"\r\n").getBytes());
			dos.write(("\r\n").getBytes());
			dos.write(bytesToSend);
			dos.write(("\r\n").getBytes());
			dos.flush();
			messages.setText("POST Request Sent\r\n");
			//read text response
			String response = "";
			String line = "";
			while(!(line = br.readLine()).equals(""))
			{
                            response += line +"\n";
			}
			messages.setText(response);
			String imgData = "";
			while((line = br.readLine())!=null)
			{
                            imgData += line;
			}
			String base64Str =imgData.substring(imgData.indexOf('\'')+1,imgData.lastIndexOf('}')-1);
			byte[] decodedString = Base64.getDecoder().decode(base64Str);
			//Display the image
			Image grayImg = ImageIO.read(new ByteArrayInputStream(decodedString));
                        BufferedImage imgc = (BufferedImage)grayImg;
			imgCanny.setImage(imgc);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally
                {
                    try 
                    {
                        
                        dos.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
                
	btnSend.addActionListener(new ActionListener()
	{
            @Override
            public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            String encodedFile = null;
            try {
                String choosen = filePath.getName();
		File imageFile = new File("data", choosen);
                //read the File into a FileInputStream
		FileInputStream fileInputStreamReader = new FileInputStream(imageFile);
		//Put the file contents into a byte[]
		byte[] bytes = new byte[(int)imageFile.length()];
		fileInputStreamReader.read(bytes);
		//Encode the bytes into a base64 format string
		encodedFile = new String(Base64.getEncoder().encodeToString(bytes));
		//get the bytes of this encoded string
		byte[] bytesToSend = encodedFile.getBytes();
                //Construct a POST HTTP REQUEST
		dos.write(("POST " + grayURL +" HTTP/1.1\r\n").getBytes());
		dos.write(("Content-Type: " +"application/text\r\n").getBytes());
		dos.write(("Content-Length: " + encodedFile.length()+"\r\n").getBytes());
		dos.write(("\r\n").getBytes());
		dos.write(bytesToSend);
		dos.write(("\r\n").getBytes());
		dos.flush();
		messages.setText("POST Request Sent\r\n");
		//read text response
		String response = "";
		String line = "";
		while(!(line = br.readLine()).equals(""))
                {
                    response += line +"\n";
		}
		messages.setText(response);
		String imgData = "";
		while((line = br.readLine())!=null)
		{
                    imgData += line;
		}
		String base64Str =imgData.substring(imgData.indexOf('\'')+1,imgData.lastIndexOf('}')-1);
		byte[] decodedString = Base64.getDecoder().decode(base64Str);
		//Display the image
		Image grayImg = ImageIO.read(new ByteArrayInputStream(decodedString));
                BufferedImage imgc = (BufferedImage)grayImg;
		imageBack.setImage(imgc);
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally
            {
                try {
                    dos.close();
                } catch (IOException ex) {
                    Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            }
        });
	}
	
	
}
