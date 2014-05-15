package dispatcher.example;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.List;
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
 * Beispiel für den Aufruf eines requests über die JMS Queue mit Empfang einer
 * Antwort über eine Temporary Queue
 * 
 * @author georg beier
 * 
 */
public class Requestor {

	private static String ipAddr = "localhost";

	private static final int maxAhead = 10;
	
	private static final SimpleLogger log = new SimpleLogger(Requestor.class);

	private Session session;
	private Destination destination, reply;
	private MessageProducer requestProducer;
	private String ipBase = ipAddr + System.identityHashCode(this);
	private int aheadCount = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Requestor echoInvoker = new Requestor();
		try {
			InetAddress addr = InetAddress.getLocalHost();
			// Get IP Address
			ipAddr = addr.getHostAddress();
		} catch (UnknownHostException e) {
		}
		echoInvoker.runDemo();
	}

	/**
	 * baue eine Verbindung auf, sende periodisch einen Request und empfange das
	 * Echo über eine temp queue
	 */
	public void runDemo() {
		int msgCount = 0;
		long reftime, basetime = 0;
		try {
			// Factory für Verbindungen zu einem JMS Server
			ActiveMQConnectionFactory connectionFactory =
				new ActiveMQConnectionFactory("sys", "man",
					"tcp://" + ipAddr + ExampleRequestServer.tcpPort);
			// connection aufbauen, konfigurieren und starten
			Connection connection = connectionFactory.createConnection();
			connection.setExceptionListener(excListener);
			connection.start();
			// session, queue und temporary queue anlegen
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(ExampleRequestServer.REQUEST_Q);
			reply = session.createTemporaryQueue();
			// producer für die serviceRequestQ queue anlegen und konfigurieren
			requestProducer = session.createProducer(destination);
			requestProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// consumer für die reply (temporary) queue anlegen und mit einem
			// MessageListener verbinden
			MessageConsumer consumer = session.createConsumer(reply);
			consumer.setMessageListener(msgListener);
			// jetzt kann gesendet werden
			Double[] numbers = new Double[10];
			Random random = new Random(System.nanoTime());
			int msgNo = 0;
			while (true) {
				reftime = System.nanoTime();
				if (msgCount == 0)
					basetime = reftime;
				String id = "msg-" + msgNo++; //ipBase + reftime;
				for (int i = 0; i < numbers.length; i++) {
					numbers[i] = random.nextDouble() * 1.e7;
				}
				Message testMessage = session
					.createObjectMessage(numbers);
				// eine Message besitzt optional verschiedene Properties
				testMessage.setJMSReplyTo(reply);
				testMessage.setJMSCorrelationID(id);
				requestProducer.send(testMessage);
				if (++msgCount >= 10) {
					long roundtrip = (reftime - basetime) / msgCount;
					log.info("Message Roundtrip is " + roundtrip
						+ "nsec = " + roundtrip / 1000000 + " msec");
					msgCount = 0;
				}
				 log.debug(id+"->");
				addAhead();
				while (getAhead() > maxAhead) {
					Thread.sleep(20);
				}
			}

		} catch (Exception e) {
			log.error("[RequestQ] Caught: " + e);
		}

	}

	private synchronized int addAhead() {
		return ++aheadCount;
	}

	private synchronized int subAhead() {
		return --aheadCount;
	}

	private synchronized int getAhead() {
		return aheadCount;
	}

	/**
	 * lokale Implementierung für einen MessageListener
	 * 
	 * @author georg beier
	 */
	private MessageListener msgListener = new MessageListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void onMessage(Message replyMessage) {
			int count = 0;
			StringBuilder sb = new StringBuilder();
			Formatter f = new Formatter(sb);
			try {
				subAhead();
				if (replyMessage instanceof MapMessage) {
					MapMessage message = (MapMessage) replyMessage;
					List<Double> numbers = (List<Double>) message
						.getObject("numbers");
					double sum = 0., min = 0., max = 0.;
					for (Double num : numbers) {
						sum += num;
						min = Math.min(min, num);
						max = Math.max(max, num);
					}
					double msum = message.getDouble("sum");
					double mmax = message.getDouble("max");
					double mmin = message.getDouble("min");
//					f.format("%d # %d | %d # %d | %d # %d", sum,
//						msum, min, mmin, max, mmax);
//					if (sum != msum || max != mmax || min != mmin)
//						log.warn(sb.toString());
//					else
//						log.debug(sb.toString());
				} else {
					log.error(replyMessage.getClass().getSimpleName()
						+ ": " + replyMessage);
				}
			} catch (JMSException e) {
				log.errorException(e);
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
		public void onException(JMSException e) {
			log.errorException(e);
		}
	};
}
