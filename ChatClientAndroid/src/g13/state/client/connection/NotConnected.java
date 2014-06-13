package g13.state.client.connection;

import g13.message.interfaces.ChatServerMessageProducer;
import g13.message.interfaces.ChatServerMessageReceiver;
import g13.state.ChatClientState;

public class NotConnected extends ChatClientState {

	public NotConnected(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	public NotConnected(ChatServerMessageProducer messageProducer,
			ChatServerMessageReceiver messageReceiver) {
		super(messageProducer, messageReceiver);
	}

	@Override
	public void onConnect(String url, int port, String user, String pw) {
		messageProducer.connectToServer(url, port, user, pw);
	}

	@Override
	public void gotConnectSuccess() {
		new Connected(this);
	}

	@Override
	public void gotConnectFailture(String error) {
		// TODO failture
	}
}
