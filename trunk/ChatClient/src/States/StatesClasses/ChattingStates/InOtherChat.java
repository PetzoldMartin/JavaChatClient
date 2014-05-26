package States.StatesClasses.ChattingStates;

import messaging.logic.ChatJmsAdapter;
import messaging.logic.ChatSwingClient;
import States.StatesClasses.Chatting;

public abstract class InOtherChat extends Chatting {

	public InOtherChat(ChatJmsAdapter messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}

	@Override
	public abstract void onLeave();

	@Override
	public abstract void gotChatClosed();
}
