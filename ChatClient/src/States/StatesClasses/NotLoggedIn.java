package States.StatesClasses;

import States.ChatClientState;

public abstract class NotLoggedIn extends ChatClientState {

	public abstract void gotFail();
	public abstract void onRegister();
	public abstract void onLogin();
	
}
