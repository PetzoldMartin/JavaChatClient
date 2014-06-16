package g13.message.logic;

import g13.gui.ListActivity;
import g13.gui.MainActivity;
import g13.gui.Popup;
import g13.message.interfaces.ChatServerMessageReceiver;
import g13.message.interfaces.IReceiveStompMessages;
import g13.state.ChatClientState;
import g13.state.client.LoggedIn;
import g13.state.client.NotLoggedIn;
import g13.state.client.chat.InOwnChat;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.FragmentManager;
import android.content.ComponentName;
import android.util.Log;
import de.fh_zwickau.informatik.stompj.StompMessage;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;

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
		Popup popup = new Popup();
		popup.setGUIAdapter(this);
		popup.setMessage("gotInvite", chatID, "Do you want to join" + chatID);
		FragmentManager fm = gui.getFragmentManager();
		popup.show(fm,"tag");
		
	}

	@Override
	public void gotChatStarted(String chatId) {
		gui.gotoOwnChatView();
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
		//gui.(chatterID + " Accepted your invite.");
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
		//gui.DebugLog("your request to join a chat room was rejected by user: " + chatterID);

	}
	
	@Override
	/**
	 * Other user has joint the chat room
	 */
	public void gotParticipantEntered(String chatterID) {
		//gui.DebugLog(chatterID + ": has joint the chatroom.");

	}

	@Override
	/**
	 * Other user has left the chat room
	 */
	public void gotParticipantLeft(String chatterID) {
		//gui.DebugLog(chatterID + ": has left the chatroom.");
	}

	@Override
	/**
	 * The user that you have invited don't want to join your chat
	 */
	public void gotRequestCancelled(String chatterID) {
		//gui.DebugLog(chatterID + "don't want to join your chat!");
	}

	/**
	 * Other user wants to join my chat
	 * @param chatterID The joined Chatters ID
	 */
	@Override
	public void gotRequest(String chatterID) {
		Popup popup = new Popup();
		popup.setGUIAdapter(this);
		popup.setMessage("accUser", chatterID, "Accept " + chatterID + " to join?");
		FragmentManager fm = gui.getFragmentManager();
		popup.show(fm,"tag");
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
	
	public void popupOkPressed(String type, String item) {
		if(type.equals("gotInvite")) {
			state.onAcceptInvitation(item);
		} else {
			// own chat
			state.onAccept(item);
		}
	}
	
	public void popupCanclePressed(String type, String item) {
		if(type.equals("gotInvite")) {
			state.onDeny(item);;
		} else {
			// own chat
			state.onReject(item);
		}
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
	public void buttonSendPressed(String message) {
		state.onChat(message);
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
		debug(error);
	}

	public ChatClientState getState() {
		return state;
	}
	
	public ChatClientState debugGetState() {
		return state;
	}

	public void onConnect(String url, int port, String user, String pw) {
		state.onConnect(url, port, user, pw);
		debug(url + "\t" + port + "\t" + user + " \t" + pw);
	}

	public void onNewChat(String chatText) {
		state.onChat(chatText);
	}

	@Override
	public void debug(String debug) {
		// TODO Auto-generated method stub
		
	}

}
