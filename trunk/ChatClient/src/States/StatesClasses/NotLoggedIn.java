package States.StatesClasses;

import States.ChatClientState;

public abstract class NotLoggedIn extends ChatClientState {


	public void gotFail(){
	
	}
	public abstract void gotSucess();

	public abstract void onRegister();
	public abstract void onLogin();
	
}
