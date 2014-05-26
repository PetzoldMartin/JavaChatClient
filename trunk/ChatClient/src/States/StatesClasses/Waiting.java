package States.StatesClasses;

import messaging.logic.ChatJmsAdapter;
import messaging.logic.ChatSwingClient;
import States.ChatClientState;

public abstract class Waiting extends ChatClientState {

	public Waiting(ChatJmsAdapter messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}

	@Override
	public abstract void gotChatClosed();
}
