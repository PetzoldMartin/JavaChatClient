package g13.state.client.connection;

import g13.message.interfaces.ChatServerMessageProducer;
import g13.message.interfaces.ChatServerMessageReceiver;
import g13.state.ChatClientState;
import g13.state.client.NotLoggedIn;

public class NotConnected extends ChatClientState {

	public NotConnected(ChatClientState oldState) {
		super(oldState);
	}

	public NotConnected(ChatServerMessageProducer messageProducer,
			ChatServerMessageReceiver messageReceiver) {
		super(messageProducer, messageReceiver);
	}

	@Override
	public void onConnect(String url, int port, String user, String pw) {
		messageProducer.connectToServer(url, port, user, pw);
		this.url = url;
		this.port = port;
		this.user = user;
		this.pw = pw;
	}

	@Override
	public void gotConnectSuccess() {
		new NotLoggedIn(this);
	}

	@Override
	public void gotConnectFailture(String error) {
		messageReceiver.error(error);
	}

	@Override
	public void setView() {
		messageReceiver.gotConnectFail();
	}
}
