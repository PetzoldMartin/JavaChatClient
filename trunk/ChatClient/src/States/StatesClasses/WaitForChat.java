package States.StatesClasses;

import States.ChatClientState;

public abstract class WaitForChat extends ChatClientState {

	public abstract void gotChatStarted();
}
