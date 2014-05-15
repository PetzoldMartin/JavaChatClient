package demo;

import java.util.Enumeration;

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

/**
 * Beispiel für den Aufruf einer JMS Queue mit Empfang einer Antwort über eine
 * Temporary Queue
 * 
 * @author georg beier
 * 
 */
public class EchoInvoker {

	private Session session;
	private Destination destination, reply;
	private MessageProducer requestProducer;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EchoInvoker echoInvoker = new EchoInvoker();
		echoInvoker.runDemo();
	}

	/**
	 * baue eine Verbindung auf, sende periodisch eine Message und empfange das
	 * Echo über eine temp queue
	 */
	public void runDemo() {
		try {
			// Factory für Verbindungen zu einem JMS Server
			ActiveMQConnectionFactory connectionFactory = 
				new ActiveMQConnectionFactory("sys", "man",
						"tcp://localhost:61616");
//		"tcp://192.168.111.102:61616");
			// connection aufbauen, konfigurieren und starten
			Connection connection = connectionFactory.createConnection();
			connection.setExceptionListener(excListener);
			connection.start();
			// session, queue und temporary queue anlegen
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue("eq");
			reply = session.createTemporaryQueue();
			// producer für die serviceRequestQ queue anlegen und konfigurieren
			requestProducer = session.createProducer(destination);
			requestProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// consumer für die reply (temporary) queue anlegen und mit einem
			// MessageListener verbinden
			MessageConsumer consumer = session.createConsumer(reply);
			consumer.setMessageListener(msgListener);
			// jetzt kann gesendet werden
			while (true) {
				String id = "" + System.currentTimeMillis();
				// es gibt verschiedene Arten von Messages, hier eine TextMessage
				Message testMessage = session
					.createTextMessage("Test Message @" + id);
				// eine Message besitzt optional verschiedene Properties
				testMessage.setJMSReplyTo(reply);
				testMessage.setJMSCorrelationID(id);
				requestProducer.send(testMessage);
				Thread.sleep(5000);
			}

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
				Enumeration<String> props = replyMessage.getPropertyNames();
				String prop;
				while (props.hasMoreElements()) {
					prop = props.nextElement();
					System.out.println(prop + ": "
						+ replyMessage.getStringProperty(prop));
				}
				if (replyMessage instanceof TextMessage) {
					System.out.println("type-> " + replyMessage.getJMSType()
						+ "\nCorrelationID-> "
						+ replyMessage.getJMSCorrelationID() + "\nMessageID-> "
						+ replyMessage.getJMSMessageID() + "\nDestination-> "
						+ replyMessage.getJMSDestination() + "\nMessage-> "
						+ ((TextMessage) replyMessage).getText());
				} else {
					System.out.println(replyMessage.getClass().getSimpleName()
						+ ": " + replyMessage);
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}
