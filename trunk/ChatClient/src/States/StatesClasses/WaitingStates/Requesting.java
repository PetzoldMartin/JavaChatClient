package States.StatesClasses.WaitingStates;

import javax.jms.JMSException;

import States.ChatClientState;
import States.StatesClasses.LoggedIn;
import States.StatesClasses.ChattingStates.InOtherChat;

public class Requesting extends AbstractWaiting {

	public Requesting(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void gotRejected() {
		unexpectedEvent();
	}

	@Override
	public void gotParticipating() {
		messageReceiver.gotParticipating();
		new InOtherChat(this);
	}

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
