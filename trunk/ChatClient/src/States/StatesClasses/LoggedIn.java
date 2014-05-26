package States.StatesClasses;

import java.util.ArrayList;

import javax.jms.JMSException;

import States.ChatClientState;
import States.StatesClasses.WaitingStates.Requesting;

public class LoggedIn extends ChatClientState {

	public LoggedIn(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	// @Override TODO needed?
	// public void gotChatClosed() {
	// messageReceiver.gotChatClosed();
	// }

	// @Override TODO needed?
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
	public void onRequest(String theParticipant) {
		try {
			messageProducer.requestParticipian(theParticipant);
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
}
