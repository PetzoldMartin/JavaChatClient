/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice.chat;

import javax.jms.JMSException;
import javax.jms.Message;

import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * abstract Base type for Object-for-State Pattern
 * 
 * @author georg beier
 * 
 */
public abstract class ChatroomState extends StateBase {

	private static final boolean trace = true;

	public ChatroomState(String name) {
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
		case chatCreate:
			return create(message);
		case chatChat:
			return chat(message);
		case chatNewParticipant:
			return newParticipant(message);
		case chatLeave:
			return leave(message);
		case chatCancelRequest:
			return cancelRequest(message);
		case chatParticipationRequest:
			return participationRequest(message);
		case chatClose:
			return close(message);
		default:
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
	 * @return
	 * @throws JMSException
	 */
	protected boolean cancelRequest(Message message) throws JMSException {
		logError(message);
		return false;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return
	 * @throws JMSException
	 */
	protected boolean create(Message message) throws JMSException {
		logError(message);
		return false;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return
	 * @throws JMSException
	 */
	protected boolean chat(Message message) throws JMSException {
		logError(message);
		return false;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return
	 * @throws JMSException
	 */
	protected boolean newParticipant(Message message) throws JMSException {
		logError(message);
		return false;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return
	 * @throws JMSException
	 */
	protected boolean leave(Message message) throws JMSException {
		logError(message);
		return false;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return
	 * @throws JMSException
	 */
	protected boolean participationRequest(Message message) throws JMSException {
		logError(message);
		return false;
	}

	/**
	 * method should be overwritten in all concrete subclasses if event message
	 * is really expected. else, method will be ignored and an error will be
	 * logged
	 * 
	 * @param message
	 *            incoming jms event message
	 * @return
	 * @throws JMSException
	 */
	protected boolean close(Message message) throws JMSException {
		logError(message);
		return false;
	}

}
