/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice.chat;

import java.io.Serializable;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTextMessage;

import de.fh_zwickau.pti.jms.tracing.TraceGenerator;
import de.fh_zwickau.pti.jms.userservice.JmsReference;
import de.fh_zwickau.pti.jms.userservice.User;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * Repräsentiert einen Chat Teilnehmer auf Serverseite. Über die reine
 * Identifikation des externen Users, die durch die Basisklasse User gegeben
 * ist, verfügt Chatter über folgende Funktionalitäten:
 * <ul>
 * <li>State Chart Implementierung bildet die Kommunikation zwischen Chatter und
 * Chatroom ab</li>
 * <li>Message-Kommunikation mit externem Client</li>
 * <li>Message Kommunikation mit Chatroom</li>
 * </ul>
 * 
 * @author georg beier
 * 
 */
public class Chatter extends User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1788756022188693509L;

	/**
	 * for debugging, trace messages can be swiched in
	 */
	private static final boolean trace = true;

	private static Map<String, String> chatterNicknames;
	private static Map<String, Chatter> activeChatters;
	private static Map<String, Chatroom> activeChatrooms;

	/**
	 * The current state of a Chatter object is represented by an appropriate
	 * instance of a subclass of ChatterState. All activities that are initiated
	 * by an incoming method will be performed by this state object. This
	 * implements the Objects-for-States pattern in combination with
	 * Methods-for-Transitions
	 */
	private ChatterState state;

	/**
	 * implementation of ..1 association end across jms by a uid
	 */
	private final JmsReference chatID = new JmsReference();

	private MessageProducer messageProducer;

	private Destination chatroomDestination;

	private TraceGenerator traceGenerator;

	/**
	 * initialize new instance
	 * 
	 * @param uname
	 *            user name
	 * @param pword
	 *            password
	 */
	public Chatter(String uname, String pword) {
		super(uname, pword);
		state = create;
	}

	/**
	 * handle all incoming jms messages by delegating message to appropriate
	 * method of current instance of state
	 * 
	 * @param message
	 *            a jms message
	 */
	@Override
	public boolean processMessage(Message message) {
		boolean result;
		String fromState = state.getName();
		result = state.processMessage(message);
		if (trace && traceGenerator != null) {
			try {
				traceGenerator.setObjId(getUsername()
						+ "@"
						+ message.getStringProperty(MessageHeader.AuthToken
								.toString()));
			} catch (JMSException e) {
			}
			traceGenerator.trace(fromState, state.getName(), message);
		}
		return result;
	}

	public void setProducer(MessageProducer replyProducer) {
		messageProducer = replyProducer;
	}

	public void setChatroomDestination(Destination chatrooms) {
		chatroomDestination = chatrooms;
	}

	public void setTraceGenerator(TraceGenerator generator) {
		traceGenerator = generator;
		traceGenerator.setObjId(getUsername());
	}

	/**
	 * is this chatter idle?
	 */
	public boolean isIdle() {
		return state == idle;
	}

	/**
	 * is this chatter owning a chat?
	 */
	public boolean isOwningChat() {
		return state == owningChat;
	}

	/**
	 * get reference of chat
	 * 
	 * @return id string of chat
	 */
	public String getChatId() {
		if (chatID != null)
			return chatID.id;
		else
			return "";
	}

	public static void setChatterNicknames(Map<String, String> chatterNicknames) {
		Chatter.chatterNicknames = chatterNicknames;
	}

	public static void setActiveChatters(Map<String, Chatter> activeChatters) {
		Chatter.activeChatters = activeChatters;
	}

	public static void setActiveChatrooms(Map<String, Chatroom> rooms) {
		activeChatrooms = rooms;
	}

	/**
	 * should be used by external transitions to change the state
	 * 
	 * @param s
	 *            new state
	 * @param messagemessage
	 *            that triggered transition
	 */
	private void changeState(ChatterState s, Message message) {
		state = s;
		try {
			state.onEntry(message);
		} catch (JMSException e) {
		}
	}

	/**
	 * send message to extern client
	 * 
	 * @param inMsg
	 *            last incoming message from chatroom holds important reference
	 *            information
	 * @param outMsg
	 *            new outgoing message
	 * @param kind
	 *            event kind
	 * @throws JMSException
	 */
	private void sendClient(Message inMsg, Message outMsg, MessageKind kind)
			throws JMSException {
		
		String id = inMsg.getStringProperty(MessageHeader.RefID.toString());
		if (id != null && activeChatters.keySet().contains(id))
			outMsg.setStringProperty(MessageHeader.RefID.toString(), activeChatters.get(id).getUsername());
		else 
			outMsg.setStringProperty(MessageHeader.RefID.toString(), id);
		id = inMsg.getStringProperty(MessageHeader.ChatroomID.toString());
		if (id != null)
			outMsg.setStringProperty(MessageHeader.ChatroomID.toString(), id);
		if (id==null&&kind.toString()==MessageKind.clientNewChat.toString())
			outMsg.setStringProperty(MessageHeader.RefID.toString(), inMsg.getStringProperty(MessageHeader.ChatterNickname.toString()));
		outMsg.setJMSReplyTo(getReplyDestination());
		outMsg.setJMSDestination(getClientDestination());
//		outMsg.setStringProperty(MessageHeader.AuthToken.toString(), inMsg
//				.getStringProperty(MessageHeader.AuthToken.toString()));
		outMsg.setStringProperty(MessageHeader.MsgKind.toString(),
				kind.toString());
		messageProducer.send(getClientDestination(), outMsg);
	}

	/**
	 * send message to chatroom
	 * 
	 * @param inMsg
	 *            message from Chatter
	 * @param outMsg
	 *            new empty message to be completed and sent
	 * @param chatId
	 *            identifies the addressed chatroom, empty string if not yet
	 *            created
	 * @param kind
	 *            event kind
	 * @throws JMSException
	 */
	private void sendChatroom(Message inMsg, Message outMsg, String chatId,
			MessageKind kind)
			throws JMSException {
		outMsg.setJMSDestination(chatroomDestination);
		outMsg.setJMSReplyTo(getReplyDestination());
		outMsg.setStringProperty(MessageHeader.ChatterNickname.toString(),
				getUsername());
		outMsg.setStringProperty(MessageHeader.AuthToken.toString(),
				inMsg.getStringProperty(MessageHeader.AuthToken
						.toString()));
		outMsg.setStringProperty(MessageHeader.MsgKind.toString(),
				kind.toString());
		outMsg.setStringProperty(MessageHeader.ChatroomID.toString(), chatId);
		messageProducer.send(chatroomDestination, outMsg);
	}

	/**
	 * send message to peer chatter
	 * 
	 * @param inMsg
	 *            message from Client
	 * @param outMsg
	 *            new empty message to be completed and sent
	 * @throws JMSException
	 */
	private void sendPeer(Message inMsg, Message outMsg, MessageKind kind)
			throws JMSException {
//		String requestorId = inMsg.getStringProperty(MessageHeader.RefID
//				.toString());
		String requestorId = inMsg.getStringProperty(MessageHeader.RefID
				.toString());
		String token;
		if (requestorId != null) {
			token = chatterNicknames.get(requestorId);
			if (token != null) {
				String chatId = inMsg
						.getStringProperty(MessageHeader.ChatroomID
								.toString());
				if (chatId != null)
					outMsg.setStringProperty(
							MessageHeader.ChatroomID.toString(),
							chatId);
				outMsg.setJMSDestination(inMsg.getJMSReplyTo());
				outMsg.setJMSReplyTo(getReplyDestination());
				outMsg.setStringProperty(MessageHeader.AuthToken.toString(),
						token);
				outMsg.setStringProperty(MessageHeader.MsgKind.toString(),
						kind.toString());
				outMsg.setStringProperty(
						MessageHeader.ChatterNickname.toString(),
						getUsername());
				// outMsg.setStringProperty(MessageHeader.RefID.toString(),
				// inMsg.getStringProperty(MessageHeader.AuthToken.toString()));
				outMsg.setStringProperty(MessageHeader.RefID.toString(), getUsername());
				messageProducer.send(inMsg.getJMSDestination(), outMsg);
			}
		}
	}

	private final ChatterState create = new ChatterState("create@"+getUsername()) {

		@Override
		protected boolean authenticated(Message message)
				throws javax.jms.JMSException {
			setClientDestination(message.getJMSReplyTo());
			String token = message.getStringProperty(MessageHeader.AuthToken
					.toString());
			TextMessage tm = new ActiveMQTextMessage();
			if (message instanceof TextMessage) {
				tm.setText(((TextMessage) message).getText());
			}
			tm.setStringProperty(MessageHeader.AuthToken.toString(), token);
			sendClient(message, tm, MessageKind.authenticated);
			changeState(idle, message);
			return true;
		};

	};

	/*
	 * Implementierung des Objects for States Patterns für die State Chart
	 */
	private final ChatterState idle = new ChatterState("idle@"+getUsername()) {

		@Override
		protected boolean chatCreated(Message message)
				throws javax.jms.JMSException {
			chatID.id = message.getStringProperty(MessageHeader.ChatroomID
					.toString());
			chatID.destination = message.getJMSReplyTo();
			sendClient(message, new ActiveMQMessage(),
					MessageKind.clientChatStarted);
			changeState(owningChat, message);
			return true;
		}

		@Override
		protected boolean closed(Message message) throws javax.jms.JMSException {
			sendClient(message, new ActiveMQMessage(),
					MessageKind.clientChatClosed);
			return true;
		}

		@Override
		protected boolean participantLeft(Message message) throws JMSException {
			return true;
		}

		@Override
		protected boolean invited(Message message)
				throws javax.jms.JMSException {
			chatID.id = message.getStringProperty(MessageHeader.ChatroomID
					.toString());
			Message reply = new ActiveMQMessage();
			sendClient(message, reply, MessageKind.clientInvitation);
			changeState(invitedToParticipate, message);
			return true;
		}

		@Override
		protected boolean msgRequestParticipation(Message message)
				throws javax.jms.JMSException {
			Message outMsg = new ActiveMQMessage();
			chatID.id = message.getStringProperty(MessageHeader.RefID
					.toString());
			sendChatroom(message, outMsg, chatID.id,
					MessageKind.chatParticipationRequest);
			changeState(requestingParticipation, message);
			return true;
		}

		@Override
		protected boolean msgStartChat(Message message)
				throws javax.jms.JMSException {
			Message outMsg = new ActiveMQMessage();
			sendChatroom(message, outMsg, "", MessageKind.chatCreate);
			return true;
		}
		
		@Override
		protected boolean msgDeny(Message message) throws JMSException {
			// ignore this
			return true;
		};

		@Override
		protected boolean msgQueryChats(Message message) throws JMSException {
			TextMessage outMsg = new ActiveMQTextMessage();
			StringBuilder body = new StringBuilder();
			for (String key : activeChatrooms.keySet()) {
				Chatroom chatroom = activeChatrooms.get(key);
				if (chatroom != null)
					body.append(key).append(": ")
							.append(chatroom.getInitiator())
							.append('\n');
			}
			if (body.length() > 0)
				outMsg.setText(body.toString());
			else
				outMsg.setText("no active chatroom found");
			sendClient(message, outMsg, MessageKind.clientAnswerChats);
			return true;
		};

		@Override
		protected boolean msgQueryChatters(Message message) throws JMSException {
			TextMessage outMsg = new ActiveMQTextMessage();

			StringBuilder body = new StringBuilder();
			for (String cKey : activeChatters.keySet()) {
				Chatter ch;
				if ((ch = activeChatters.get(cKey)) != null
						&& ch.isIdle()) {
					body.append(ch.getUsername()).append('\n');
				}
			}
			if (body.length() > 0) {
				outMsg.setText(body.toString());
			} else {
				outMsg.setText("no idle chatter found");
			}
			sendClient(message, outMsg, MessageKind.clientAnswerChatters);
			return true;
		};
	};

	private final ChatterState invitedToParticipate = new ChatterState(
			"invitedToParticipate@"+getUsername()) {

		@Override
		protected boolean closed(Message message) throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendClient(message, outMsg, MessageKind.clientChatClosed);
			changeState(idle, message);
			return true;
		}

		@Override
		protected boolean msgAcceptInvitation(Message message)
				throws javax.jms.JMSException {
			Message outMsg = new ActiveMQMessage();
			sendPeer(message, outMsg, MessageKind.chatterMsgAcceptInvitation);
			changeState(participatingInChat, message);
			return true;
		}

		@Override
		protected boolean msgDeny(Message message)
				throws javax.jms.JMSException {
			Message outMsg = new ActiveMQMessage();
			sendPeer(message, outMsg, MessageKind.chatterMsgDeny);
			changeState(idle, message);
			return true;
		}

	};

	private final ChatterState requestingParticipation = new ChatterState(
			"requestingParticipation@"+getUsername()) {

		@Override
		protected boolean closed(Message message) throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendClient(message, outMsg, MessageKind.clientChatClosed);
			changeState(idle, message);
			return true;
		}

		@Override
		protected boolean accepted(Message message)
				throws javax.jms.JMSException {
			Message outMsg = new ActiveMQMessage();
			sendClient(message, outMsg, MessageKind.clientParticipating);
			changeState(participatingInChat, message);
			return true;
		}

		@Override
		protected boolean rejected(Message message)
				throws javax.jms.JMSException {
			Message outMsg = new ActiveMQMessage();
			sendClient(message, outMsg, MessageKind.clientRejected);
			changeState(idle, message);
			return true;
		}

		@Override
		protected boolean invited(Message message) throws JMSException {
			return true;
		}

		@Override
		protected boolean msgCancel(Message message)
				throws javax.jms.JMSException {
			Message outMsg = new ActiveMQMessage();
			sendChatroom(message, outMsg, chatID.id,
					MessageKind.chatCancelRequest);
			changeState(idle, message);
			return true;
		}
	};

	private final ChatterState owningChat = new Chatting("owningChat@"+getUsername()) {

		@Override
		protected boolean denied(Message message) throws JMSException {
			return true;
		}

		@Override
		protected boolean participationRequest(Message message)
				throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendClient(message, outMsg, MessageKind.clientRequest);
			return true;
		}

		@Override
		protected boolean requestCancelled(Message message) throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendClient(message, outMsg, MessageKind.clientRequestCancelled);
			return true;
		}

		@Override
		protected boolean msgAccept(Message message) throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendPeer(message, outMsg, MessageKind.chatterAccepted);
			return true;
		}

		@Override
		protected boolean msgAcceptInvitation(Message message)
				throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendClient(message, outMsg, MessageKind.clientAccepted);
			return true;
		};

		@Override
		protected boolean msgDeny(Message message) throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendClient(message, outMsg, MessageKind.clientDenied);
			return true;
		};

		@Override
		protected boolean msgInvite(Message message) throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendPeer(message, outMsg, MessageKind.chatterInvited);
			return true;
		}

		@Override
		protected boolean msgReject(Message message) throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendPeer(message, outMsg, MessageKind.chatterReject);
			return true;
		}

		@Override
		protected boolean msgClose(Message message) throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendChatroom(message, outMsg, chatID.id, MessageKind.chatClose);
			changeState(idle, message);
			return true;
		};

		@Override
		protected boolean msgQueryChatters(Message message) throws JMSException {
			TextMessage outMsg = new ActiveMQTextMessage();

			StringBuilder body = new StringBuilder();
			for (String cKey : activeChatters.keySet()) {
				Chatter ch;
				if ((ch = activeChatters.get(cKey)) != null && ch.isIdle()) {
					body.append(ch.getUsername()).append('\n');
				}
			}
			if (body.length() > 0) {
				outMsg.setText(body.toString());
			} else {
				outMsg.setText("no idle chatter found");
			}
			sendClient(message, outMsg, MessageKind.clientAnswerChatters);
			return true;
		};
	};

	private final ChatterState participatingInChat = new Chatting(
			"participatingInChat@"+getUsername()) {

		@Override
		protected void onEntry(Message message) throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendChatroom(message, outMsg, chatID.id,
					MessageKind.chatNewParticipant);
		}

		@Override
		protected boolean closed(Message message) throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendClient(message, outMsg, MessageKind.clientChatClosed);
			changeState(idle, message);
			return true;
		}

		@Override
		protected boolean msgLeave(Message message) throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendChatroom(message, outMsg, chatID.id, MessageKind.chatLeave);
			changeState(idle, message);
			return true;
		}

	};

	/**
	 * Implementierung des Compound State Chatting aus der State Chart
	 * 
	 * @author georg beier
	 * 
	 */
	private class Chatting extends ChatterState {

		public Chatting(String name) {
			super(name);
		}

		@Override
		protected boolean newChat(Message message) throws JMSException {
			TextMessage outMsg = new ActiveMQTextMessage();
			if (message instanceof TextMessage)
				outMsg.setText(((TextMessage) message).getText());
				
			sendClient(message, outMsg, MessageKind.clientNewChat);
			return true;
		}

		@Override
		protected boolean participantEntered(Message message)
				throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendClient(message, outMsg, MessageKind.clientParticipantEntered);
			return true;
		}

		@Override
		protected boolean participantLeft(Message message) throws JMSException {
			Message outMsg = new ActiveMQMessage();
			sendClient(message, outMsg, MessageKind.clientParticipantLeft);
			return true;
		}

		@Override
		protected boolean msgChat(Message message) throws JMSException {
			TextMessage outMsg = new ActiveMQTextMessage();
			if (message instanceof TextMessage)
				outMsg.setText(((TextMessage) message).getText());
			sendChatroom(message, outMsg, chatID.id,
					MessageKind.chatChat);
			return true;
		}

	}

}
