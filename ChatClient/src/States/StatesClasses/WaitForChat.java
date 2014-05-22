package States.StatesClasses;

import gui.ChatSwingClient;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;
import States.ChatClientState;

public abstract class WaitForChat extends ChatClientState {

	

	

	public WaitForChat(ChatServerMessageProducer messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}

	public abstract void gotChatStarted();
}
