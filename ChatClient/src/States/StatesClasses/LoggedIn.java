package States.StatesClasses;

import java.util.ArrayList;

import gui.ChatSwingClient;
import messaging.ChatChatterRelationship;
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
	@Override
	public  void gotChatClosed(){
		messageReceiver.gotChatClosed();
	}
	@Override
	public  void gotDenied(String CNN){
		messageReceiver.gotDenied(CNN);
	}
	@Override
	public  void gotChatters(ArrayList<String> chatters){
		
	}
	@Override
	public  void gotChats(ArrayList<ChatChatterRelationship> chatsAndChatters){
		
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
	@Override
	public  void onRequest(String theParticipant){
		
	}
	@Override
	public  void onStartChat(){
	}	
	
	@Override
	public void gotLogout(){
		messageReceiver.gotLogout();
		super.changeState(new NotLoggedIn(messageProducer, messageReceiver) {
		});
	}
	}
