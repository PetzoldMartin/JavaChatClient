package messaging.logic;

import gui.ListBrowser;
import gui.SwingWindow;

import java.awt.Color;
import java.util.ArrayList;

import messaging.interfaces.ChatServerMessageProducer;
import messaging.interfaces.ChatServerMessageReceiver;
import States.ChatClientState;
import States.StatesClasses.NotLoggedIn;

public class ChatSwingClient implements ChatServerMessageReceiver {

	// Singleton instance
	private static ChatSwingClient chatSwingClient = null;

	// client info
	private String userName;

	private SwingWindow gui;
	private ChatClientState state;

	private ArrayList<String> chatRooms;
	private ArrayList<String> chatClients;

	public ChatSwingClient() {
		myInit();
	}

	private void myInit() {
		ChatServerMessageProducer messageProducer = ChatJmsAdapter
				.getInstance();
		// messageProducer.setMessageReceiver(this);

		state = new NotLoggedIn(messageProducer, this) {
		};
		messageProducer.setState(state);
		String localConnection = "tcp://localhost:61616";
		messageProducer.connectToServer(localConnection);

		gui = new SwingWindow(this);
	}

	@Override
	public void gotSuccess() {
		gui.AddLineToLog("System: sucess");
		gui.SetStatusColor(Color.GREEN);
		gui.toggleLoginOut();
		gui.SetShowRegister(false);
		gui.SetShowCreate(true);
		gui.SetShowJoin(true);
		gui.SetShowPartyUser(true);
		// user is logged in
	}

	@Override
	public void gotFail() {
		// login fail
		gui.AddLineToLog("System: fail");
		gui.SetStatusColor(Color.RED);
		gui.SetShowRegister(true);
	}

	@Override
	public void gotLogout() {
		gui.AddLineToLog("System: Logout");
		gui.SetStatusColor(Color.YELLOW);
		gui.toggleLoginOut();
		gui.SetShowRegister(true);
		gui.SetShowJoin(false);
		gui.SetShowInvite(false);
		gui.SetShowCreate(false);
		gui.SetShowPartyUser(false);
	}

	@Override
	public void gotChatClosed() {
		gui.AddLineToLog("System: Chat closed");
		gui.SetStatusColor(Color.GREEN);
		gui.SetShowJoin(true);
		gui.SetShowCreate(true);
		gui.SetShowInvite(false);
	}

	@Override
	public void gotInvite() {
		// TODO Auto-generated method stub
		gui.SetStatusColor(Color.CYAN);
		// TODO: popup
	}

	@Override
	public void gotReject() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotChatStarted() {
		// in own chat
		gui.SetStatusColor(Color.PINK);
		gui.SetShowJoin(false);
		gui.SetShowInvite(true);
		gui.SetShowCreate(false);
	}

	@Override
	public void gotParticipating() {
		// in other char
		gui.SetStatusColor(Color.PINK);
	}

	@Override
	public void gotNewChat() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotParticipantEntered() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotParticipantLeft() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotRequestCancelled() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotRequest() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotDenied(String cNN) {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotAccepted() {
		gui.AddLineToLog("System: Accepted");

	}

	public void gotChats(ArrayList<String> chatRooms) {
		this.chatRooms = chatRooms;
	}

	// START BUTTON PRESSED AREA ////////////////

	public void buttonRegisterPressed() {
		userName = gui.getName();

		// get password
		String password = gui.getPassword();

		// call register on state
		state.onRegister(userName, password);

		// clear password for safety
		gui.SetPasswordField("");
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
		new ListBrowser(this, chatRooms, "Join");
	}

	/**
	 * User is in his own chat an want to invite a user A new ListBrowser will
	 * show clients
	 */
	public void buttonInvitePressed() {
		state.onAskForChatters();
		new ListBrowser(this, chatClients, "Invite");
	}

	/**
	 * User has selected an item from a ListBrowser
	 */
	public void buttonFromListBrowserPressed(String item, String listType) {
		if (listType.endsWith("Join")) {
			state.onRequest(item);
		} else if (listType.endsWith("Invite")) {
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

		// state.onChat(message);
	}

	public void buttonCreateChatPressed() {
		state.onStartChat();
	}

	public static ChatSwingClient getInstance() {
		if (chatSwingClient == null) {
			chatSwingClient = new ChatSwingClient();
		}
		return chatSwingClient;
	}

	@Override
	public void setState(ChatClientState state) {
		this.state = state;
	}
}
