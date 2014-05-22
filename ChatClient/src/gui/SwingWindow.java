package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JTextField;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.BoxLayout;

import de.fh_zwickau.pti.chatclientcommon.ChatClientState;
import messaging.ChatJmsAdapter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class SwingWindow {
	
	private ChatSwingClient chatClient;
	private boolean toggleLoginOut;				// true -> login false -> logout

	private JFrame frame;
	private JTextField txtName;
	private JTextField txtPassword;
	private JButton btnLoginOut;
	private JButton btnRegister;
	private JPanel panelChatter;
	private JTextField txtSendmessage;
	private JButton btnSend;
	private JPanel panelChatLog;
	private JTextPane textPaneLog;
	private JPanel panelStatus;

	/**
	 * Create the application.
	 */
	public SwingWindow(ChatSwingClient chatClient) {
		toggleLoginOut = true;
		initialize();	
		frame.validate();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.chatClient = chatClient;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panelAccount = new JPanel();
		FlowLayout fl_panelAccount = (FlowLayout) panelAccount.getLayout();
		fl_panelAccount.setAlignment(FlowLayout.LEFT);
		frame.getContentPane().add(panelAccount, BorderLayout.NORTH);
		
		txtName = new JTextField();
		txtName.setText("Name");
		panelAccount.add(txtName);
		txtName.setColumns(10);
		
		txtPassword = new JTextField();
		txtPassword.setText("password");
		panelAccount.add(txtPassword);
		txtPassword.setColumns(10);
		
		btnLoginOut = new JButton("Login");
		btnLoginOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Log in/out Button pressed
				if(toggleLoginOut)
					chatClient.buttonLoginPressed();
				else
					chatClient.buttonLogoutPressed();
				// TODO: Maybe display some stuff
			}
		});
		panelAccount.add(btnLoginOut);
		
		btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chatClient.buttonRegisterPressed();
			}
		});
		panelAccount.add(btnRegister);
		
		panelChatter = new JPanel();
		frame.getContentPane().add(panelChatter, BorderLayout.SOUTH);
		panelChatter.setLayout(new BoxLayout(panelChatter, BoxLayout.X_AXIS));
		
		txtSendmessage = new JTextField();
		txtSendmessage.setText("SendMessage");
		panelChatter.add(txtSendmessage);
		txtSendmessage.setColumns(10);
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chatClient.btnSendPressed();
			}
		});
		
		panelStatus = new JPanel();
		panelStatus.setBackground(Color.YELLOW);
		panelChatter.add(panelStatus);
		panelChatter.add(btnSend);
		
		panelChatLog = new JPanel();
		frame.getContentPane().add(panelChatLog, BorderLayout.CENTER);
		panelChatLog.setLayout(new BoxLayout(panelChatLog, BoxLayout.X_AXIS));
		
		textPaneLog = new JTextPane();
		panelChatLog.add(textPaneLog);
		
	}
	
	public String getName() {
		return txtName.getText();
	}
	
	public String getPassword() {
		return txtPassword.getText();
	}
	
	public String getMessage() {
		return txtSendmessage.getText();
	}
	
	public void AddLineToLog(String text) {
		textPaneLog.setText(textPaneLog.getText() + "\n" + text);
	}
	
	public void SetStatusColor(Color color) {
		panelStatus.setBackground(color);
	}
	
	public void SetPasswordField(String password) {
		txtPassword.setText(password);
	}
	
	public void toggleLoginOut() {
		if(toggleLoginOut) {
			btnLoginOut.setText("Logout");
		} else {
			btnLoginOut.setText("Login");
		}
		toggleLoginOut = !toggleLoginOut; 
	}

}
