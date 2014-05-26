package de.fh_zwickau.pti.jms.userservice.chat;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * extracted common methods for all state classes
 * 
 * @author georg beier
 * 
 */
public abstract class StateBase {

	protected String name;

	public StateBase(String name) {
		this.name = name;
	}

	/**
	 * handle all incoming jms messages by delegating message to appropriate
	 * method of subclass
	 * 
	 * @param message
	 *            a jms message
	 */
	public boolean processMessage(Message message) {
		String msgKind = "";
		try {
			msgKind = message.getStringProperty(MessageHeader.MsgKind
					.toString());
			MessageKind kind = MessageKind.valueOf(msgKind);
			return executeEventMethod(kind, message);
		} catch (IllegalArgumentException e) {
			Logger.getRootLogger().log(Level.ERROR,
					"Unknown message kind " + msgKind);
		} catch (JMSException e) {
			Logger.getRootLogger().log(Level.ERROR, e);
		}
		return false;
	}

	public String getName() {
		return name;
	}

	/**
	 * select the appropriate event handler method for incoming method, must be
	 * implemented in subclasses
	 * 
	 * @param kind
	 *            enum defining kind of incoming method
	 * @param message
	 *            incoming jms method
	 * @return
	 * @throws JMSException
	 */
	protected abstract boolean executeEventMethod(MessageKind kind,
			Message message) throws JMSException;

	/**
	 * State entry method, to be redefined by subclass if needed
	 * 
	 * @param message
	 *            message that triggered the transition
	 * @throws JMSException 
	 */
	protected void onEntry(Message message) throws JMSException {

	}

	/**
	 * State exit method, to be redefined by subclass if needed
	 * 
	 * @param message
	 *            message that triggered the transition
	 */
	protected void onExit(Message message) {

	}

	/**
	 * log errors for unexpected events
	 * 
	 * @param message
	 */
	protected void logError(Message message) {
		try {
			Logger.getRootLogger().log(
					Level.ERROR,
					"unexpected message "
							+ message.getStringProperty(MessageHeader.MsgKind
									.toString()) + " in state " + name);
		} catch (JMSException e) {
		}
	}

}