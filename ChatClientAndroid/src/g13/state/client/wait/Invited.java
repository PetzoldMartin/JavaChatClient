package g13.state.client.wait;

import javax.jms.JMSException;

import g13.state.ChatClientState;
import g13.state.client.LoggedIn;
import g13.state.client.chat.InOtherChat;


public class Invited extends AbstractWaiting {

	/**
	 * @param oldState
	 */
	public Invited(ChatClientState oldState) {
		super(oldState);
		// TODO Auto-generated constructor stub
	}

	/**
	 * deny an invite
	 */
	@Override
	public void onDeny(String Chatroomid) {
		try {
			messageProducer.deny(Chatroomid);
			new LoggedIn(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onAcceptInvitation(String request) {
		try {
			messageProducer.acceptInvitation(request);
			new InOtherChat(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
