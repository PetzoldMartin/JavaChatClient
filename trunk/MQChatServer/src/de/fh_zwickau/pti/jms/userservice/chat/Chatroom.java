/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice.chat;

import java.util.HashMap;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTextMessage;

import de.fh_zwickau.pti.jms.tracing.TraceGenerator;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * objects of this class manage chats between two or more participants
 * 
 * @author georg beier
 * 
 */
public class Chatroom {

	/**
	 * for debugging, trace messages can be swiched on
	 */
	private static final boolean trace = true;

	private String roomId;

	/**
	 * The current state of a Chatroom object is represented by an appropriate
	 * instance of a subclass of ChatroomState. All activities that are
	 * initiated by an incoming message will be performed by this state object.
	 * This implements the Objects-for-States pattern in combination with
	 * Methods-for-Transitions
	 */
	private ChatroomState state;

	/**
	 * implementation of ..1 association end across jms
	 */
	private ChatterReference initiator = new ChatterReference();
	/**
	 * implementation of ..* association end across jms
	 */
	private HashMap<String, ChatterReference> participants = new HashMap<>();

	/**
	 * producer used to send jms messages
	 */
	private MessageProducer messageProducer;

	private TraceGenerator traceGenerator;

	/**
	 * set to init state on construction
	 * 
	 * @param producer
	 *            a message producer that is used to send all resulting messages
	 *            to jms
	 * @param uuid
	 *            unique key for this chat room
	 */
	public Chatroom(MessageProducer producer, String uuid) {
		messageProducer = producer;
		roomId = uuid;
		state = init;
	}

	/**
	 * handle all incoming jms messages by delegating message to appropriate
	 * method of current instance of state
	 * 
	 * @param message
	 *            a jms message
	 */
	public boolean processMessage(Message message) {
		boolean result;
		String fromState = state.getName();
		result = state.processMessage(message);
		if (trace && traceGenerator != null) {
			traceGenerator.trace(fromState, state.getName(), message);
		}
		return result;
	}

	public void setTraceGenerator(TraceGenerator traceGen) {
		traceGenerator = traceGen;
		traceGenerator.setObjId(roomId);
	}

	/**
	 * handle constructing messages
	 */
	private ChatroomState init = new ChatroomState("init") {
		@Override
		protected boolean create(Message message) throws JMSException {
			initiator.destination = message.getJMSReplyTo();
			initiator.id = message.getStringProperty(MessageHeader.AuthToken
					.toString());
			initiator.nickname = message
					.getStringProperty(MessageHeader.ChatterNickname.toString());
			participants.put(initiator.id, initiator);
			Message outMessage = new ActiveMQMessage();
			outMessage.setStringProperty(MessageHeader.MsgKind.toString(),
					MessageKind.chatterChatCreated.toString());
			outMessage.setStringProperty(MessageHeader.AuthToken.toString(),
					initiator.id);
			outMessage.setStringProperty(MessageHeader.ChatroomID.toString(),
					roomId);
			outMessage.setJMSDestination(initiator.destination);
			messageProducer.send(initiator.destination, outMessage);
			state = active; // switch to next state
			return true;
		}
	};

	/**
	 * handle messages in state active
	 */
	private ChatroomState active = new ChatroomState("active") {

		@Override
		protected void onEntry(Message message) {

		}

		@Override
		protected boolean chat(Message message) throws JMSException {
			String id = message.getStringProperty(MessageHeader.AuthToken
					.toString());
			String nickname = participants.get(id).nickname;
			String chat = ((TextMessage) message).getText();
			TextMessage outMessage = new ActiveMQTextMessage();
			outMessage.setText(chat);
			outMessage.setStringProperty(MessageHeader.MsgKind.toString(),
					MessageKind.chatterNewChat.toString());
			outMessage.setStringProperty(
					MessageHeader.ChatterNickname.toString(), nickname);
			for (ChatterReference participant : participants.values()) {
				outMessage.setJMSDestination(participant.destination);
				outMessage.setStringProperty(
						MessageHeader.AuthToken.toString(),
						participant.id);
				messageProducer.send(participant.destination, outMessage);
			}
			return true;
		}

		@Override
		protected boolean newParticipant(Message message) throws JMSException {
			ChatterReference newParticipant = new ChatterReference();
			newParticipant.id = message
					.getStringProperty(MessageHeader.AuthToken
							.toString());
			newParticipant.destination = message.getJMSReplyTo();
			newParticipant.nickname = message
					.getStringProperty(MessageHeader.ChatterNickname.toString());
			participants.put(newParticipant.id, newParticipant);
			TextMessage outMessage = new ActiveMQTextMessage();
			outMessage.setText(newParticipant.nickname + " entered chatroom");
			outMessage.setStringProperty(MessageHeader.MsgKind.toString(),
					MessageKind.chatterParticipantEntered.toString());
			for (ChatterReference participant : participants.values()) {
				outMessage.setJMSDestination(participant.destination);
				outMessage.setStringProperty(
						MessageHeader.AuthToken.toString(),
						participant.id);
				outMessage.setStringProperty(
						MessageHeader.ChatterNickname.toString(),
						newParticipant.nickname);
				messageProducer.send(participant.destination, outMessage);
			}
			return true;
		}

		@Override
		protected boolean leave(Message message) throws JMSException {
			String id = message.getStringProperty(MessageHeader.AuthToken
					.toString());
			String nickname = participants.get(id).nickname;
			TextMessage outMessage = new ActiveMQTextMessage();
			outMessage.setText(nickname + " left chatroom");
			outMessage.setStringProperty(MessageHeader.MsgKind.toString(),
					MessageKind.chatterParticipantLeft.toString());
			outMessage.setStringProperty(
					MessageHeader.ChatterNickname.toString(),
					nickname);
			for (ChatterReference participant : participants.values()) {
				outMessage.setJMSDestination(participant.destination);
				outMessage.setStringProperty(
						MessageHeader.AuthToken.toString(),
						participant.id);
				messageProducer.send(participant.destination, outMessage);
			}
			participants.remove(id);
			return true;
		}

		@Override
		protected boolean cancelRequest(Message message)
				throws JMSException {
			String identity = message.getStringProperty(MessageHeader.AuthToken
					.toString());
			Destination destination = message.getJMSReplyTo();
			String nickname = message
					.getStringProperty(MessageHeader.ChatterNickname.toString());
			Message outMessage = new ActiveMQMessage();
			outMessage.setStringProperty(MessageHeader.MsgKind.toString(),
					MessageKind.chatterRequestCanceled.toString());
			outMessage.setStringProperty(
					MessageHeader.ChatterNickname.toString(),
					nickname);
			outMessage.setStringProperty(MessageHeader.RefID.toString(),
					identity);
			outMessage.setStringProperty(MessageHeader.AuthToken.toString(),
					initiator.id);
			outMessage.setJMSReplyTo(destination);
			outMessage.setJMSDestination(initiator.destination);
			messageProducer.send(initiator.destination, outMessage);
			return true;
		}

		@Override
		protected boolean participationRequest(Message message)
				throws JMSException {
			String identity = message.getStringProperty(MessageHeader.AuthToken
					.toString());
			Destination destination = message.getJMSReplyTo();
			String nickname = message
					.getStringProperty(MessageHeader.ChatterNickname.toString());
			Message outMessage = new ActiveMQMessage();
			outMessage.setStringProperty(MessageHeader.MsgKind.toString(),
					MessageKind.chatterParticipationRequest.toString());
			outMessage.setStringProperty(
					MessageHeader.ChatterNickname.toString(),
					nickname);
			outMessage.setStringProperty(MessageHeader.RefID.toString(),
					identity);
			outMessage.setStringProperty(MessageHeader.AuthToken.toString(),
					initiator.id);
			outMessage.setJMSReplyTo(destination);
			outMessage.setJMSDestination(initiator.destination);
			messageProducer.send(initiator.destination, outMessage);
			return true;
		}

		@Override
		protected boolean close(Message message) throws JMSException {
			Message outMessage = new ActiveMQMessage();
			outMessage.setStringProperty(MessageHeader.MsgKind.toString(),
					MessageKind.chatterClosed.toString());
			for (ChatterReference participant : participants.values()) {
				outMessage.setJMSDestination(participant.destination);
				outMessage.setStringProperty(
						MessageHeader.AuthToken.toString(),
						participant.id);
				messageProducer.send(participant.destination, outMessage);
			}
			participants.clear();
			initiator = null;
			state = closing;
			return false;
		}
	};

	/**
	 * handle messages in state closing
	 */
	private ChatroomState closing = new ChatroomState("closing") {
		@Override
		protected boolean participationRequest(Message message)
				throws JMSException {
			TextMessage outMessage = new ActiveMQTextMessage();
			Destination destination = message.getJMSReplyTo();
			String identity = message.getStringProperty(MessageHeader.AuthToken
					.toString());
			outMessage.setStringProperty(MessageHeader.MsgKind.toString(),
					MessageKind.chatterReject.toString());
			outMessage
					.setStringProperty(MessageHeader.AuthToken.toString(),
							identity);
			outMessage.setJMSDestination(destination);
			outMessage.setText("Chatroom is closed");
			messageProducer.send(destination, outMessage);
			return false;
		}
	};

}
