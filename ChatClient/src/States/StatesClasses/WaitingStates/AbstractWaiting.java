package States.StatesClasses.WaitingStates;

import States.ChatClientState;

public abstract class AbstractWaiting extends ChatClientState {

	public AbstractWaiting(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void gotChatClosed() {
		unexpectedEvent();
	}
}
