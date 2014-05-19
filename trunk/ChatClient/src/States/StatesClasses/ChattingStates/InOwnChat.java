package States.StatesClasses.ChattingStates;

import States.StatesClasses.Chatting;

public abstract class InOwnChat extends Chatting {

	public abstract void gotRequestCancelled();
	public abstract void gotRequest();
	public abstract void gotAccepted();
	public abstract void gotDenied();
	public abstract void onAccept();
	public abstract void onReject();
	public abstract void onInvite();
	public abstract void onChatClose();
	
}
