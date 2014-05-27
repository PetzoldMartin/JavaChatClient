package States.StatesClasses.ChattingStates;

import java.util.ArrayList;

import javax.jms.JMSException;

import States.ChatClientState;

public class InOwnChat extends AbstractChatting {

	public InOwnChat(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void gotChatters(ArrayList<String> chatters) {
		messageReceiver.gotChatters(chatters);
	}
	public void gotRequestCancelled() {
		unexpectedEvent();
	}

	public void gotRequest() {
		unexpectedEvent();
	}

	public void gotAccepted() {
		unexpectedEvent();
	}

	public void gotDenied() {
		unexpectedEvent();
	}

	@Override
	public void onAccept() {
		unexpectedEvent();
	}

	public void onReject() {
		unexpectedEvent();
	}

	public void onInvite() {
		unexpectedEvent();
	}

	@Override
	public void onChatClose() {
		unexpectedEvent();
	}
	
	@Override
	public void onAskForChatters() {
		try {
			messageProducer.askForChatters();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
