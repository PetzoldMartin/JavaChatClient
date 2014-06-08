package g13.state.client;

import g13.state.client.chat.InOwnChat;
import g13.state.ChatClientState;

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
