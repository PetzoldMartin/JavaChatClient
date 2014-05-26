package States.StatesClasses.WaitingStates;

import States.ChatClientState;

public abstract class Invited extends AbstractWaiting {

	public Invited(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	public abstract void onDeny();

	public abstract void onAcceptInvitataion();
}
