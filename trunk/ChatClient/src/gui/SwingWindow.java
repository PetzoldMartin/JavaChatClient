package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import messaging.logic.ChatGUIAdapter;

public class SwingWindow {

	private final ChatGUIAdapter chatClient;

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
	 * 
	 * @wbp.parser.constructor
	 */
	public SwingWindow(ChatGUIAdapter chatClient) {
		initialize();
		frame.validate();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.chatClient = chatClient;
		chatClient.setGui(this);
	}

	public SwingWindow(String user, String password) {
		this(new ChatGUIAdapter());
		this.txtName.setText(user);
		this.txtPassword.setText(password);
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
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Log in/out Button pressed
				if (arg0.getActionCommand().equals("Login")) {
					chatClient.buttonLoginPressed();
				} else if (arg0.getActionCommand().equals("Logout")) {
					chatClient.buttonLogoutPressed();
				} else if (arg0.getActionCommand().equals("Leave")) {
					chatClient.buttonLeavePressed();
				}
			}
		});
		panelAccount.add(btnLoginOut);

		btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			@Override
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
			@Override
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
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Create new chat
				chatClient.buttonCreateChatPressed();
			}
		});
		panelChannelAdmin.add(btnCreate);

		btnJoin = new JButton("Join");
		btnJoin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chatClient.buttonJoinPressed();
			}
		});
		btnJoin.setVisible(false);
		panelChannelAdmin.add(btnJoin);

		btnInvite = new JButton("Invite");
		btnInvite.addActionListener(new ActionListener() {
			@Override
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

	public void setName(String name) {
		txtName.setText(name);
	}

	public void setFirstButtonUsage(String usage) {
		btnLoginOut.setText(usage);
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

	public void SetShowLogout(boolean show) {
		btnLoginOut.setVisible(show);
	}

}
