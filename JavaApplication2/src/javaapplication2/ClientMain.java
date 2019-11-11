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
import javax.swing.JFrame;

public class ClientMain extends JFrame{
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClientFrame frame = new ClientFrame();
		frame.setSize(800,700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

}