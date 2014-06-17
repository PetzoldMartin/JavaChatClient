package g13.state.client;

import g13.state.ChatClientState;
import g13.state.client.chat.InOwnChat;
import g13.state.client.connection.Connected;

public class WaitForChat extends Connected {

	public WaitForChat(ChatClientState oldState) {
		super(oldState);
	}

	@Override
	public void gotChatStarted(String chatID) {
		messageReceiver.gotChatStarted(chatID);
		new InOwnChat(this);
	}

	@Override
	public void setView() {
		try {
			messageProducer.startChat();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
