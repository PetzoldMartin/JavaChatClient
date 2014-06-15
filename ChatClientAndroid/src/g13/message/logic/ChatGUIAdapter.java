package g13.message.logic;

import g13.gui.ListActivity;
import g13.gui.MainActivity;
import g13.message.interfaces.ChatServerMessageReceiver;
import g13.message.interfaces.IReceiveStompMessages;
import g13.state.ChatClientState;
import g13.state.client.LoggedIn;
import g13.state.client.NotLoggedIn;
import g13.state.client.chat.InOtherChat;
import g13.state.client.chat.InOwnChat;

import java.io.Serializable;
import java.security.acl.Owner;
import java.util.ArrayList;

import de.fh_zwickau.informatik.stompj.StompMessage;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import android.content.ComponentName;
import android.util.Log;

public class ChatGUIAdapter implements IReceiveStompMessages,
		ChatServerMessageReceiver {

	private MainActivity gui;
	private ChatClientState state;

	public ChatGUIAdapter(MainActivity mainActivity) {
		gui = mainActivity;
	}

	// START GOT METHODS ////////////////////////////////////////////////////// AREA //
	
	@Override
	public void gotSuccess() {
		Log.i("GUI-Adapter", "gotSuccess()");
		gui.gotoLoggedInView();
	}

	@Override
	// login failed
	public void gotFail() {
		gui.gotoNotLoggedInView();
	}

	@Override
	public void gotLogout() {
		gui.gotoNotLoggedInView();
	}

	@Override
	public void gotChatClosed() {
		gui.gotoLoggedInView();
	}

	@Override
	public void gotInvite(String chatter, String chatID) {
		// TODO implement parameters
//		if (JOptionPane.showConfirmDialog(null, chatter
//				+ " Do you want to join?", "Got Invite from " + chatter,
//				JOptionPane.YES_NO_OPTION) == 0) {
//			// User accept Invite
//			state.onAcceptInvitation(chatID);
//			setInOtherChat();
//		} else {
//			state.onDeny(chatID);
//		}
	}

	@Override
	public void gotChatStarted(String chatId) {
		gui.gotoOwnChatView();
		gui.AddLineToLog("You are now in your own chat: " + chatId);
	}

	@Override
	public void gotParticipating() {
		gui.gotoOtherChatView();
	}


	/**
	 * A Chatter has send a textMessage 
	 * @param chatter The Chatter user
	 * @param textMessage The received message
	 */
	@Override
	public void gotNewChat(String chatter, String textMessage) {
		gui.setChatinChatlog(chatter,textMessage);
	}

	@Override
	public void gotDenied(String cNN) {
		gui.gotoLoggedInView();
	}

	/**
	 * User is in his own chat and has invited a another user
	 * he becomes a info that the invite is accepted
	 * @param chatterID The invited chatterID
	 */
	@Override
	public void gotAccepted(String chatterID) {
		gui.AddLineToLog(chatterID + " Accepted your invite.");
	}

	@Override
	public void gotChats(ArrayList<String> chatRooms) {
		gui.SetActivity(ListActivity.class, chatRooms);
	}

	@Override
	public void gotChatters(ArrayList<String> chatters) {
		gui.SetActivity(ListActivity.class, chatters);
	}
	
	@Override
	
	/**
	 * Rejected by chat owner
	 */
	public void gotRejected(String chatterID) {
		gui.AddLineToLog("your request to join a chat room was rejected by user: " + chatterID);

	}
	
	@Override
	/**
	 * Other user has joint the chat room
	 */
	public void gotParticipantEntered(String chatterID) {
		gui.AddLineToLog(chatterID + ": has joint the chatroom.");

	}

	@Override
	/**
	 * Other user has left the chat room
	 */
	public void gotParticipantLeft(String chatterID) {
		gui.AddLineToLog(chatterID + ": has left the chatroom.");
	}

	@Override
	/**
	 * The user that you have invited don't want to join your chat
	 */
	public void gotRequestCancelled(String chatterID) {
		gui.AddLineToLog(chatterID + "don't want to join your chat!");
	}

	/**
	 * Other user wants to join my chat
	 * @param chatterID The joined Chatters ID
	 */
	@Override
	public void gotRequest(String chatterID) {
//		if (JOptionPane.showConfirmDialog(null, "Accept " + chatterID + " to join?", "Other user want to joint",
//				JOptionPane.YES_NO_OPTION) == 0) {
//			// you accept Invite
//			state.onAccept(chatterID);
//		} else {
//			state.onReject(chatterID);
//		}
	}
	
	
	// END GOT METHODS //////////////////////////////////////////////////////// AREA //

	
	// START BUTTON PRESSED /////////////////////////////////////////////////// AREA //

	
	public void buttonRegisterPressed(String name, String password) {
		state.onRegister(name, password);
	}

	/**
	 * Button for Login is Pressed
	 */
	public void buttonLoginPressed(String name, String password) {
		state.onLogin(name, password);
	}

	/**
	 * User is logged in and want to join a chat room A new Window will show the
	 * channel list
	 */
	public void buttonJoinPressed() {
		state.onAskForChats();
	}
	
	/**
	 * The use has selected a item in a List
	 * @param item name
	 */
	public void listItemSelected(String item) {
		if(state instanceof LoggedIn){
			Log.i("GUI", "User want to join " + item);
			state.onRequest(item);
		}
		if(state instanceof InOwnChat){
			Log.i("GUI", "User want to invite " + item);
			state.onInvite(item);
		}
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
		gui.gotoLoggedInView();
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
	}

	/**
	 * Button to send a message is Pressed
	 */
	public void buttonSendPressed() {
//		String message = gui.getMessage();
//		state.onChat(message);
//		gui.ClearSendTextField();
	}

	public void buttonCreateChatPressed() {
		state.onStartChat();
	}

	
	// END BUTTON PRESSED ///////////////////////////////////////////////////// AREA //
	
	
	@Override
	public void setState(ChatClientState state) {
		this.state = state;
		
		// TODO: remove this later
		if(state instanceof NotLoggedIn) {
			gui.gotoNotLoggedInView();
		}
	}
	
	public void setGui(MainActivity gui) {
		this.gui = gui;
	}
	
	// public void Connect(String ip) {
	// // ChatServerMessageProducer messageProducer = new ChatJmsAdapter();
	// // state = new NotLoggedIn(messageProducer, this);
	// messageProducer.connectToServer("tcp://" + ip); // default
	// localhost:61616"
	// }

	@Override
	public void onServiceBound(ComponentName name) {
		if (state != null) {
			state.serviceBound();
		}
		// TODO bind the service
	}

	@Override
	public void onServiceUnbound(ComponentName name) {
		Log.e("ClientStateManager.onServiceUnbound", "should never be called");

	}

	@Override
	public void onStompMessage(Serializable message) {
		Log.e("ChatguiAdapter", "should never be called on Stomp Message");
		Log.i("Message on wrong place",
				"Client: "
						+ ((StompMessage) message)
								.getProperty(MessageHeader.MsgKind.toString()));
		Log.i("Message on wrong place",
				"Client: " + ((StompMessage) message).getContentAsString());
		
	}

	@Override
	public void onConnection(boolean success) {
		Log.e("ChatguiAdapter", "should never be called on Stomp Connection");
		

	}

	@Override
	public void onError(String error) {
		Log.e("Error", error); // TODO refractor tag
	}

	public ChatClientState getState() {
		return state;
	}

	@Override
	public void debug(String debug) {
		gui.AddLineToLog(debug);
	}
	
	public ChatClientState debugGetState() {
		return state;
	}

	public void onConnect(String ip, int i, String string, String string2) {
		state.onConnect(ip, i, string, string2);
		
	}

	public void onNewChat(String chatText) {
		state.onChat(chatText);
		
	}

}
