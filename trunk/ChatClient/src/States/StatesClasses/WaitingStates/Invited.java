package States.StatesClasses.WaitingStates;

import States.ChatClientState;
import States.StatesClasses.Waiting;

public abstract class Invited extends Waiting {

	public Invited(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	public abstract void onDeny();

	public abstract void onAcceptInvitataion();
}
