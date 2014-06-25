package g13.state.client.wait;

import g13.state.ChatClientState;
import g13.state.client.LoggedIn;
import g13.state.client.connection.Connected;


public abstract class AbstractWaiting extends Connected {

	public AbstractWaiting(ChatClientState oldState) {
		super(oldState);
	}

	@Override
	public void gotChatClosed() {
		messageReceiver.gotChatClosed();
		new LoggedIn(this);
	}

	@Override
	public void setView() {
		messageReceiver.setWaiting();
	}
}
