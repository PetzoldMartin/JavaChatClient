package States.StatesClasses;

import gui.ChatSwingClient;
import messaging.ChatJmsAdapter;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;
import States.ChatClientState;

public abstract class Chatting extends ChatClientState {

	

	
	public Chatting(ChatJmsAdapter messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}
	public abstract void onChat();
	public abstract void gotNewChat();
	public abstract void gotParticipantEntered();
	public abstract void gotParticipantLeft();
}
