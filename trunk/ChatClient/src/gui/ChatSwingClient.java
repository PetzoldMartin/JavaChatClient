package gui;


import messaging.ChatServerMessageReceiver;

public class ChatSwingClient implements ChatServerMessageReceiver{

	private static ChatSwingClient chatSwingClient = null;
	
	private ChatSwingClient(){
		
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
	
	public void onLoginOut() {
		
	}
	
	public static ChatSwingClient getInstance()
	{
		if(chatSwingClient==null){
			chatSwingClient= new ChatSwingClient();
		}
		return chatSwingClient;
	}

}
