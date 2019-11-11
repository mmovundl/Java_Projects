package za.ac.uj.acsse.csc2b;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SMTFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtSender;
	private JTextField txtReceiver;
	private JButton	btnSend;
	private JTextArea txtContent;
	
	public SMTFrame() 
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4,2));
		
		txtSender = new JTextField(10);
		txtReceiver = new JTextField(10);
		
		JPanel snder =new JPanel();
		snder.setLayout(new GridLayout(4,1));
		//snder.setSize(NORMAL, NORMAL);
		snder.add(new JLabel("Sender Name"));
		snder.add(txtSender);
		
		JPanel rcver = new JPanel();
		rcver.setLayout(new GridLayout(4,1));
		//rcver.setSize(NORMAL, NORMAL);
		rcver.add(new JLabel("Receiver Name"));
		rcver.add(txtReceiver);
		
		txtContent = new JTextArea(40,30);
		
		JPanel pMassg = new JPanel();
		pMassg.setLayout(new GridLayout(2,4));
		pMassg.add(new JLabel("Massege"));
		pMassg.add(txtContent);
		
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String snder= txtSender.getText();
				String rcr = txtReceiver.getText();
				if( snder == null && rcr == null)
				{	
					SMTPClient client = new SMTPClient("201410613","201410613",txtContent.getText());
				}else {
		
					SMTPClient client = new SMTPClient(snder,rcr,txtContent.getText());
				}
				
				
			}});
		panel.add(snder);
		panel.add(rcver);
		panel.add(pMassg);
		panel.add(btnSend);
		
		add(panel,BorderLayout.CENTER);
	}

}
