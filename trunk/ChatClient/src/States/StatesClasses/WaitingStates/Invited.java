package States.StatesClasses.WaitingStates;

import javax.jms.JMSException;

import States.ChatClientState;
import States.StatesClasses.LoggedIn;
import States.StatesClasses.ChattingStates.InOtherChat;

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
	public void onDeny(){
		try {
			messageProducer.deny();
			new LoggedIn(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onAcceptInvitataion() {
		try {
			messageProducer.acceptInvitation();
			new InOtherChat(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
