package States.StatesClasses.ChattingStates;

import gui.ChatSwingClient;
import messaging.ChatJmsAdapter;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;
import States.StatesClasses.Chatting;

public abstract class InOtherChat extends Chatting {

	
	

	public InOtherChat(ChatJmsAdapter messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}
	public abstract void onLeave();
	public abstract void gotChatClosed();
}
