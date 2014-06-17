package g13.state.client;

import g13.state.ChatClientState;
import g13.state.client.connection.Connected;

public class NotLoggedIn extends Connected {

	public NotLoggedIn(ChatClientState oldState) {
		super(oldState);
	}

	@Override
	public void gotFail() {
		messageReceiver.gotFail();
		messageReceiver.error("fail to login");
	}

	@Override
	public void gotSucess() {
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

	@Override
	public void setView() {
		messageReceiver.gotConnectSuccess();
	}
}
