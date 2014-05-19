package States.StatesClasses.ChattingStates;

import States.StatesClasses.Chatting;

public abstract class InOtherChat extends Chatting {

	public abstract void onLeave();
	public abstract void gotChatClosed();
}
