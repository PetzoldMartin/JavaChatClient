package g13.state.client.wait;



import g13.state.ChatClientState;
import g13.state.client.LoggedIn;
import g13.state.client.chat.InOtherChat;


public class Invited extends AbstractWaiting {

	/**
	 * @param oldState
	 */
	public Invited(ChatClientState oldState) {
		super(oldState);
	}

	/**
	 * deny an invite
	 */
	@Override
	public void onDeny(String Chatroomid) {
		try {
			messageProducer.deny(Chatroomid);
			new LoggedIn(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onAcceptInvitation(String request) {
		try {
			messageProducer.acceptInvitation(request);
			messageReceiver.gotParticipating(); // TODO state changes in
												// constructor
			new InOtherChat(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setView() {
		// nothing
	}
}
