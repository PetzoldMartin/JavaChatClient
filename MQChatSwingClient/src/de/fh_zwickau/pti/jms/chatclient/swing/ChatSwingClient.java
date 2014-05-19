/**
 * 
 */
package de.fh_zwickau.pti.jms.chatclient.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import de.fh_zwickau.pti.chatclientcommon.ChatClientState;
import de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer;
import de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver;
import de.fh_zwickau.pti.jms.chatclient.messaging.ChatJmsAdapter;

/**
 * @author georg beier
 * 
 */
public class ChatSwingClient implements ChatServerMessageReceiver {

	private JTextField nameTextField;
	private JPasswordField passwordField;
	private JButton btnRegister;
	private JButton btnLogin;
	private JTextPane messageTextPane;
	private JButton btnLogout;

	private ChatClientState currentState;

	private JFrame frame;
	private ChatServerMessageProducer messageProducer;
	private ChatPanel chatPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatSwingClient window = new ChatSwingClient();
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
	public ChatSwingClient() {
		initialize();
		myInit();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		chatPanel = new ChatPanel();
		frame.getContentPane().add(chatPanel, BorderLayout.CENTER);
	}

	/**
	 * 
	 */
	private void myInit() {
		messageProducer = new ChatJmsAdapter();
		messageProducer.setMessageReceiver(this);
		String localConnection = "tcp://localhost:61616";
		((ChatJmsAdapter) messageProducer).connectToServer(localConnection);
		
		nameTextField = chatPanel.getNameTextField();
		passwordField = chatPanel.getPasswordField();
		messageTextPane = chatPanel.getMessageTextPane();
		btnLogin = chatPanel.getBtnLogin();
		btnLogout = chatPanel.getBtnLogout();
		btnRegister = chatPanel.getBtnRegister();

		currentState = notLoggedIn;

		btnLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentState.onLogin();
			}
		});

		btnRegister.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentState.onRegister();
			}
		});

		btnLogout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentState.onlogout();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver#gotSuccess()
	 */
	@Override
	public void gotSuccess() {
		currentState.gotSuccess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver#gotFail()
	 */
	@Override
	public void gotFail() {
		currentState.gotFail();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver#gotLogout()
	 */
	@Override
	public void gotLogout() {
		currentState.gotLogout();
	}

	ChatClientState notLoggedIn = new ChatClientState() {
		public void onRegister() {
			String un = nameTextField.getText();
			String pw = new String(passwordField.getPassword());
			passwordField.setText("");
			btnLogin.setEnabled(false);
			btnRegister.setEnabled(false);
			try {
				messageProducer.register(un, pw);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};

		public void onLogin() {
			String un = nameTextField.getText();
			String pw = new String(passwordField.getPassword());
			passwordField.setText("");
			btnLogin.setEnabled(false);
			btnRegister.setEnabled(false);
			try {
				messageProducer.login(un, pw);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		};

		public void gotSuccess() {
			btnLogout.setEnabled(true);
			currentState = loggedIn;
			messageTextPane.setText("got success");
		};

		public void gotFail() {
			btnRegister.setEnabled(true);
			btnLogin.setEnabled(true);
			messageTextPane.setText("got fail");
		};

		public void gotLogout() {
			btnRegister.setEnabled(true);
			btnLogin.setEnabled(true);
			messageTextPane.setText("logged out");
		};
	};

	ChatClientState loggedIn = new ChatClientState() {
		public void onlogout() {
			btnLogout.setEnabled(false);
			currentState= notLoggedIn;
			try {
				messageProducer.logout();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
	};

}
