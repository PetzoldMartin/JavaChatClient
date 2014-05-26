package States.StatesClasses;

import messaging.interfaces.ChatServerMessageProducer;
import messaging.interfaces.ChatServerMessageReceiver;
import States.ChatClientState;

public abstract class NotLoggedIn extends ChatClientState {

	public NotLoggedIn(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	public NotLoggedIn(ChatServerMessageProducer messageProducer,
			ChatServerMessageReceiver messageReceiver) {
		super(messageProducer, messageReceiver);
	}

	@Override
	public void gotFail() {
		messageReceiver.gotFail();
	}

	@Override
	public void gotSucess() {
		messageReceiver.gotSuccess();
		new LoggedIn(this) {
		};
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
