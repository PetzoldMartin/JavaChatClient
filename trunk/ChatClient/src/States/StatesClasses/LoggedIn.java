package States.StatesClasses;

import gui.ChatSwingClient;
import messaging.ChatJmsAdapter;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;
import States.ChatClientState;

public abstract class LoggedIn extends ChatClientState {

	
	
	
	public LoggedIn(ChatJmsAdapter messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		System.out.println("State:Loggedin");
		// TODO Auto-generated constructor stub
	}
	public  void gotChatClosed(){
		
	}
	public  void gotDenied(){
		
	}
	public  void gotSucess(){
		
	}
	@Override
	public  void onLogout(){
		try {
			messageProducer.logout();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public  void onRequest(){
		
	}
	public  void onStartchat(){
	}	
	
	@Override
	public void gotLogout(){
		messageReceiver.gotLogout();
		super.changeState(new NotLoggedIn(messageProducer, messageReceiver) {
		});
	}
	}
