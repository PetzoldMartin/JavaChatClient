package g13.state.client.connection;

import g13.state.ChatClientState;

public class Connected extends ChatClientState {

	
	private static ChatClientState state;
	public Connected(ChatClientState oldState) {
		super(oldState);
	}
}
