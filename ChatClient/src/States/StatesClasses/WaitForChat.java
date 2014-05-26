package States.StatesClasses;

import States.ChatClientState;

public abstract class WaitForChat extends ChatClientState {

	public WaitForChat(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	public abstract void gotChatStarted();
}
