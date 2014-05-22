package States.StatesClasses.WaitingStates;

import gui.ChatSwingClient;
import messaging.ChatJmsAdapter;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;
import States.StatesClasses.Waiting;

public abstract class Requesting extends Waiting {

	
	
	
	public Requesting(ChatJmsAdapter messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}
	public abstract void gotRejected();
	public abstract void gotParticipating();
	public abstract void onCancel();
}
