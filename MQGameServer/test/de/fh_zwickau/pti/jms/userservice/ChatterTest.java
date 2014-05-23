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
import javax.jms.TextMessage;

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
	private static TemporaryQueue inviteTQ;
	private static MessageConsumer participatorConsumer;
	private static MessageConsumer initiatorConsumer;
	private static MessageConsumer inviteConsumer;
	private static MessageConsumer tracingConsumer;
	private static ArrayList<Message> initMessages = new ArrayList<Message>();
	private static ArrayList<Message> ptcpMessages = new ArrayList<Message>();
	private static ArrayList<Message> inviteMessages = new ArrayList<Message>();
	private static ArrayList<String> initStates = new ArrayList<>();
	private static ArrayList<String> ptcpStates = new ArrayList<>();
	private static ArrayList<String> inviteStates = new ArrayList<>();
	private static ArrayList<String> chatroomStates = new ArrayList<>();
	private static MsgSender initSender;
	private static MsgSender ptcpSender;
	private static MsgSender inviteSender;
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
			inviteTQ = session.createTemporaryQueue();
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

			inviteConsumer = session.createConsumer(inviteTQ);
			inviteConsumer.setMessageListener(inviteListener);

			initSender = new MsgSender(initiatorTQ);
			ptcpSender = new MsgSender(participatorTQ);
			inviteSender = new MsgSender(inviteTQ);

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
					} else if (traceRecord.getClazz().equals("Chatter")
							&& traceRecord.getObjId().startsWith("Micky")) {
						if (inviteStates.size() == 0)
							inviteStates.add(traceRecord.getFromState());
						inviteStates.add(traceRecord.getEvent());
						inviteStates.add(traceRecord.getToState());
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

	private static MessageListener inviteListener = new MessageListener() {

		@Override
		public void onMessage(Message msg) {
			synchronized (inviteMessages) {
				inviteMessages.add(msg);
			}
		}
	};

	private static class MsgSender {
		private Destination replyDestination;
		private String token;
		private String chatroomId = "";
		private String nickname;

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
			if (refID != null && refID.length() > 0)
				m.setStringProperty(MessageHeader.RefID.toString(), refID);
			if (chatroomId.length() > 0) {
				m.setStringProperty(MessageHeader.ChatroomID.toString(),
						chatroomId);
				chatroomId = "";
			}
			m.setStringProperty(MessageHeader.ChatterNickname.toString(), nickname);
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

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
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
//		 testOpenCloseChat();
//		testOpenRequestEnterChat();
//		testOpenRequestEnterChatListChats();
//		 testRequestRejectCancelChat();
		 testInviteDenyAcceptLeaveChat();
		 testCloseInWait();
		testLogout();
	}

	public void testCreateOrRegister() throws JMSException,
			InterruptedException {
		System.out.println("testCreateOrRegister");
		initStates.clear();
		ptcpStates.clear();
		inviteStates.clear();
		int initLast = 1, ptcpLast = 1, inviteLast = 1;
		String x = "" + System.currentTimeMillis() % 1000;
		initSender.setNickname( "Ede" + x);
		initSender.sendMessage(authDestination, MessageKind.login, "",
				new String[] { "Ede" + x, "Wolf" });
		ptcpSender.setNickname("Paulchen" + x);
		ptcpSender.sendMessage(authDestination, MessageKind.login, "",
				new String[] { "Paulchen" + x, "Panther" });
		inviteSender.setNickname("Micky" + x);
		inviteSender.sendMessage(authDestination, MessageKind.login, "",
				new String[] { "Micky" + x, "Maus" });
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
		lastIndex = waitIndexForReplies(inviteMessages, ptcpLast);
		if (lastIndex >= 0
				&& getMsgKind(inviteMessages.get(lastIndex)).equals("failed")) {
			inviteSender.sendMessage(authDestination, MessageKind.register,
					"", new String[] { "Micky" + x, "Maus" });
			inviteLast++;
		}
		lastIndex = waitIndexForReplies(inviteMessages, inviteLast);
		assertEquals("login/register failed for initiator",
				MessageKind.authenticated.toString(),
				getMsgKind(inviteMessages.get(lastIndex)));
		String token = getMsgToken(initMessages.get(lastIndex));
		initSender.setToken(token);
		lastIndex = waitIndexForReplies(ptcpMessages, ptcpLast);
		assertEquals("login/register failed for participator",
				MessageKind.authenticated.toString(),
				getMsgKind(ptcpMessages.get(lastIndex)));
		token = getMsgToken(ptcpMessages.get(lastIndex));
		ptcpSender.setToken(token);
		assertEquals("login/register failed for invitor",
				MessageKind.authenticated.toString(),
				getMsgKind(inviteMessages.get(lastIndex)));
		token = getMsgToken(inviteMessages.get(lastIndex));
		inviteSender.setToken(token);
		printStateTrace("init: ", initStates);
		printStateTrace("ptcp: ", ptcpStates);
		printStateTrace("invite: ", inviteStates);
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
		System.out.println("\ntestOpenRequestEnterChat");
		initStates.clear();
		ptcpStates.clear();
		inviteStates.clear();
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

	public void testOpenRequestEnterChatListChats() throws JMSException,
			InterruptedException {
		System.out.println("\ntestOpenRequestEnterChatListChats");
		initStates.clear();
		ptcpStates.clear();
		inviteStates.clear();
		chatroomStates.clear();
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgStartChat, "");
		Thread.sleep(sleeptime);
		String activeChatId = getChatroomID(getLastMsg(initMessages));
		ptcpSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgStartChat, activeChatId);
		Thread.sleep(sleeptime);
//		String rqId = getRefID(getLastMsg(initMessages));
//		// sende an den Requestor
//		initSender.sendMessage(chatterDestination,
//				MessageKind.chatterMsgAccept, rqId);
//		Thread.sleep(sleeptime);
//		ptcpSender.sendMessage(chatterDestination,
//				MessageKind.chatterMsgChat, "",
//				new String[] { "Hallo zusammen!" });
//		Thread.sleep(sleeptime);
//		initSender.sendMessage(chatterDestination,
//				MessageKind.chatterMsgChat, "", new String[] { "Hi" });
//		Thread.sleep(sleeptime);
//		rqId = getRefID(getLastMsg(inviteMessages));
		inviteSender.sendMessage(chatterDestination, MessageKind.chatterMsgChats, "");
		Thread.sleep(sleeptime);
		TextMessage m = (TextMessage) getLastMsg(inviteMessages);
		System.out.println(m.getText());
		inviteSender.sendMessage(chatterDestination, MessageKind.chatterMsgChatters, "");
		Thread.sleep(sleeptime);
		m = (TextMessage) getLastMsg(inviteMessages);
		System.out.println(m.getText());
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgClose, "");
		ptcpSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgClose, "");
		Thread.sleep(sleeptime);
		printStateTrace("init: ", initStates);
		printStateTrace("ptcp: ", ptcpStates);
		printStateTrace("invite: ", inviteStates);
		printStateTrace("chat: ", chatroomStates);
	}

	public void testRequestRejectCancelChat() throws JMSException,
			InterruptedException {
		System.out.println("\ntestRequestRejectCancelChat");
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
		System.out.println("\ntestInviteDenyAcceptLeaveChat");
		initStates.clear();
		ptcpStates.clear();
		inviteStates.clear();
		chatroomStates.clear();
		String ede = initSender.getNickname();
		String paulchen = ptcpSender.getNickname();
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
				MessageKind.chatterMsgInvite, ptcpSender.getNickname());
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
				MessageKind.chatterMsgInvite, ptcpSender.getNickname());
		Thread.sleep(sleeptime);
		initSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgClose, "");
		Thread.sleep(sleeptime);
		// steht hier nur wegen fehler im state model (rote transition)
		ptcpSender.sendMessage(chatterDestination,
				MessageKind.chatterMsgDeny, peerId);
		Thread.sleep(sleeptime);
		printStateTrace("init: ", initStates);
		printStateTrace("ptcp: ", ptcpStates);
		printStateTrace("chat: ", chatroomStates);
	}

	public void testCloseInWait() throws JMSException,
			InterruptedException {
		System.out.println("\ntestCloseInWait");
		initStates.clear();
		ptcpStates.clear();
		chatroomStates.clear();
		String ede = initSender.getNickname();
		String paulchen = ptcpSender.getNickname();
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
	public void testLogout() throws JMSException, InterruptedException {
		System.out.println("\ntestLogout");
		initStates.clear();
		ptcpStates.clear();
		inviteStates.clear();
		chatroomStates.clear();
		initSender.sendMessage(authDestination,
				MessageKind.logout, "");
		inviteSender.sendMessage(authDestination,
				MessageKind.logout, "");
		ptcpSender.sendMessage(authDestination,
				MessageKind.logout, "");
		Thread.sleep(sleeptime);
		printStateTrace("init: ", initStates);
		printStateTrace("ptcp: ", ptcpStates);
		printStateTrace("invite: ", inviteStates);
		printStateTrace("chat: ", chatroomStates);
		
	}
}
