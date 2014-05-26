package States.StatesClasses.WaitingStates;

import messaging.logic.ChatJmsAdapter;
import messaging.logic.ChatSwingClient;
import States.StatesClasses.Waiting;

public abstract class Requesting extends Waiting {

	public Requesting(ChatJmsAdapter messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}

	@Override
	public abstract void gotRejected();

	@Override
	public abstract void gotParticipating();

	public abstract void onCancel();
}
