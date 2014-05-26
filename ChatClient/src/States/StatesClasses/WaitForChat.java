package States.StatesClasses;

import States.ChatClientState;
import States.StatesClasses.ChattingStates.InOwnChat;

public class WaitForChat extends ChatClientState {

	public WaitForChat(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void gotChatStarted(String chatID) {
		messageReceiver.gotChatStarted(chatID);
		new InOwnChat(this);
	}
}
