package dispatcher.example;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Random;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.grlea.log.SimpleLogger;

/**
 * Basisklasse für die Bereitstellung von Services
 * 
 * @author georg beier
 * 
 */
public abstract class BasicService {

	private Session session;
	private Destination serviceRequestQ;
	private MessageProducer replyProducer;
	private Random random = new Random(System.nanoTime());
	
	protected static final SimpleLogger log = new SimpleLogger(BasicService.class);

	/**
	 * 
	 */
	protected static String ownIp() {
		String ipAddr = "localhost";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			// Get IP Address
			ipAddr = addr.getHostAddress();
		} catch (UnknownHostException e) {
		}
		return ipAddr;
	}

	/**
	 * baue benötigte Verbindungen auf
	 */
	protected void startService(String qName) {
		try {
			// Factory für Verbindungen zu einem JMS Server
			ActiveMQConnectionFactory connectionFactory =
				new ActiveMQConnectionFactory("sys", "man",
					"tcp://" + ownIp() + ExampleRequestServer.tcpPort);
			// connection aufbauen, konfigurieren und starten
			Connection connection = connectionFactory.createConnection();
			connection.setExceptionListener(excListener);
			connection.start();
			// session, queue und temporary queue anlegen
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			serviceRequestQ = session.createQueue(qName);
			// consumer für die serviceRequestQ anlegen und mit einem
			// MessageListener verbinden. der wird in der abgeleiteten klasse
			// gesetzt
			MessageConsumer consumer = session.createConsumer(serviceRequestQ);
			consumer.setMessageListener(msgListener);
			// producer anlegen, der nicht an eine bestimmte Destination
			// gebunden ist
			replyProducer = session.createProducer(null);
			replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		} catch (Exception e) {
		}
	}

	/**
	 * lokale Implementierung für einen MessageListener
	 * 
	 * @author georg beier
	 */
	protected MessageListener msgListener = new MessageListener() {
		@Override
		public void onMessage(Message serviceRequest) {
			try {
				if (serviceRequest instanceof ObjectMessage) {
					ObjectMessage message = (ObjectMessage) serviceRequest;
					Double[] numbers = (Double[]) message.getObject();
					MapMessage serviceReply = session.createMapMessage();
					String refId = serviceRequest.getJMSCorrelationID();
					serviceReply.setStringProperty("refId", refId);
					serviceReply.setJMSCorrelationID(serviceRequest
						.getJMSCorrelationID());
					log.debug("Request refId: " + refId);
					serviceCalculation(numbers, serviceReply);
					Thread
						.sleep(random.nextInt(2) + 1, random.nextInt(999999));
					replyProducer.send(message.getJMSReplyTo(), serviceReply);
				} else {
					log.error(serviceRequest.getClass()
						.getSimpleName()
						+ ": " + serviceRequest);
				}
			} catch (JMSException e) {
				log.error(e.toString());
			} catch (InterruptedException e) {
			}
		}
	};

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

	protected abstract void serviceCalculation(Double[] numbers,
		MapMessage serviceReply);
}
