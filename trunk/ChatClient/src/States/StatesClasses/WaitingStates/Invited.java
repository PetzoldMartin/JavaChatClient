package States.StatesClasses.WaitingStates;

import messaging.logic.ChatJmsAdapter;
import messaging.logic.ChatSwingClient;
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
