package States.StatesClasses.ChattingStates;

import javax.jms.JMSException;

import States.ChatClientState;

public abstract class AbstractChatting extends ChatClientState {

	public AbstractChatting(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onChat(String messageText) {
		try {
			messageProducer.chat(messageText);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void gotNewChat(String Chatter, String messageText) {
		messageReceiver.gotNewChat(Chatter, messageText);
	}

	public void gotParticipantEntered() {

		unexpectedEvent();
	}

	public void gotParticipantLeft() {
		unexpectedEvent();
	}
}
