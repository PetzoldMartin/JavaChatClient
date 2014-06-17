package g13.state.client.connection;

import g13.state.ChatClientState;

public abstract class Connected extends ChatClientState {

	public Connected(ChatClientState oldState) {
		super(oldState);
	}

	@Override
	public void serviceBound() {
		super.serviceBound();
		messageProducer.connectToServer(url, port, user, pw);
		// setView();
	}

	@Override
	public void gotConnectSuccess() {
		// setView();
	}

	// protected abstract void setView();
}
