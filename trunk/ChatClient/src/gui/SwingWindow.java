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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;
import net.miginfocom.swing.MigLayout;
import java.awt.GridLayout;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;

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
	private JPanel panelChannel;
	private JPanel panelChannelAdmin;
	private JButton btnCreate;
	private JButton btnJoin;
	private JButton btnInvite;
	private JTextField txtUser;

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
		
		panelChannel = new JPanel();
		panelChatLog.add(panelChannel);
		panelChannel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		panelChannelAdmin = new JPanel();
		panelChannel.add(panelChannelAdmin);
		panelChannelAdmin.setLayout(new GridLayout(0, 1, 0, 0));
		
		btnCreate = new JButton("Create");
		btnCreate.setVisible(false);
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Create new chat
				chatClient.buttonCreateChatPressed();
			}
		});
		panelChannelAdmin.add(btnCreate);
		
		btnJoin = new JButton("Join");
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatClient.buttonJoinPressed();
			}
		});
		btnJoin.setVisible(false);
		panelChannelAdmin.add(btnJoin);
		
		btnInvite = new JButton("Invite");
		btnInvite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatClient.buttonInvitePressed();
			}
		});
		btnInvite.setVisible(false);
		panelChannelAdmin.add(btnInvite);
		
		txtUser = new JTextField();
		txtUser.setText("User");
		txtUser.setVisible(false);
		panelChannelAdmin.add(txtUser);
		txtUser.setColumns(10);
		
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
	
	public String getPartyUser() {
		return txtUser.getText();
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
	
	public void SetShowInvite(boolean show) {
		btnInvite.setVisible(show);
	}
	
	public void SetShowJoin(boolean show) {
		btnJoin.setVisible(show);
	}
	
	public void SetShowCreate(boolean show) {
		btnCreate.setVisible(show);
	}
	
	public void SetShowRegister(boolean show) {
		btnRegister.setVisible(show);
	}
	
	public void SetShowPartyUser(boolean show) {
		txtUser.setVisible(show);
	}

}
