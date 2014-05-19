package States.StatesClasses.WaitingStates;

import States.StatesClasses.Waiting;

public abstract class Requesting extends Waiting {

	public abstract void gotRejected();
	public abstract void gotParticipating();
	public abstract void onCancel();
}
