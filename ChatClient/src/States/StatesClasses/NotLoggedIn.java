package States.StatesClasses;

import gui.ChatSwingClient;
import messaging.ChatJmsAdapter;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;
import States.ChatClientState;

public abstract class NotLoggedIn extends ChatClientState {

	
	

	
	public NotLoggedIn(ChatServerMessageProducer messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}
	public void gotFail(){
	}
	public void gotSucess(){
		
	}

	public  void onRegister(){
		
	}
	public  void onLogin(){
		
	}
	
}
