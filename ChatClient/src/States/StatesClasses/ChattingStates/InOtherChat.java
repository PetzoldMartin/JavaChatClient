package States.StatesClasses.ChattingStates;

import States.ChatClientState;

public class InOtherChat extends AbstractChatting {

	public InOtherChat(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onLeave() {
		unexpectedEvent();
	}

	@Override
	public void gotChatClosed() {
		unexpectedEvent();
	}
}
