package de.fh_zwickau.pti.jms.userservice;

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
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import de.fh_zwickau.pti.jms.userservice.AuthenticationServer;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * Beispiel für den Aufruf einer JMS Queue mit Empfang einer Antwort über eine
 * Temporary Queue
 * 
 * @author georg beier
 * 
 */
public class TestClient {

	private Session session;
	private Destination destination, reply;
	private MessageProducer requestProducer;

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
		TestClient testClient = new TestClient();
		testClient.runTest(brokerUri);
	}

	/**
	 * baue eine Verbindung auf, sende periodisch eine Message und empfange das
	 * Echo über eine temp queue
	 * 
	 * @param brokerUri
	 *            die Broker URI
	 */
	public void runTest(String brokerUri) {
		try {
			// Factory für Verbindungen zu einem JMS Server
			ActiveMQConnectionFactory connectionFactory =
					new ActiveMQConnectionFactory("sys", "man",
							brokerUri);
			// "tcp://192.168.111.102:61616");
			// connection aufbauen, konfigurieren und starten
			Connection connection = connectionFactory.createConnection();
			connection.setExceptionListener(excListener);
			connection.start();
			// session, queue und temporary queue anlegen
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(AuthenticationServer.LOGINQ);
			reply = session.createTemporaryQueue();
			// producer ohne bestimmte queue anlegen und konfigurieren
			requestProducer = session.createProducer(null);
			requestProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// consumer für die reply (temporary) queue anlegen und mit einem
			// MessageListener verbinden
			MessageConsumer consumer = session.createConsumer(reply);
			consumer.setMessageListener(msgListener);
			// jetzt kann gesendet werden
			Message testMessage = makeLoginMsg("schlapp", "hut");
			requestProducer.send(destination, testMessage);
			Thread.sleep(2000);
			testMessage = makeLoginMsg("hut", "schnur");
			requestProducer.send(destination, testMessage);
			Thread.sleep(2000);
			testMessage = makeLoginMsg("potz", "blitz");
			requestProducer.send(destination, testMessage);
			Thread.sleep(2000);
			testMessage = makeLoginMsg("muetze", "cap");
			requestProducer.send(destination, testMessage);
			Thread.sleep(2000);
			System.exit(0);

		} catch (Exception e) {
			System.out.println("[EchoRequestQ] Caught: " + e);
			e.printStackTrace();
		}

	}

	/**
	 * lege testweise login message an
	 * 
	 * @param un
	 *            user name
	 * @param pw
	 *            passwort
	 * @return die fertige message
	 * @throws JMSException
	 *             da ging was schief
	 */
	private Message makeLoginMsg(String un, String pw) throws JMSException {
		Message testMessage = session
				.createTextMessage();
		// eine Message besitzt optional verschiedene Properties
		testMessage.setJMSReplyTo(reply);
		testMessage.setJMSDestination(destination);
		testMessage.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.login.toString());
		testMessage.setStringProperty(
				MessageHeader.LoginUser.toString(), un);
		testMessage.setStringProperty(
				MessageHeader.LoginPassword.toString(), pw);
		return testMessage;
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
		private ArrayList<String> tokens = new ArrayList<>();
		private Destination playerServiceQ;

		@Override
		public void onMessage(Message replyMessage) {
			try {
				if (replyMessage instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) replyMessage;
					String msgKind = textMessage
							.getStringProperty(MessageHeader.MsgKind.toString());
					String token = textMessage
							.getStringProperty(MessageHeader.AuthToken
									.toString());
					if (token != null && msgKind.equals(MessageKind.authenticated.toString())) {
						tokens.add(token);
						playerServiceQ = textMessage.getJMSReplyTo();
						;
					}
					System.out.println("Received " + msgKind + ": " + token
							+ " (" + textMessage.getText() + ")");

				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}
