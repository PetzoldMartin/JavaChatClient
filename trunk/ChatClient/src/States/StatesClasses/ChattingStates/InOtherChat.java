package States.StatesClasses.ChattingStates;

import javax.jms.JMSException;

import States.ChatClientState;
import States.StatesClasses.LoggedIn;

public class InOtherChat extends AbstractChatting {

	public InOtherChat(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onLeave() {
		try {
			messageProducer.leave();
			new LoggedIn(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void gotChatClosed() {
		new LoggedIn(this);
		messageReceiver.gotChatClosed();

	}
}
