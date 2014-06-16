package g13.state.client.wait;


import g13.state.ChatClientState;
import g13.state.client.LoggedIn;
import g13.state.client.chat.InOtherChat;


public class Requesting extends AbstractWaiting {

	public Requesting(ChatClientState oldState) {
		super(oldState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see States.ChatClientState#gotRejected()
	 */
	@Override
	public void gotRejected(String chatterID) {
		messageReceiver.gotRejected(chatterID);
		new LoggedIn(this);
	}

	@Override
	public void gotParticipating() {
		messageReceiver.gotParticipating();
		new InOtherChat(this);
	}

	@Override
	public void onCancel() {
		try {
			messageProducer.cancel();
			new LoggedIn(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// unexpectedEvent();
	}
}
