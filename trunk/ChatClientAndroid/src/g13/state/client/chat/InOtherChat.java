package g13.state.client.chat;



import g13.state.ChatClientState;
import g13.state.client.LoggedIn;


public class InOtherChat extends AbstractChatting {

	public InOtherChat(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onLeave() {
		try {
			messageProducer.leave();
			new LoggedIn(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void gotChatClosed() {
		messageReceiver.gotChatClosed();
		new LoggedIn(this);
	}

	@Override
	public void setView() {
		messageReceiver.gotParticipating();
	}
}
