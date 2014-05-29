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
	private SwingWindow gui;
	private ChatClientState state;

	public ChatGUIAdapter() {
		ChatServerMessageProducer messageProducer = new ChatJmsAdapter();
		state = new NotLoggedIn(messageProducer, this);
		// messageProducer.setState(state);
		String localConnection = "tcp://localhost:61616";
		messageProducer.connectToServer(localConnection);
	}

	// START GOT METHODS ////////////////////////////////////////////////////// AREA //
	
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
			state.onAcceptInvitation(chatID);
			setInOtherChat();
		}
	}

	@Override
	public void gotChatStarted(String chatId) {
		setInOwnChat();
		gui.AddLineToLog("You are now in your own chat: " + chatId);
	}

	@Override
	public void gotParticipating() {
		setInOtherChat();
	}

	@Override
	public void gotNewChat(String Chatter, String messageText) {
		gui.AddLineToLog(Chatter + ": " + messageText);
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
	
	@Override
	
	public void gotRejected(String chatterID) {
		// TODO Auto-generated method stub

	}
	
	@Override
	/**
	 * Other user has joint the chat room
	 */
	public void gotParticipantEntered(String chatterID) {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * Other user has left the chat room
	 */
	public void gotParticipantLeft(String chatterID) {
		gui.AddLineToLog(chatterID + ": has left the chatroom.");

	}

	@Override
	public void gotRequestCancelled(String chatterID) {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * Other user wants to join my chat
	 */
	public void gotRequest(String chatterID) {
		if (JOptionPane.showConfirmDialog(null, "Accept " + chatterID + " to join?", "Other user want to joint",
				JOptionPane.YES_NO_OPTION) == 0) {
			// you accept Invite
			state.onAccept(chatterID);
		}
	}
	
	
	// END GOT METHODS //////////////////////////////////////////////////////// AREA //

	
	// START BUTTON PRESSED /////////////////////////////////////////////////// AREA //

	
	public void buttonRegisterPressed() {
		String userName = gui.getName();

		// get password
		String password = gui.getPassword();

		// call register on state
		state.onRegister(userName, password);
	}

	/**
	 * Button for Login is Pressed
	 */
	public void buttonLoginPressed() {
		String userName = gui.getName();
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
		setLogedin();
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
	 * User is in own chat and want to close it
	 */
	public void buttonClosePressed() {
		state.onChatClose();
		setLogedin();
	}

	/**
	 * Button to send a message is Pressed
	 */
	public void buttonSendPressed() {
		String message = gui.getMessage();
		state.onChat(message);
		gui.ClearSendTextField();
	}

	public void buttonCreateChatPressed() {
		state.onStartChat();
	}

	
	// END BUTTON PRESSED ///////////////////////////////////////////////////// AREA //
	
	
	@Override
	public void setState(ChatClientState state) {
		this.state = state;
	}
	
	public void setGui(SwingWindow gui) {
		this.gui = gui;
		setNotLoggedin();
	}

	
	// START GUI SETTER /////////////////////////////////////////////////////// AREA //
	
	
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
		gui.setFirstButtonUsage("Close");
		gui.SetShowJoin(false);
		gui.SetShowInvite(true);
		gui.SetShowCreate(false);
		gui.SetShowLogout(true);
		gui.SetShowRegister(false);
	}
	
	
	// END GUI SETTER ///////////////////////////////////////////////////////// AREA //
}
