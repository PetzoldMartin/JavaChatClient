package g13.state.client.chat;


import g13.state.ChatClientState;


public class AbstractChatting extends ChatClientState {

	public AbstractChatting(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onChat(String messageText) {
		try {
			messageProducer.chat(messageText);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void gotNewChat(String chatterID, String messageText) {
		messageReceiver.gotNewChat(chatterID, messageText);
	}

	@Override
	public void gotParticipantEntered(String chatterID) {
		messageReceiver.gotParticipantEntered(chatterID);
	}

	@Override
	public void gotParticipantLeft(String chatterID) {
		messageReceiver.gotParticipantLeft(chatterID);
	}
}
