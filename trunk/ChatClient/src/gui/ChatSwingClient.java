package gui;


import States.ChatClientState;
import messaging.ChatServerMessageReceiver;

public class ChatSwingClient implements ChatServerMessageReceiver{

	// Singleton instance
	private static ChatSwingClient chatSwingClient = null;
	
	private SwingWindow gui;
	private ChatClientState state;
	
	private ChatSwingClient(){
		// Create GUI and run it on a new Thread
		gui = new SwingWindow();
		Thread threadWindow = new Thread(gui);
		threadWindow.start();
	}
	
	@Override
	public void gotSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gotFail() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gotLogout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gotChatClosed() {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Button for Login is Pressed
	 */
	public void buttonLoginPressed() {
		String name = gui.getName();
		String password = gui.getPassword();
		
		//state.onLogin(name, password);
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

}
