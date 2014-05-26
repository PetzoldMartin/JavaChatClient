package States.StatesClasses;

import States.ChatClientState;

public class WaitForChat extends ChatClientState {

	public WaitForChat(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void gotChatStarted(String chatID) {
		unexpectedEvent();
		System.out.println(chatID);
	}
}
