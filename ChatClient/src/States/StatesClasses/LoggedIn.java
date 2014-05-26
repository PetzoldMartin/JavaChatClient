package States.StatesClasses;

import java.util.ArrayList;

import javax.jms.JMSException;

import States.ChatClientState;

public class LoggedIn extends ChatClientState {

	public LoggedIn(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void gotChatClosed() {
		// TODO Sinnlos in diesem State da kein chat offen
		messageReceiver.gotChatClosed();
	}

	@Override
	public void gotDenied(String CNN) {
		messageReceiver.gotDenied(CNN);
	}

	@Override
	public void gotChatters(ArrayList<String> chatters) {

	}

	@Override
	public void gotChats(ArrayList<String> chatsWithOwner) {

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
