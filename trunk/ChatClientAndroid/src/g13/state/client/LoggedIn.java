package g13.state.client;

import g13.state.ChatClientState;
import g13.state.client.connection.Connected;
import g13.state.client.wait.Invited;
import g13.state.client.wait.Requesting;

import java.util.ArrayList;


public class LoggedIn extends Connected {

	public LoggedIn(ChatClientState oldState) {
		super(oldState);
	}

	// @Override TODO needed? no, because its not
	// interesting for the user in this state
	// public void gotChatClosed() {
	// messageReceiver.gotChatClosed();
	// }

	@Override
	public void gotChats(ArrayList<String> chatsWithOwner) {
		messageReceiver.gotChats(chatsWithOwner);
	}

	@Override
	public void onLogout() {
		try {
			messageProducer.logout();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onRequest(String chatRoomID) {
		try {
			messageProducer.requestParticipian(chatRoomID);
			new Requesting(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onStartChat() {
			new WaitForChat(this);
	}

	@Override
	public void gotLogout() {
		messageReceiver.gotLogout();
		new NotLoggedIn(this);
	}

	@Override
	public void onAskForChats() {
		try {
			messageProducer.askForChats();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void gotInvite(String chatterID, String chatRoomID) {
		messageReceiver.gotInvite(chatterID, chatRoomID);
		new Invited(this);
	}

	@Override
	public void setView() {
		messageReceiver.gotSuccess();
	}

}
