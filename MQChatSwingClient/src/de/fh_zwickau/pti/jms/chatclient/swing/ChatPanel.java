/**
 * 
 */
package de.fh_zwickau.pti.jms.chatclient.swing;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import java.awt.Dimension;

/**
 * @author georg beier
 *
 */
public class ChatPanel extends JPanel {
	private JTextField nameTextField;
	private JPasswordField passwordField;
	private JButton btnRegister;
	private JButton btnLogin;
	private JTextPane messageTextPane;
	private JLabel lblMessages;
	private JButton btnLogout;

	/**
	 * Create the panel.
	 */
	public ChatPanel() {

		initComponents();
	}
	private void initComponents() {
		setLayout(new MigLayout("", "[][][grow]", "[][][][][grow]"));
		
		JLabel lblNickname = new JLabel("Nickname");
		add(lblNickname, "cell 0 0,alignx trailing");
		
		nameTextField = new JTextField();
		add(nameTextField, "cell 1 0 2 1,growx,aligny center");
		nameTextField.setColumns(10);
		
		JLabel lblPasswort = new JLabel("Passwort");
		add(lblPasswort, "cell 0 1,alignx trailing");
		
		passwordField = new JPasswordField();
		add(passwordField, "cell 1 1 2 1,grow");
		
		btnRegister = new JButton("Register");
		add(btnRegister, "cell 0 2");
		
		btnLogin = new JButton("Login");
		btnLogin.setPreferredSize(new Dimension(75, 23));
		add(btnLogin, "cell 1 2");
		
		btnLogout = new JButton("Logout");
		btnLogout.setPreferredSize(new Dimension(75, 23));
		add(btnLogout, "cell 2 2");
		
		lblMessages = new JLabel("Messages");
		add(lblMessages, "cell 0 3,alignx right");
		
		messageTextPane = new JTextPane();
		messageTextPane.setPreferredSize(new Dimension(6, 180));
		messageTextPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		messageTextPane.setEditable(false);
		add(messageTextPane, "cell 0 4 3 1,growx,aligny top");
	}
	public JTextField getNameTextField() {
		return nameTextField;
	}
	public JPasswordField getPasswordField() {
		return passwordField;
	}
	public JButton getBtnRegister() {
		return btnRegister;
	}
	public JButton getBtnLogin() {
		return btnLogin;
	}
	public JTextPane getMessageTextPane() {
		return messageTextPane;
	}
	public JButton getBtnLogout() {
		return btnLogout;
	}

}
