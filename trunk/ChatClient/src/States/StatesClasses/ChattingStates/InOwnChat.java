package States.StatesClasses.ChattingStates;

import States.ChatClientState;

public class InOwnChat extends AbstractChatting {

	public InOwnChat(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	public void gotRequestCancelled() {
		unexpectedEvent();
	}

	public void gotRequest() {
		unexpectedEvent();
	}

	public void gotAccepted() {
		unexpectedEvent();
	}

	public void gotDenied() {
		unexpectedEvent();
	}

	@Override
	public void onAccept() {
		unexpectedEvent();
	}

	public void onReject() {
		unexpectedEvent();
	}

	public void onInvite() {
		unexpectedEvent();
	}

	@Override
	public void onChatClose() {
		unexpectedEvent();
	}

}
