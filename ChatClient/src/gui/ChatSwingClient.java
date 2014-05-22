package gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import States.ChatClientState;
import messaging.ChatJmsAdapter;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;

public class ChatSwingClient implements ChatServerMessageReceiver{

	// Singleton instance
	private static ChatSwingClient chatSwingClient;
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

		// Create GUI and run it on a new Thread
		gui = new SwingWindow(this);
	}
	
	@Override
	public void gotSuccess() {
		
		System.out.println("sucess");
		gui.AddLineToLog("sucess");
		
	}

	@Override
	public void gotFail() {
		System.out.println("fail");
		
	}

	@Override
	public void gotLogout() {
		System.out.println("Logout");
		
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
		System.out.println("Accepted");
		
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

}
