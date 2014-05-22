package States.StatesClasses.WaitingStates;

import gui.ChatSwingClient;
import messaging.ChatJmsAdapter;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;
import States.StatesClasses.Waiting;

public abstract class Invited extends Waiting {


	
	
	public Invited(ChatJmsAdapter messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}
	public abstract void onDeny();
	public abstract void onAcceptInvitataion();
}
