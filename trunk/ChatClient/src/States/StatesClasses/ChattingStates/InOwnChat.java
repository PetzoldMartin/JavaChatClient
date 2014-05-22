package States.StatesClasses.ChattingStates;

import gui.ChatSwingClient;
import messaging.ChatJmsAdapter;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;
import States.StatesClasses.Chatting;

public abstract class InOwnChat extends Chatting {

	
	

	public InOwnChat(ChatJmsAdapter messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}
	public abstract void gotRequestCancelled();
	public abstract void gotRequest();
	public abstract void gotAccepted();
	public abstract void gotDenied();
	public abstract void onAccept();
	public abstract void onReject();
	public abstract void onInvite();
	public abstract void onChatClose();
	
}
