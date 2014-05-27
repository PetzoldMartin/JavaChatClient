package States.StatesClasses.WaitingStates;

import States.ChatClientState;
import States.StatesClasses.LoggedIn;

public abstract class AbstractWaiting extends ChatClientState {

	public AbstractWaiting(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void gotChatClosed() {
		messageReceiver.gotChatClosed();
		new LoggedIn(this);
	}
}
