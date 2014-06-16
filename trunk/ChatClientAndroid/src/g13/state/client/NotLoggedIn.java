package g13.state.client;

import g13.state.ChatClientState;

public class NotLoggedIn extends ChatClientState {

	public NotLoggedIn(ChatClientState oldState) {
		super(oldState);
	}

	@Override
	public void gotFail() {
		messageReceiver.gotFail();
	}

	@Override
	public void gotSucess() {
		messageReceiver.gotSuccess();
		new LoggedIn(this);

	}

	@Override
	public void onRegister(String username, String passwort) {
		try {
			messageProducer.register(username, passwort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLogin(String username, String passwort) {
		try {
			messageProducer.login(username, passwort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
