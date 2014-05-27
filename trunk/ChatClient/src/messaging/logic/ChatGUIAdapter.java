package messaging.logic;

import gui.ListBrowser;
import gui.SwingWindow;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import messaging.interfaces.ChatServerMessageProducer;
import messaging.interfaces.ChatServerMessageReceiver;
import States.ChatClientState;
import States.StatesClasses.NotLoggedIn;

public class ChatGUIAdapter implements ChatServerMessageReceiver {

	// Singleton instance
	private static ChatGUIAdapter chatSwingClient = null;

	// client info
	private String userName;
	// TODO remove userName

	private SwingWindow gui;
	private ChatClientState state;

	// public static ChatGUIAdapter getInstance() {
	// if (chatSwingClient == null) {
	// chatSwingClient = new ChatGUIAdapter();
	// }
	// return chatSwingClient;
	// }

	public ChatGUIAdapter() {
		ChatServerMessageProducer messageProducer = new ChatJmsAdapter();
		state = new NotLoggedIn(messageProducer, this);
		// messageProducer.setState(state);
		String localConnection = "tcp://localhost:61616";
		messageProducer.connectToServer(localConnection);
	}

	@Override
	public void gotSuccess() {
		gui.AddLineToLog("System: sucess");
		setLogedin();
	}

	@Override
	public void gotFail() {
		// login fail
		gui.AddLineToLog("System: fail");
		setNotLoggedin();
	}

	@Override
	public void gotLogout() {
		gui.AddLineToLog("System: Logout");
		setNotLoggedin();
	}

	@Override
	public void gotChatClosed() {
		gui.AddLineToLog("System: Chat closed");
		setLogedin();
	}

	@Override
	public void gotInvite(String chatter, String chatID) {
		// TODO implement parameters
		if (JOptionPane.showConfirmDialog(null, chatter
				+ " Do you want to join?", "Got Invite from " + chatter,
				JOptionPane.YES_NO_OPTION) == 0) {
			// User accept Invite
			state.onAcceptInvitation("todo");
		}
	}

	@Override
	public void gotChatStarted(String chatId) {
		// TODO implement chat id
		setInOwnChat();
	}

	@Override
	public void gotParticipating() {
		setInOtherChat();
	}

	@Override
	public void gotNewChat(String Chatter, String messageText) {
		// TODO display chat msg
	}

	@Override
	public void gotDenied(String cNN) {
		setLogedin();
	}

	@Override
	public void gotAccepted(String chatterID) {
		// TODO implement chatterID
		gui.AddLineToLog("System: Accepted");

	}

	@Override
	public void gotChats(ArrayList<String> chatRooms) {
		new ListBrowser(this, chatRooms, "Join");
	}

	@Override
	public void gotChatters(ArrayList<String> chatters) {
		new ListBrowser(this, chatters, "Invite");
	}

	// START BUTTON PRESSED AREA ////////////////

	public void buttonRegisterPressed() {
		userName = gui.getName();

		// get password
		String password = gui.getPassword();

		// call register on state
		state.onRegister(userName, password);
	}

	/**
	 * Button for Login is Pressed
	 */
	public void buttonLoginPressed() {
		userName = gui.getName();
		String password = gui.getPassword();

		state.onLogin(userName, password);
	}

	/**
	 * User is logged in and want to join a chat room A new Window will show the
	 * channel list
	 */
	public void buttonJoinPressed() {
		state.onAskForChats();
	}

	/**
	 * User is in his own chat an want to invite a user A new ListBrowser will
	 * show clients
	 */
	public void buttonInvitePressed() {
		state.onAskForChatters();
	}

	/**
	 * User is in a chatroom and want to leave it
	 */
	public void buttonLeavePressed() {
		state.onLeave();
	}

	/**
	 * User has selected an item from a ListBrowser
	 */
	public void buttonFromListBrowserPressed(String item, String listType) {
		if (item == null)
			item = "null";
		if (listType.equals("Join")) {
			state.onRequest(item);
		} else if (listType.equals("Invite")) {
			state.onInvite(item);
		}
	}

	/**
	 * Button for Logout is Pressed
	 */
	public void buttonLogoutPressed() {
		state.onLogout();
	}

	/**
	 * Button to send a message is Pressed
	 */
	public void btnSendPressed() {
		String message = gui.getMessage();

		// Add message to Log
		gui.AddLineToLog(userName + ": " + message);

		state.onChat(message);
	}

	public void buttonCreateChatPressed() {
		state.onStartChat();
	}

	@Override
	public void setState(ChatClientState state) {
		this.state = state;
	}

	@Override
	public void gotParticipantEntered(String chatterID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gotParticipantLeft(String chatterID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gotRequestCancelled(String chatterID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gotRequest(String chatterID) {
		// TODO Auto-generated method stub

	}

	private void setLogedin() {
		gui.SetStatusColor(Color.GREEN);
		gui.setFirstButtonUsage("Logout");
		gui.SetShowJoin(true);
		gui.SetShowCreate(true);
		gui.SetShowInvite(false);
		gui.SetShowLogout(true);
		gui.SetShowRegister(false);
	}

	private void setNotLoggedin() {
		gui.SetStatusColor(Color.YELLOW);
		gui.setFirstButtonUsage("Login");
		gui.SetShowRegister(true);
		gui.SetShowJoin(false);
		gui.SetShowInvite(false);
		gui.SetShowCreate(false);
		gui.SetShowPartyUser(false);
	}

	private void setInOtherChat() {
		gui.SetStatusColor(Color.PINK);
		gui.setFirstButtonUsage("Leave");
		gui.SetShowJoin(false);
		gui.SetShowInvite(false);
		gui.SetShowCreate(false);
		gui.SetShowLogout(true);
		gui.SetShowRegister(false);
	}

	private void setInOwnChat() {
		gui.SetStatusColor(Color.PINK);
		gui.setFirstButtonUsage("Leave");
		gui.SetShowJoin(false);
		gui.SetShowInvite(true);
		gui.SetShowCreate(false);
		gui.SetShowLogout(true);
		gui.SetShowRegister(false);
	}

	@Override
	public void gotRejected(String chatterID) {
		// TODO Auto-generated method stub

	}

	public void setGui(SwingWindow gui) {
		this.gui = gui;
		setNotLoggedin();
	}
}
