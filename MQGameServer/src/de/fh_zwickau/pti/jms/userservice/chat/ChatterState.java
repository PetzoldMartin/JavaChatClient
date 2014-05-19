/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice.chat;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * abstract Base type for Object-for-State Pattern
 * 
 * @author georg beier
 * 
 */
public abstract class ChatterState extends StateBase {

	public ChatterState(String name) {
		super(name);
	}

	/**
	 * select the appropriate event handler method for incoming method for class
	 * Chatroom
	 * 
	 * @param kind
	 *            enum defining kind of incoming method
	 * @param message
	 *            incoming jms method
	 * @return
	 * @throws JMSException
	 */
	protected boolean executeEventMethod(MessageKind kind, Message message)
			throws JMSException {
		switch (kind) {
		case authenticated:
			return authenticated(message);
		case chatterAccepted:
			return accepted(message);
		case chatterChatCreated:
			return chatCreated(message);
		case chatterClosed:
			return closed(message);
		case chatterDenied:
			return denied(message);
		case chatterInvited:
			return invited(message);
		case chatterNewChat:
			return newChat(message);
		case chatterParticipantEntered:
			return participantEntered(message);
		case chatterParticipantLeft:
			return participantLeft(message);
		case chatterParticipationRequest:
			return participationRequest(message);
		case chatterRequestCanceled:
			return requestCancelled(message);
		case chatterReject:
			return rejected(message);
		case chatterMsgAccept:
			return msgAccept(message);
		case chatterMsgAcceptInvitation:
			return msgAcceptInvitation(message);
		case chatterMsgCancel:
			return msgCancel(message);
		case chatterMsgChat:
			return msgChat(message);
		case chatterMsgClose:
			return msgClose(message);
		case chatterMsgDeny:
			return msgDeny(message);
		case chatterMsgInvite:
			return msgInvite(message);
		case chatterMsgLeave:
			return msgLeave(message);
		case chatterMsgReject:
			return msgReject(message);
		case chatterMsgRequestParticipation:
			return msgRequestParticipation(message);
		case chatterMsgStartChat:
			return msgStartChat(message);
		default:
			logUnexpectedMethod(message);
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return true if object will stay alive
	 * @throws JMSException
	 */
	protected boolean authenticated(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return true if object will stay alive
	 * @throws JMSException
	 */
	protected boolean accepted(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	protected boolean chatCreated(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return true if object will stay alive
	 * @throws JMSException
	 */
	protected boolean closed(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return true if object will stay alive
	 * @throws JMSException
	 */
	protected boolean denied(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return true if object will stay alive
	 * @throws JMSException
	 */
	protected boolean invited(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return true if object will stay alive
	 * @throws JMSException
	 */
	protected boolean newChat(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return true if object will stay alive
	 * @throws JMSException
	 */
	protected boolean participantEntered(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return true if object will stay alive
	 * @throws JMSException
	 */
	protected boolean participantLeft(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return true if object will stay alive
	 * @throws JMSException
	 */
	protected boolean participationRequest(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return true if object will stay alive
	 * @throws JMSException
	 */
	protected boolean requestCancelled(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return true if object will stay alive
	 * @throws JMSException
	 */
	protected boolean rejected(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 *  handle incoming jms messages from client
	 * @throws JMSException 
	 */
	protected boolean msgAccept(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 *  handle incoming jms messages from client
	 * @throws JMSException 
	 */
	protected boolean msgAcceptInvitation(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 *  handle incoming jms messages from client
	 * @throws JMSException 
	 */
	protected boolean msgCancel(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 *  handle incoming jms messages from client
	 * @throws JMSException 
	 */
	protected boolean msgChat(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 *  handle incoming jms messages from client
	 * @throws JMSException 
	 */
	protected boolean msgClose(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 *  handle incoming jms messages from client
	 * @throws JMSException 
	 */
	protected boolean msgDeny(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 *  handle incoming jms messages from client
	 * @throws JMSException 
	 */
	protected boolean msgInvite(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 *  handle incoming jms messages from client
	 * @throws JMSException 
	 */
	protected boolean msgLeave(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 *  handle incoming jms messages from client
	 * @throws JMSException 
	 */
	protected boolean msgReject(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 *  handle incoming jms messages from client
	 * @throws JMSException 
	 */
	protected boolean msgRequestParticipation(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 *  handle incoming jms messages from client
	 * @throws JMSException 
	 */
	protected boolean msgStartChat(Message message) throws JMSException {
		logUnexpectedMethod(message);
		return true;
	}

	/**
	 * produce logging output if this method is called and was not overwritten
	 * 
	 * @param message
	 *            lms event message
	 * @throws JMSException
	 *             if message could not be decoded
	 */
	private void logUnexpectedMethod(Message message) throws JMSException {
		Logger.getLogger(getClass().getName()).log(
				Level.ERROR,
				"unexpected message "
						+ message.getStringProperty(MessageHeader.MsgKind
								.toString()) + " in state " + getName());
	}
}
