package States.StatesClasses;

import java.util.ArrayList;

import javax.jms.JMSException;

import States.ChatClientState;
import States.StatesClasses.WaitingStates.Invited;
import States.StatesClasses.WaitingStates.Requesting;

public class LoggedIn extends ChatClientState {

	public LoggedIn(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	// @Override TODO needed? no, because its not
	// interesting for the user in this state
	// public void gotChatClosed() {
	// messageReceiver.gotChatClosed();
	// }

	// @Override TODO needed? no, because its not
	// interesting for the user in this state
	// public void gotDenied(String CNN) {
	// messageReceiver.gotDenied(CNN);
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
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onStartChat() {
		try {
			messageProducer.startChat();
			new WaitForChat(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void gotInvite(String chatterID, String chatRoomID) {
		new Invited(this);
		messageReceiver.gotInvite(chatterID, chatRoomID);

	}

}
