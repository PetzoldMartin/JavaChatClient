package g13.state.client.connection;

import g13.state.ChatClientState;

public class Connected extends ChatClientState { // TODO let all connected
													// extends from this

	public Connected(ChatClientState oldState) {
		super(oldState);
	}

	@Override
	public void restore() {
		super.restore(); // TODO main restore function for all substates
	}
}
