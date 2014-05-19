package de.fh_zwickau.pti.jms.chatclient.messaging;

import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.SwingUtilities;

import org.apache.activemq.ActiveMQConnectionFactory;

import de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer;
import de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver;
import de.fh_zwickau.pti.mqgamecommon.MQConstantDefs;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * send and receive chat messages
 * 
 * @author georg beier
 * 
 */
public class ChatJmsAdapter implements ChatServerMessageProducer {

	private String authToken = "";
	private Session session;
	private Destination chatServiceQ;
	private Destination loginQ, reply;
	private MessageProducer requestProducer;
	
	private ChatServerMessageReceiver messageReceiver;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String brokerUri;
		if (args.length == 0) {
			String localConnection = "tcp://localhost:61616";
			brokerUri = localConnection;
		} else {
			brokerUri = args[0];
		}
		ChatJmsAdapter chatJmsAdapter = new ChatJmsAdapter();
		chatJmsAdapter.connectToServer(brokerUri);
	}

	/**
	 * connect to broker
	 * 
	 * @param brokerUri
	 *            die Broker URI
	 */
	public void connectToServer(String brokerUri) {
		try {
			// Factory für Verbindungen zu einem JMS Server
			ActiveMQConnectionFactory connectionFactory =
					new ActiveMQConnectionFactory("sys", "man",
							brokerUri);
			// connection aufbauen, konfigurieren und starten
			Connection connection = connectionFactory.createConnection();
			connection.setExceptionListener(excListener);
			connection.start();
			// session, queue und temporary queue anlegen
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			loginQ = session.createQueue(MQConstantDefs.LOGINQ);
			reply = session.createTemporaryQueue();
			// producer ohne bestimmte queue anlegen und konfigurieren
			requestProducer = session.createProducer(null);
			requestProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// consumer für die reply (temporary) queue anlegen und mit einem
			// MessageListener verbinden
			MessageConsumer consumer = session.createConsumer(reply);
			consumer.setMessageListener(msgListener);
		} catch (Exception e) {
			System.out.println("[EchoRequestQ] Caught: " + e);
			e.printStackTrace();
		}

	}

	/**
	 * lokale Implementierung für einen ExceptionListener
	 * 
	 * @author georg beier
	 */
	private ExceptionListener excListener = new ExceptionListener() {
		@Override
		public void onException(JMSException arg0) {
			arg0.printStackTrace();
		}
	};

	/**
	 * lokale Implementierung für einen MessageListener
	 * 
	 * @author georg beier
	 */
	private MessageListener msgListener = new MessageListener() {
		@Override
		public void onMessage(Message replyMessage) {
			try {
				if (replyMessage instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) replyMessage;
					String msgKind = textMessage
							.getStringProperty(MessageHeader.MsgKind.toString());
					MessageKind messageKind = MessageKind.valueOf(msgKind);
					switch (messageKind) {
					case authenticated:
						authToken = textMessage
								.getStringProperty(MessageHeader.AuthToken
										.toString());
						chatServiceQ = textMessage.getJMSReplyTo();
						if(messageReceiver != null)
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									messageReceiver.gotSuccess();
								}
							});
						break;
					case failed:
						if(messageReceiver != null)
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									messageReceiver.gotFail();
								}
							});
						break;
					case loggedOut:
						if(messageReceiver != null)
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									messageReceiver.gotLogout();
								}
							});
						break;

					default:
						break;
					}
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer#register
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public void register(String uname, String pword) throws Exception {
		loginOrRegister(true, uname, pword);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer#login(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void login(String uname, String pword) throws Exception {
		loginOrRegister(false, uname, pword);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer#logout()
	 */
	@Override
	public void logout() throws Exception {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.logout.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		requestProducer.send(loginQ, message);
	}

	public void setMessageReceiver(ChatServerMessageReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}

	private void loginOrRegister(boolean register, String uname, String pword)
			throws JMSException {
		String msgKind;
		if (register)
			msgKind = MessageKind.register.toString();
		else
			msgKind = MessageKind.login.toString();
		Message message = createMessage(loginQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				msgKind);
		message.setStringProperty(
				MessageHeader.LoginUser.toString(), uname);
		message.setStringProperty(
				MessageHeader.ChatterNickname.toString(), uname);
		message.setStringProperty(
				MessageHeader.LoginPassword.toString(), pword);
		requestProducer.send(loginQ, message);

	}

	/**
	 * @return
	 * @throws JMSException
	 */
	private TextMessage createMessage(Destination destination) throws JMSException {
		TextMessage textMessage = session
				.createTextMessage();
		// eine Message besitzt optional verschiedene Properties
		textMessage.setJMSReplyTo(reply);
		textMessage.setJMSDestination(destination);
		return textMessage;
	}
}
