package States.StatesClasses;

import messaging.logic.ChatJmsAdapter;
import messaging.logic.ChatSwingClient;
import States.ChatClientState;

public abstract class WaitForChat extends ChatClientState {

	public WaitForChat(ChatJmsAdapter messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}

	public abstract void gotChatStarted();
}
