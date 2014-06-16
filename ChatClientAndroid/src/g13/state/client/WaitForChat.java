package g13.state.client;

import g13.state.ChatClientState;
import g13.state.client.chat.InOwnChat;

public class WaitForChat extends ChatClientState {

	public WaitForChat(ChatClientState oldState) {
		super(oldState);
	}

	@Override
	public void gotChatStarted(String chatID) {
		new InOwnChat(this);
		messageReceiver.gotChatStarted(chatID);
	}
}
