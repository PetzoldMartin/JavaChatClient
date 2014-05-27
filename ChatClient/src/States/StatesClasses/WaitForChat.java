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
		new InOwnChat(this);
		messageReceiver.gotChatStarted(chatID);

	}
}
