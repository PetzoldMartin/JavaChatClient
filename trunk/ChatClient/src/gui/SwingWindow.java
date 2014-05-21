package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.BoxLayout;

public class SwingWindow {

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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingWindow window = new SwingWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SwingWindow() {
		initialize();
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
		panelAccount.add(btnLoginOut);
		
		btnRegister = new JButton("Register");
		panelAccount.add(btnRegister);
		
		panelChatter = new JPanel();
		frame.getContentPane().add(panelChatter, BorderLayout.SOUTH);
		panelChatter.setLayout(new BoxLayout(panelChatter, BoxLayout.X_AXIS));
		
		txtSendmessage = new JTextField();
		txtSendmessage.setText("SendMessage");
		panelChatter.add(txtSendmessage);
		txtSendmessage.setColumns(10);
		
		btnSend = new JButton("Send");
		panelChatter.add(btnSend);
		
		panelChatLog = new JPanel();
		frame.getContentPane().add(panelChatLog, BorderLayout.CENTER);
		panelChatLog.setLayout(new BoxLayout(panelChatLog, BoxLayout.X_AXIS));
		
		textPaneLog = new JTextPane();
		panelChatLog.add(textPaneLog);
	}

}
