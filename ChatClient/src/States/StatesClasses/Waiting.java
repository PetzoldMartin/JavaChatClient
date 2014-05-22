package States.StatesClasses;

import gui.ChatSwingClient;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;
import States.ChatClientState;

public abstract class Waiting extends ChatClientState {
	
 



public Waiting(ChatServerMessageProducer messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}

public abstract void gotChatClosed();
}
