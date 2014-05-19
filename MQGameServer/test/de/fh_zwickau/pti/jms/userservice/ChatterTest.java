/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fh_zwickau.pti.jms.tracing.TraceGenerator;
import de.fh_zwickau.pti.jms.tracing.TraceRecord;
import de.fh_zwickau.pti.jms.userservice.AuthenticationServer;
import de.fh_zwickau.pti.jms.userservice.chat.ChatServer;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * @author georg beier
 * 
 */
public class ChatterTest {

	private static final long sleeptime = 100;
	private static Session session;
	private static Queue authDestination;
	private static Queue chatterDestination, tracingDestination;
	private static MessageProducer unboundProducer;
	private static String brokerUri = "tcp://localhost:61616";
	private static Connection connection;
	private static TemporaryQueue participatorTQ;
	private static TemporaryQueue initiatorTQ;
	private static MessageConsumer participatorConsumer;
	private static MessageConsumer initiatorConsumer;
	private static MessageConsumer tracingConsumer;
	private static ArrayList<Message> initMessages = new ArrayList<Message>();
	private static ArrayList<Message> ptcpMessages = new ArrayList<Message>();
	private static ArrayList<String> initStates = new ArrayList<>();
	private static ArrayList<String> ptcpStates = new ArrayList<>();
	private static ArrayList<String> chatroomStates = new ArrayList<>();
	private static MsgSender initSender;
	private static MsgSender ptcpSender;
	private static boolean printTrace = false;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
			// destinations anlegen
			chatterDestination = session.createQueue(ChatServer.CHATTERQ);
			tracingDestination = session.createQueue(TraceGenerator.TRACEQ);
			authDestination = session.createQueue(AuthenticationServer.LOGINQ);
			initiatorTQ = session.createTemporaryQueue();
			participatorTQ = session.createTemporaryQueue();
			// producer anlegen, der nicht an eine bestimmte logInOutDestination
			// gebunden ist
			unboundProducer = session.createProducer(null);
			unboundProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// consumer für die queues anlegen und mit Listenern verknüpfen
			tracingConsumer = session.createConsumer(tracingDestination);
			tracingConsumer.setMessageListener(tracingListener);
			initiatorConsumer = session.createConsumer(initiatorTQ);
			initiatorConsumer.setMessageListener(initListener);
			participatorConsumer = session.createConsumer(participatorTQ);
			participatorConsumer.setMessageListener(ptcpListener);

			initSender = new MsgSender(initiatorTQ);
			ptcpSender = new MsgSender(participatorTQ);

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
		// Message m;
		// // flush queues
		// tracingConsumer.setMessageListener(null);
		// do {
		// m = tracingConsumer.receive(50);
		// } while (m != null);
		// initiatorConsumer.setMessageListener(null);
		// do {
		// m = initiatorConsumer.receive(50);
		// } while (m != null);
		// participatorConsumer.setMessageListener(null);
		// do {
		// m = participatorConsumer.receive(50);
		// } while (m != null);
		Thread.sleep(100);
		session.close();
		connection.close();
	}

	@Before
	public void setUp() {
	}

	private String getMsgKind(Message msg) throws JMSException {
		return msg.getStringProperty(MessageHeader.MsgKind.toString());
	}

	private String getMsgToken(Message msg) throws JMSException {
		return msg.getStringProperty(MessageHeader.AuthToken.toString());
	}

	private String getRefID(Message msg) throws JMSException {
		return msg.getStringProperty(MessageHeader.RefID.toString());
	}

	private String getChatroomID(Message msg) throws JMSException {
		return msg.getStringProperty(MessageHeader.ChatroomID.toString());
	}

	private void printMessages(String prefix, ArrayList<Message> messages)
			throws JMSException {
		synchronized (messages) {
			for (Message message : messages) {
				System.out.println(prefix + message
						.getStringProperty(MessageHeader.MsgKind.toString()));
			}
		}
	}

	private void printStateTrace(String prefix, ArrayList<String> states) {
		int ll = 0, br = 100;
		String empty = prefix.replaceAll(".", " ");
		System.out.print(prefix);
		for (Iterator<String> it = states.iterator(); it.hasNext();) {
			String traceItem = (String) it.next();
			System.out.print(traceItem);
			ll += traceItem.length();
			if (ll > br) {
				System.out.print(" \\\n");
				System.out.print(empty + "... ");
				ll = 4;
			}
			if (it.hasNext()) {
				traceItem = it.next();
				System.out.print("-(" + traceItem + ")->");
				ll += traceItem.length() + 5;
			}
		}
		System.out.println();
	}

	private Message getLastMsg(ArrayList<Message> messages) {
		return messages.get(messages.size() - 1);
	}

	private int waitIndexForReplies(ArrayList<Message> messages,
			int expectedSize) throws InterruptedException {
		long sleepTime = 10L;
		for (int lc = 0; lc < 8 && messages.size() < expectedSize; lc++) {
			Thread.sleep(sleepTime);
			sleepTime *= 2;
		}
		return messages.size() - 1;
	}

	private boolean testTrace(ArrayList<Message> messages,
			MessageKind[] messageKinds) throws JMSException {
		if (messages.size() != messageKinds.length)
			return false;
		int i = 0;
		for (MessageKind messageKind : messageKinds) {
			if (!messages.get(i)
					.getStringProperty(MessageHeader.MsgKind.toString())
					.equals(messageKind.toString()))
				return false;
		}
		return true;
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
					if (traceRecord.getClazz().equals("Chatter")
							&& traceRecord.getObjId().startsWith("Ede")) {
						if (initStates.size() == 0)
							initStates.add(traceRecord.getFromState());
						initStates.add(traceRecord.getEvent());
						initStates.add(traceRecord.getToState());
					} else if (traceRecord.getClazz().equals("Chatter")
							&& traceRecord.getObjId().startsWith("Paulchen")) {
						if (ptcpStates.size() == 0)
							ptcpStates.add(traceRecord.getFromState());
						ptcpStates.add(traceRecord.getEvent());
						ptcpStates.add(traceRecord.getToState());
					} else if (traceRecord.getClazz().equals("Chatroom")) {
						if (chatroomStates.size() == 0)
							chatroomStates.add(traceRecord.getFromState());
						chatroomStates.add(traceRecord.getEvent());
						chatroomStates.add(traceRecord.getToState());
					}
					if (printTrace)
						System.out.println(traceRecord);
				}
			} catch (JMSException e) {
			}
		}
	};

	private static MessageListener initListener = new MessageListener() {

		@Override
		public void onMessage(Message msg) {
			synchronized (initMessages) {
				initMessages.add(msg);
			}
		}
	};

	private static MessageListener ptcpListener = new MessageListener() {

		@Override
		public void onMessage(Message msg) {
			synchronized (ptcpMessages) {
				ptcpMessages.add(msg);
			}
		}
	};

	private static class MsgSender {
		private Destination replyDestination;
		private String token;
		private String chatroomId = "";

		public MsgSender(Destination reply) {
			replyDestination = reply;
		}

		public void sendMessage(Destination destination, MessageKind kind,
				String refID, String... param) throws JMSException {
			Message m;
			if (param.length == 0) {
				m = session.createMessage();
			} else if (param.length == 2) {
				m = session.createMessage();
				m.setStringProperty(MessageHeader.LoginUser.toString(),
						param[0]);
				m.setStringProperty(MessageHeader.LoginPassword.toString(),
						param[1]);
			} else {
				m = session.createTextMessage(param[0]);
			}
			if (token != null) {
				m.setStringProperty(MessageHeader.AuthToken.toString(), token);
			}
			if (refID.length() > 0)
				m.setStringProperty(MessageHeader.RefID.toString(), refID);
			if (chatroomId.length() > 0) {
				m.setStringProperty(MessageHeader.ChatroomID.toString(),
						chatroomId);
				chatroomId = "";
			}
			m.setJMSReplyTo(replyDestination);
			m.setJMSDestination(destination);
			m.setStringProperty(MessageHeader.MsgKind.toString(),
					kind.toString());
			unboundProducer.send(destination, m);
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getToken() {
			return token;
		}

		public String getChatroomId() {
			return chatroomId;
		}

		public void setChatroomId(String chatroomId) {
			this.chatroomId = chatroomId;
		}
	}

	/**
	 * Test müssen in der richtigen Reihenfolge ausgeführt werden!
	 * 
	 * @throws JMSException
	 * @throws InterruptedException
	 */
	@Test
	public void runTests() throws JMSException,
			InterruptedException {
		testCreateOrRegister();
		testOpenCloseChat();
		testOpenRequestEnterChat();
		testRequestRejectCancelChat();
		testInviteDenyAcceptLeaveChat();
		testCloseInWait();
	}

	public void testCreateOrRegister() throws JMSException,
			InterruptedException {
		System.out.println("testCreateOrRegister");
		initStates.clear();
		ptcpStates.clear();
		int initLast = 1, ptcpLast = 1;
		String x = "" + System.currentTimeMillis() % 1000;
		initSender.sendMessage(authDestination, MessageKind.login, "",
				new String[] { "Ede" + x, "Wolf" });
		ptcpSender.sendMessage(authDestination, MessageKind.login, "",
				new String[] { "Paulchen" + x, "Panther" });
		int lastIndex = waitIndexForReplies(initMessages, initLast);
		if (lastIndex >= 0
				&& getMsgKind(initMessages.get(lastIndex)).equals("failed")) {
			initSender.sendMessage(authDestination, MessageKind.register,
					"", new String[] { "Ede" + x, "Wolf" });
			initLast++;
		}
		lastIndex = waitIndexForReplies(ptcpMessages, ptcpLast);
		if (lastIndex >= 0
				&& getMsgKind(ptcpMessages.get(lastIndex)).equals("failed")) {
			ptcpSender.sendMessage(authDestination, MessageKind.register,
					"", new String[] { "Paulchen" + x, "Panther" });
			ptcpLast++;
		}
		lastIndex = waitIndexForReplies(initMessages, initLast);
		assertEquals("login/register failed for initiator",
				MessageKind.authenticated.toString(),
				getMsgKind(initMessages.get(lastIndex)));
		String token = getMsgToken(initMessages.get(lastIndex));
		initSender.setToken(token);
		lastIndex = waitIndexForReplies(ptcpMessages, ptcpLast);
		assertEquals("login/register failed for participator",
				MessageKind.authenticated.toString(),
				getMsgKind(ptcpMessages.get(lastIndex)));
		token = getMsgToken(ptcpMessages.get(lastIndex));
		ptcpSender.setToken(token);
		printStateTrace("init: ", initStates);
		printStateTrace("ptcp: ", ptcpStates);
	}

	public void testOpenCloseChat() throws JMSException, InterruptedException {
		System.out.println("testOpenCloseChat");
		initStates.clear();
		chatroomStates.clear();
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgStartChat, "");
		Thread.sleep(sleeptime);
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgClose, "");
		Thread.sleep(sleeptime);
		printStateTrace("init: ", initStates);
		printStateTrace("chat: ", chatroomStates);
	}

	public void testOpenRequestEnterChat() throws JMSException,
			InterruptedException {
		System.out.println("testOpenRequestEnterChat");
		initStates.clear();
		ptcpStates.clear();
		chatroomStates.clear();
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgStartChat, "");
		Thread.sleep(sleeptime);
		String activeChatId = getChatroomID(getLastMsg(initMessages));
		ptcpSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgRequestParticipation, activeChatId);
		Thread.sleep(sleeptime);
		String rqId = getRefID(getLastMsg(initMessages));
		// sende an den Requestor
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgAccept, rqId);
		Thread.sleep(sleeptime);
		ptcpSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgChat, "",
				new String[] { "Hallo zusammen!" });
		Thread.sleep(sleeptime);
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgChat, "", new String[] { "Hi" });
		Thread.sleep(sleeptime);
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgClose, "");
		Thread.sleep(sleeptime);
		printStateTrace("init: ", initStates);
		printStateTrace("ptcp: ", ptcpStates);
		printStateTrace("chat: ", chatroomStates);
	}

	public void testRequestRejectCancelChat() throws JMSException,
			InterruptedException {
		System.out.println("testRequestRejectCancelChat");
		initStates.clear();
		ptcpStates.clear();
		chatroomStates.clear();
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgStartChat, "");
		Thread.sleep(sleeptime);
		String activeChatId = getChatroomID(getLastMsg(initMessages));
		ptcpSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgRequestParticipation, activeChatId);
		Thread.sleep(sleeptime);
		String rqId = getRefID(getLastMsg(initMessages));
		// sende an den Requestor
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgReject, rqId);
		Thread.sleep(sleeptime);
		ptcpSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgRequestParticipation, activeChatId);
		Thread.sleep(sleeptime);
		ptcpSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgCancel, activeChatId);
		Thread.sleep(sleeptime);
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgClose, "");
		Thread.sleep(sleeptime);
		printStateTrace("init: ", initStates);
		printStateTrace("ptcp: ", ptcpStates);
		printStateTrace("chat: ", chatroomStates);
	}

	public void testInviteDenyAcceptLeaveChat() throws JMSException,
			InterruptedException {
		System.out.println("testInviteDenyAcceptLeaveChat");
		initStates.clear();
		ptcpStates.clear();
		chatroomStates.clear();
		String ede = initSender.getToken();
		String paulchen = ptcpSender.getToken();
		// printTrace = true;
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgStartChat, "");
		Thread.sleep(sleeptime);
		String activeChatId = getChatroomID(getLastMsg(initMessages));
		initSender.setChatroomId(activeChatId);
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgInvite, paulchen);
		Thread.sleep(sleeptime);
		String peerId = getRefID(getLastMsg(ptcpMessages));
		ptcpSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgDeny, peerId);
		Thread.sleep(sleeptime);
		initSender.setChatroomId(activeChatId);
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgInvite, ptcpSender.getToken());
		Thread.sleep(sleeptime);
		peerId = getRefID(getLastMsg(ptcpMessages));
		ptcpSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgAcceptInvitation, peerId);
		Thread.sleep(sleeptime);
		ptcpSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgLeave, "");
		Thread.sleep(sleeptime);
		initSender.setChatroomId(activeChatId);
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgInvite, ptcpSender.getToken());
		Thread.sleep(sleeptime);
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgClose, "");
		Thread.sleep(sleeptime);
		printStateTrace("init: ", initStates);
		printStateTrace("ptcp: ", ptcpStates);
		printStateTrace("chat: ", chatroomStates);
	}

	public void testCloseInWait() throws JMSException,
			InterruptedException {
		System.out.println("testCloseInWait");
		initStates.clear();
		ptcpStates.clear();
		chatroomStates.clear();
		String ede = initSender.getToken();
		String paulchen = ptcpSender.getToken();
		// printTrace = true;
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgStartChat, "");
		Thread.sleep(sleeptime);
		String activeChatId = getChatroomID(getLastMsg(initMessages));
		initSender.setChatroomId(activeChatId);
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgInvite, paulchen);
		Thread.sleep(sleeptime);
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgClose, "");
		Thread.sleep(sleeptime);
		printStateTrace("init: ", initStates);
		printStateTrace("ptcp: ", ptcpStates);
		printStateTrace("chat: ", chatroomStates);
	}

}
