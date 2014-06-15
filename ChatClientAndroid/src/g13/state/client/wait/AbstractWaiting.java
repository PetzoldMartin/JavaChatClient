package g13.state.client.wait;

import g13.state.ChatClientState;
import g13.state.client.LoggedIn;


public class AbstractWaiting extends ChatClientState {

	public AbstractWaiting(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void gotChatClosed() {
		messageReceiver.gotChatClosed();
		new LoggedIn(this);
	}
}