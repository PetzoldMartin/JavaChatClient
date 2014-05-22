package gui;

import java.awt.Color;

import States.ChatClientState;
import States.StatesClasses.Chatting;
import States.StatesClasses.NotLoggedIn;
import States.StatesClasses.Waiting;
import messaging.ChatJmsAdapter;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;

public class ChatSwingClient implements ChatServerMessageReceiver{

	// Singleton instance
	private static ChatSwingClient chatSwingClient=null;
	private ChatJmsAdapter messageProducer;
	
	// client info
	private String userName;
	
	private SwingWindow gui;
	private ChatClientState state;
	
	public ChatSwingClient() {
		myInit();
	}
	private void myInit() {
		messageProducer = ChatJmsAdapter.getInstance();
		messageProducer.setMessageReceiver(this);
		
		
		state=new NotLoggedIn(messageProducer,this) {
		};
		messageProducer.setState(state);
		String localConnection = "tcp://localhost:61616";
		((ChatJmsAdapter) messageProducer).connectToServer(localConnection);
		


		gui = new SwingWindow(this);
	}
	
	@Override
	public void gotSuccess() {
		gui.AddLineToLog("System: sucess");
		// user is logged in
	}

	@Override
	public void gotFail() {
		// login fail
		gui.AddLineToLog("System: fail");
		gui.SetStatusColor(Color.RED);
	}

	@Override
	public void gotLogout() {
		gui.AddLineToLog("System: Logout");
		gui.SetStatusColor(Color.YELLOW);
	}

	@Override
	public void gotChatClosed() {
		gui.AddLineToLog("System: Chat closed");
		gui.SetStatusColor(Color.YELLOW);
	}

	@Override
	public void gotInvite() {
		// TODO Auto-generated method stub
		gui.SetStatusColor(Color.CYAN);
	}

	@Override
	public void gotReject() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotChatStarted() {
		// in own chat
		gui.SetStatusColor(Color.GREEN);
	}

	@Override
	public void gotParticipating() {
		// in other char
		gui.SetStatusColor(Color.GREEN);
	}

	@Override
	public void gotNewChat() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotParticipantEntered() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotParticipantLeft() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotRequestCancelled() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotRequest() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotDenied() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotAccepted() {
		gui.AddLineToLog("System: Accepted");
		
	}
	
	// START BUTTON PRESSED AREA ////////////////
	
	
	public void buttonRegisterPressed() {
		userName = gui.getName();
		
		// get password
		String password = gui.getPassword();
		
		// call register on state
		state.onRegister(userName, password);
		
		// clear password for safety
		gui.SetPasswordField("");
	}
	
	/**
	 * Button for Login is Pressed
	 */
	public void buttonLoginPressed() {
		userName = gui.getName();
		String password = gui.getPassword();
		
		state.onLogin(userName, password);
	}
	
	/**
	 * Button for Logout is Pressed
	 */
	public void buttonLogoutPressed() {
		state.onLogout();
	}
	
	/**
	 * Button to send a message is Pressed
	 */
	public void btnSendPressed() {
		String message = gui.getMessage();
		
		// Add message to Log
		gui.AddLineToLog(userName + ": " + message);
		
		//state.onChat(message);
	}
	
	public static ChatSwingClient getInstance()
	{
		if(chatSwingClient==null){
			chatSwingClient= new ChatSwingClient();
		}
		return chatSwingClient;
	}

	public void setState(ChatClientState cCS){
		state=cCS;
	}
}
