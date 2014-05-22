package States.StatesClasses;

import gui.ChatSwingClient;
import messaging.ChatJmsAdapter;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;
import States.ChatClientState;

public abstract class NotLoggedIn extends ChatClientState {

	public NotLoggedIn(ChatJmsAdapter messageProducer,
			ChatSwingClient messageReceiver) {
		super(messageProducer, messageReceiver);
		System.out.println("State:NotLoggedin");
	}
	@Override
	public void gotFail(){
		messageReceiver.gotFail();
	}
	@Override
	public void gotSucess(){
		messageReceiver.gotSuccess();
		super.changeState(new LoggedIn(messageProducer, messageReceiver) {
		});
	}
	@Override
	public  void onRegister(String username , String passwort){
		try {
			messageProducer.register(username, passwort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public  void onLogin(String username , String passwort){
		try {
			messageProducer.login(username, passwort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
