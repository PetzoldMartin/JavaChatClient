package States.StatesClasses;

import States.ChatClientState;

public abstract class Waiting extends ChatClientState {

	public Waiting(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public abstract void gotChatClosed();
}
