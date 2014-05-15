package de.fh_zwickau.pti.jms.userservice.chat;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.fh_zwickau.pti.jms.tracing.JmsTraceSender;
import de.fh_zwickau.pti.jms.tracing.TraceGenerator;
import de.fh_zwickau.pti.jms.userservice.AuthenticationServer;
import de.fh_zwickau.pti.jms.userservice.UserDto;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * JMS Service Provider und Demo-Programm für unterschiedliche ActiveMQ Features
 * 
 * @author georg beier
 * 
 */
public class ChatServer {

	public static final String CHATTERQ = AuthenticationServer.USERQ;
	public static final String CHATQ = "chatq";

	private Session session;
	private Destination chatroomDestination;
	private Destination chatterDestination;
	private MessageProducer replyProducer;
	private String brokerUri;
	protected ConcurrentHashMap<String, Chatroom> activeChatRooms = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, Chatter> activeChatters = new ConcurrentHashMap<>();
	private Queue tracingDestination;
	private MessageProducer traceProducer;

	/**
	 * Servewr anlegen
	 * 
	 * @param brUri
	 *            URI des MessageBrokers
	 */
	public ChatServer(String brUri) {
		brokerUri = brUri;
	}

	/**
	 * main programm stellt JMS Message Broker Service zur Verfügung und startet
	 * den AuthenticationServer, wenn Connectoren als Kommandozeilenargumente
	 * angegeben sind
	 * 
	 * @param args
	 *            args[0] URI des AMQ Brokers. Wenn args.length == 0 wird
	 *            lokaler Broker angelegt.
	 */
	public static void main(String[] args) throws Exception {
		String brokerUri;
		if (args.length == 0) {
			String localConnection = "tcp://localhost:61616";
			brokerUri = localConnection;
		} else {
			brokerUri = args[0];
		}
		ChatServer chatServer = new ChatServer(brokerUri);
		chatServer.runServer();
	}

	public void runServer() {
		try {
			// verbinde Server mit dem JMS Broker
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					"sys", "man", brokerUri);
			// connection aufbauen, konfigurieren und starten
			Connection connection = connectionFactory.createConnection();
			connection.setExceptionListener(exceptionListener);
			connection.start();
			// session anlegen
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			chatroomDestination = session.createQueue(CHATQ);
			chatterDestination = session.createQueue(CHATTERQ);
			tracingDestination = session.createQueue(TraceGenerator.TRACEQ);
			// producer anlegen, der nicht an eine bestimmte logInOutDestination
			// gebunden ist
			replyProducer = session.createProducer(null);
			replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			traceProducer = session.createProducer(tracingDestination);
			traceProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// consumer für die queues anlegen und mit Listenern verknüpfen
			session.createConsumer(chatroomDestination).setMessageListener(
					chatroomListener);
			session.createConsumer(chatterDestination).setMessageListener(
					chatterListener);

		} catch (Exception e) {
			Logger.getRootLogger().log(Level.ERROR,
					"Server startup error " + e);
		}

	}

	private ExceptionListener exceptionListener = new ExceptionListener() {
		@Override
		public void onException(JMSException e) {
			Logger.getRootLogger().log(Level.ERROR,
					"User-Server error " + e);
		}
	};

	/**
	 * listener für chatroom messages
	 */
	private MessageListener chatroomListener = new MessageListener() {
		/**
		 * wird per callback für jede eingehende Methode der verbundenen Queue
		 * aufgerufen
		 * 
		 * @param message
		 *            Nachricht von einem Client (direkt oder via
		 *            AuthenticationService)
		 */
		@Override
		public void onMessage(Message message) {
			String msgKind = "";
			String token = "";
			try {
				token = message.getStringProperty(MessageHeader.ChatroomID
						.toString());
				
				if (token != null && activeChatRooms.containsKey(token)) {
					if (!(activeChatRooms.get(token).processMessage(message))) {
						activeChatRooms.remove(token);
					}
				} else {
					try {
						msgKind = message
								.getStringProperty(MessageHeader.MsgKind
										.toString());
						MessageKind kind = MessageKind.valueOf(msgKind);
						if (kind == MessageKind.chatCreate) {
							String uid = UUID.randomUUID().toString();
							Chatroom chatroom = new Chatroom(replyProducer, uid);
							chatroom.setTraceGenerator(new TraceGenerator(
										new JmsTraceSender(traceProducer,
												session), Chatroom.class
												.getSimpleName()));
							activeChatRooms.put(uid, chatroom);
							chatroom.processMessage(message);
						} else {
							Logger.getRootLogger().log(
									Level.ERROR,
									"No receiver for token (" + token
											+ ") or message kind " + msgKind);
						}
					} catch (IllegalArgumentException e) {
						Logger.getRootLogger().log(Level.ERROR,
								"Unknown message kind " + msgKind);
					}
				}
			} catch (IllegalArgumentException e) {
				Logger.getLogger("Chatroom").log(Level.ERROR,
						"Unknown message kind " + msgKind);
			} catch (JMSException e) {
				Logger.getRootLogger().log(Level.ERROR, e);
			}
		}
	};

	/**
	 * listener für chatter messages
	 */
	private MessageListener chatterListener = new MessageListener() {
		/**
		 * wird per callback für jede eingehende Methode der verbundenen Queue
		 * aufgerufen
		 * 
		 * @param message
		 *            Nachricht von einem Client (direkt oder via
		 *            AuthenticationService)
		 */
		@Override
		public void onMessage(Message message) {
			String msgKind = "";
			String token = "";
			try {
				token = message.getStringProperty(MessageHeader.AuthToken
						.toString());
				if (activeChatters.containsKey(token)) {
					if (!(activeChatters.get(token).processMessage(message))) {
						activeChatters.remove(token);
					}
				} else {
					msgKind = message
							.getStringProperty(MessageHeader.MsgKind
									.toString());
					MessageKind kind = MessageKind.valueOf(msgKind);
					if (kind == MessageKind.authenticated) {
						if (message instanceof ObjectMessage) {
							ObjectMessage playerMessage = (ObjectMessage) message;
							UserDto dto = (UserDto) playerMessage.getObject();
							if (dto.isChatter()) {
								Chatter c = new Chatter(dto.getUsername(), "");
								c.setTraceGenerator(new TraceGenerator(
										new JmsTraceSender(traceProducer,
												session), Chatter.class
												.getSimpleName()));
								c.setProducer(replyProducer);
								c.setReplyDestination(chatterDestination);
								c.setChatroomDestination(chatroomDestination);
								activeChatters.put(token, c);
								c.processMessage(message);
							}
						}
					} else {
						Logger.getLogger("Chatter").log(
								Level.ERROR,
								"No receiver for token (" + token
										+ ") or message kind " + msgKind);
					}
				}
			} catch (IllegalArgumentException e) {
				Logger.getRootLogger().log(Level.ERROR,
						"Unknown message kind " + msgKind);
			} catch (JMSException e) {
				Logger.getRootLogger().log(Level.ERROR, e);
			}
		}
	};
}
