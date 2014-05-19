package States.StatesClasses;

import States.ChatClientState;

public abstract class LoggedIn extends ChatClientState {

	public abstract void gotChatClosed();
	public abstract void gotDenied();
	public abstract void gotSucess();
	public abstract void onLogout();
	public abstract void onRequest();
	public abstract void onStartchat();
	}
