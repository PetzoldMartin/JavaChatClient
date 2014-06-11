/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fh_zwickau.pti.jms.tracing.TraceGenerator;
import de.fh_zwickau.pti.jms.tracing.TraceRecord;
import de.fh_zwickau.pti.jms.userservice.chat.ChatterState;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * @author georg beier
 * 
 */
public class ChatroomTest {

	private static Session session;
	private static Queue chatterDestination, chatroomDestination,
			tracingDestination;
	private static MessageProducer chatProducer;
	private static String brokerUri = "tcp://localhost:61616";
	protected static ConcurrentHashMap<String, ChatterMock> mocks = new ConcurrentHashMap<>();
	private static ChatterMock mock1, mock2, mock3, mock4;
	private static ArrayList<String> errors = new ArrayList<>();
	private static HashMap<String, Object> results = new HashMap<>();
	private static Connection connection;
	private static int postfix;
	private static MessageConsumer tracingConsumer;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		postfix = new Random(System.currentTimeMillis()).nextInt(1000);
		mock1 = new ChatterMock();
		mocks.put("mock1" + postfix, mock1);
		mock2 = new ChatterMock();
		mocks.put("mock2" + postfix, mock2);
		mock3 = new ChatterMock();
		mocks.put("mock3" + postfix, mock3);
		mock4 = new ChatterMock();
		mocks.put("mock4" + postfix, mock4);
		try {
			// verbinde Server mit dem JMS Broker
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					"sys", "man", brokerUri);
			// connection aufbauen, konfigurieren und starten
			connection = connectionFactory.createConnection();
			connection.setExceptionListener(exceptionListener);
			connection.start();
			// session anlegen
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			chatterDestination = session.createQueue("testq");
			chatroomDestination = session.createQueue("chatq");
			tracingDestination = session.createQueue(TraceGenerator.TRACEQ);
			// producer anlegen, der nicht an eine bestimmte logInOutDestination
			// gebunden ist
			chatProducer = session.createProducer(chatroomDestination);
			chatProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// consumer für die queues anlegen und mit Listenern verknüpfen
			session.createConsumer(chatterDestination).setMessageListener(
					chatListener);
			tracingConsumer = session.createConsumer(tracingDestination);
			tracingConsumer.setMessageListener(tracingListener);

		} catch (Exception e) {
			Logger.getRootLogger().log(Level.ERROR,
					"Testcase startup error " + e);
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		session.close();
		connection.close();
	}

	@Before
	public void setUp() {
		errors.clear();
		results.clear();
	}

	private static ExceptionListener exceptionListener = new ExceptionListener() {
		@Override
		public void onException(JMSException e) {
			Logger.getRootLogger().log(Level.ERROR,
					"User-Server error " + e);
		}
	};

	/**
	 * listener für tracing messages
	 */
	private static MessageListener tracingListener = new MessageListener() {
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
			try {
				if (message instanceof ObjectMessage) {
					ObjectMessage traceMsg = (ObjectMessage) message;
					TraceRecord traceRecord = (TraceRecord) traceMsg
							.getObject();
					System.out.println(traceRecord);
				}
			} catch (JMSException e) {
			}
		}
	};

	/**
	 * listener für player messages
	 */
	private static MessageListener chatListener = new MessageListener() {
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
			String token = "";
			try {
				token = message.getStringProperty(MessageHeader.AuthToken
						.toString());
				System.out.println(token);
				if (token != null && mocks.containsKey(token)) {
					mocks.get(token).processMessage(message);
				} else {
					errors.add("wrong reply message recipient " + token);
					System.out.println("wrong reply message recipient "
							+ token + " for "
							+ message.getStringProperty(MessageHeader.MsgKind
									.toString()));
				}
			} catch (JMSException e) {
				errors.add(e.toString());
			}
		}
	};

	private void prepare(Message message, MessageKind kind, String token,
			String ref)
			throws JMSException {
		message.setJMSDestination(chatroomDestination);
		message.setJMSReplyTo(chatterDestination);
		setKind(message, kind);
		message.setStringProperty(MessageHeader.AuthToken.toString(), token);
		message.setStringProperty(MessageHeader.ChatterNickname.toString(),
				token);
		message.setStringProperty(MessageHeader.ChatroomID.toString(), ref);
	}

	private void setKind(Message message, MessageKind kind) throws JMSException {
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				kind.toString());
	}

	private ChatterState initializingChatter = new ChatterState(
			"initializer") {
		@Override
		protected boolean chatCreated(Message message) throws JMSException {
			results.put("kind", message
					.getStringProperty(MessageHeader.MsgKind.toString()));
			results.put("ref", message
					.getStringProperty(MessageHeader.ChatroomID.toString()));
			return true;
		}

		@Override
		protected boolean newChat(Message message) throws JMSException {
			reportReply(message);
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				results.put("text", textMessage.getText());
			}
			return false;
		};

		@Override
		protected boolean participationRequest(Message message)
				throws JMSException {
			reportReply(message);
			results.put("ref",
					message.getStringProperty(MessageHeader.RefID.toString()));
			return false;
		}

		@Override
		protected boolean participantEntered(Message message)
				throws JMSException {
			reportReply(message);
			return true;
		};

		@Override
		protected boolean participantLeft(Message message) throws JMSException {
			reportReply(message);
			return true;
		};

		@Override
		protected boolean closed(Message message) throws JMSException {
			results.put("kind", message
					.getStringProperty(MessageHeader.MsgKind.toString()));
			return true;
		}

		/**
		 * @param message
		 * @throws JMSException
		 */
		private void reportReply(Message message) throws JMSException {
			results.put("kind", message
					.getStringProperty(MessageHeader.MsgKind.toString()));
			results.put("who",
					message
							.getStringProperty(MessageHeader.ChatterNickname
									.toString()));
		}
	};

	private ChatterState participatingChatter = new ChatterState(
			"participator") {
		@Override
		protected boolean participantEntered(Message message)
				throws JMSException {
			// results.put("kind", message
			// .getStringProperty(MessageHeader.MsgKind.toString()));
			// results.put("who", message
			// .getStringProperty(MessageHeader.AuthToken.toString()));
			return true;
		};

		@Override
		protected boolean participantLeft(Message message) throws JMSException {
			// results.put("kind", message
			// .getStringProperty(MessageHeader.MsgKind.toString()));
			// results.put("who", message
			// .getStringProperty(MessageHeader.AuthToken.toString()));
			return true;
		};

		@Override
		protected boolean closed(Message message) throws JMSException {
			results.put("kind", message
					.getStringProperty(MessageHeader.MsgKind.toString()));
			return true;
		}
	};

	@Test
	public void runTests() throws Exception {
		createDelete();
		 newParticipant();
		 participationRequest();
		 newChat();
	}

	public void createDelete() throws JMSException, InterruptedException {
		System.out.println("testCreateDelete");
		Message message = new ActiveMQMessage();
		prepare(message, MessageKind.chatCreate, "mock1" + postfix, "");
		mock1.setState(initializingChatter);
		chatProducer.send(message);
		Thread.sleep(200);
		assertEquals("wrong message kind",
				MessageKind.chatterChatCreated.toString(), results.get("kind"));
		assertNotNull("no chatroom id", results.get("ref"));
		String chatId = results.get("ref").toString();
		System.out.println("ref: " + chatId);
		results.clear();
		setKind(message, MessageKind.chatClose);
		message.setStringProperty(MessageHeader.ChatroomID.toString(), chatId);
		chatProducer.send(message);
		Thread.sleep(200);
		assertEquals("wrong message kind",
				MessageKind.chatterClosed.toString(), results.get("kind"));
	}

	public void newParticipant() throws JMSException, InterruptedException {
		System.out.println("testNewParticipant");
		Message message = new ActiveMQMessage();
		prepare(message, MessageKind.chatCreate, "mock1" + postfix, "");
		mock1.setState(initializingChatter);
		mock2.setState(participatingChatter);
		mock3.setState(participatingChatter);
		chatProducer.send(message);
		Thread.sleep(200);
		String chatId = results.get("ref").toString();
		System.out.println("ref: " + chatId);
		Message message2 = new ActiveMQMessage();
		prepare(message2, MessageKind.chatNewParticipant, "mock2" + postfix,
				chatId);
		chatProducer.send(message2);
		Thread.sleep(200);
		assertEquals("wrong message kind",
				MessageKind.chatterParticipantEntered.toString(),
				results.get("kind"));
		assertEquals("wrong participant", "mock2" + postfix, results.get("who"));
		Message message3 = new ActiveMQMessage();
		prepare(message3, MessageKind.chatNewParticipant, "mock3" + postfix,
				chatId);
		chatProducer.send(message3);
		Thread.sleep(200);
		setKind(message2, MessageKind.chatLeave);
		chatProducer.send(message2);
		Thread.sleep(200);
		assertEquals("wrong message kind",
				MessageKind.chatterParticipantLeft.toString(),
				results.get("kind"));
		assertEquals("wrong participant", "mock2" + postfix, results.get("who"));
		message.setStringProperty(MessageHeader.ChatroomID.toString(), chatId);
		setKind(message, MessageKind.chatClose);
		chatProducer.send(message);
		Thread.sleep(200);
	}

	public void participationRequest() throws JMSException,
			InterruptedException {
		Message message = new ActiveMQMessage();
		prepare(message, MessageKind.chatCreate, "mock1" + postfix, "");
		mock1.setState(initializingChatter);
		mock2.setState(participatingChatter);
		chatProducer.send(message);
		Thread.sleep(200);
		String chatId = results.get("ref").toString();
		Message message2 = new ActiveMQMessage();
		prepare(message2, MessageKind.chatParticipationRequest, "mock2"
				+ postfix, chatId);
		chatProducer.send(message2);
		Thread.sleep(200);
		assertEquals("wrong message kind",
				MessageKind.chatterParticipationRequest.toString(),
				results.get("kind"));
		assertEquals("wrong requestor", "mock2" + postfix, results.get("who"));
		assertEquals("wrong requestor id", "mock2" + postfix,
				results.get("ref"));
		message.setStringProperty(MessageHeader.ChatroomID.toString(), chatId);
		setKind(message, MessageKind.chatClose);
		chatProducer.send(message);
		prepare(message2, MessageKind.chatParticipationRequest, "mock3"
				+ postfix, chatId);
		chatProducer.send(message2);
		Thread.sleep(200);
	}

	public void newChat() throws JMSException, InterruptedException {
		Message message = new ActiveMQMessage();
		prepare(message, MessageKind.chatCreate, "mock1" + postfix, "");
		mock1.setState(initializingChatter);
		mock2.setState(participatingChatter);
		mock3.setState(participatingChatter);
		chatProducer.send(message);
		Thread.sleep(200);
		String chatId = results.get("ref").toString();
		Message message2 = new ActiveMQMessage();
		prepare(message2, MessageKind.chatNewParticipant, "mock2" + postfix,
				chatId);
		chatProducer.send(message2);
		// Thread.sleep(200);
		Message message3 = new ActiveMQMessage();
		prepare(message3, MessageKind.chatNewParticipant, "mock3" + postfix,
				chatId);
		chatProducer.send(message3);
		// Thread.sleep(200);
		TextMessage textMessage = new ActiveMQTextMessage();
		prepare(textMessage, MessageKind.chatChat, "mock2" + postfix, chatId);
		textMessage.setText("XXXofMock2");
		chatProducer.send(textMessage);
		Thread.sleep(200);
		assertEquals("wrong message kind",
				MessageKind.chatterNewChat.toString(),
				results.get("kind"));
		assertEquals("wrong chatter", "mock2" + postfix, results.get("who"));
		assertEquals("wrong chat message", "XXXofMock2", results.get("text"));
		message.setStringProperty(MessageHeader.ChatroomID.toString(), chatId);
		setKind(message, MessageKind.chatClose);
		chatProducer.send(message);
		Thread.sleep(200);
	}

}
