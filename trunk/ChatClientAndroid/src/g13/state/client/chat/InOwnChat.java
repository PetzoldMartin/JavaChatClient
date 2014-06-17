package g13.state.client.chat;

import g13.state.ChatClientState;
import g13.state.client.LoggedIn;

import java.util.ArrayList;


public class InOwnChat extends AbstractChatting {

	public InOwnChat(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void gotChatters(ArrayList<String> chatters) {
		messageReceiver.gotChatters(chatters);
	}

	@Override
	public void gotRequestCancelled(String chatterID) {
		messageReceiver.gotRequestCancelled(chatterID);
	}

	@Override
	public void gotRequest(String chatterID) {
		messageReceiver.gotRequest(chatterID);
	}

	@Override
	public void gotAccepted(String chatterID) {
		messageReceiver.gotAccepted(chatterID);
	}

	@Override
	public void gotDenied(String chatterID) {
		messageReceiver.gotDenied(chatterID);
	}

	@Override
	public void onAccept(String chatterID) {
		try {
			messageProducer.accept(chatterID);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onReject(String chatterID) {
		try {
			messageProducer.reject(chatterID);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onInvite(String chatterID) {
		try {
			messageProducer.invite(chatterID);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onChatClose() {
		try {
			messageProducer.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onAskForChatters() {
		try {
			messageProducer.askForChatters();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 @Override
 public void gotChatClosed(){
		messageReceiver.gotChatClosed();
		new LoggedIn(this);
	}

	@Override
	public void setView() {
		messageReceiver.gotChatStarted(null);
	}
	
}
