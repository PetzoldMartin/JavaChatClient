package States.StatesClasses.WaitingStates;

import States.StatesClasses.Waiting;

public abstract class Invited extends Waiting {

	public abstract void onDeny();
	public abstract void onAcceptInvitataion();
}
