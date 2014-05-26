package States.StatesClasses.WaitingStates;

import States.ChatClientState;
import States.StatesClasses.Waiting;

public abstract class Requesting extends Waiting {

	public Requesting(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public abstract void gotRejected();

	@Override
	public abstract void gotParticipating();

	public abstract void onCancel();
}
