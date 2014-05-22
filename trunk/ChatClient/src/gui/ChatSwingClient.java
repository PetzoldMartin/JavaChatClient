package gui;

import States.ChatClientState;
import messaging.ChatJmsAdapter;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;

public class ChatSwingClient implements ChatServerMessageReceiver{

	// Singleton instance
	private static ChatSwingClient chatSwingClient=null;
	private ChatServerMessageProducer messageProducer;
	
	private SwingWindow gui;
	private ChatClientState state;
	
	public ChatSwingClient() {
		myInit();
	}
	private void myInit() {
		messageProducer = ChatJmsAdapter.getInstance();
		messageProducer.setMessageReceiver(this);
		String localConnection = "tcp://localhost:61616";
		((ChatJmsAdapter) messageProducer).connectToServer(localConnection);
		
		state = new ChatClientState() {
		};


		gui = new SwingWindow(this);
	}
	
	@Override
	public void gotSuccess() {
		gui.AddLineToLog("System: sucess");
	}

	@Override
	public void gotFail() {
		gui.AddLineToLog("System: fail");
	}

	@Override
	public void gotLogout() {
		gui.AddLineToLog("System: Logout");
	}

	@Override
	public void gotChatClosed() {
		gui.AddLineToLog("System: Chat closed");
	}

	@Override
	public void gotInvite() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotReject() {
		// TODO Auto-generated method stub
	}

	@Override
	public void gotChatStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gotParticipating() {
		// TODO Auto-generated method stub
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
	
	/**
	 * Button for Login is Pressed
	 */
	public void buttonLoginPressed() {
		String name = gui.getName();
		String password = gui.getPassword();
		
		state.onLogin(name, password);
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
