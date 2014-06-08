package g13.state.client.wait;

import javax.jms.JMSException;

import g13.state.ChatClientState;
import g13.state.client.LoggedIn;
import g13.state.client.chat.InOtherChat;


public class Requesting extends AbstractWaiting {

	public Requesting(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see States.ChatClientState#gotRejected()
	 */
	@Override
	public void gotRejected(String chatterID) {
		new LoggedIn(this);
		messageReceiver.gotRejected(chatterID);

	}

	@Override
	public void gotParticipating() {
		new InOtherChat(this);
		messageReceiver.gotParticipating();

	}

	@Override
	public void onCancel() {
		try {
			messageProducer.cancel();
			new LoggedIn(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// unexpectedEvent();
	}
}
