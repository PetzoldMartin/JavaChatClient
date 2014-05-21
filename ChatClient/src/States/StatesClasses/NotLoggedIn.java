package States.StatesClasses;

import messaging.ChatJmsAdapter;
import States.ChatClientState;

public abstract class NotLoggedIn extends ChatClientState {

	public NotLoggedIn() {
		super();
	}

	public void gotFail(){
	this.chatSwingClient.gotFail();
	}
	public abstract void gotSucess();

	public abstract void onRegister();
	public abstract void onLogin();
	
}
