package States.StatesClasses.ChattingStates;

import States.ChatClientState;
import States.StatesClasses.Chatting;

public abstract class InOwnChat extends Chatting {

	public InOwnChat(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	public abstract void gotRequestCancelled();

	public abstract void gotRequest();

	public abstract void gotAccepted();

	public abstract void gotDenied();

	@Override
	public abstract void onAccept();

	public abstract void onReject();

	public abstract void onInvite();

	@Override
	public abstract void onChatClose();

}
