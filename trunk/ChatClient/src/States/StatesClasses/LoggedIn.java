package States.StatesClasses;

import gui.ChatSwingClient;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;
import States.ChatClientState;

public abstract class LoggedIn extends ChatClientState {

	
	
	public LoggedIn(ChatServerMessageProducer messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		// TODO Auto-generated constructor stub
	}
	public abstract void gotChatClosed();
	public abstract void gotDenied();
	public abstract void gotSucess();
	public abstract void onLogout();
	public abstract void onRequest();
	public abstract void onStartchat();
	}
