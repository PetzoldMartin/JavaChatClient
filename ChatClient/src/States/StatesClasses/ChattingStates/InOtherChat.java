package States.StatesClasses.ChattingStates;

import States.ChatClientState;
import States.StatesClasses.Chatting;

public abstract class InOtherChat extends Chatting {

	public InOtherChat(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public abstract void onLeave();

	@Override
	public abstract void gotChatClosed();
}
