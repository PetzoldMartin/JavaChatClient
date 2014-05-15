/**
 * 
 */
package dispatcher;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.grlea.log.SimpleLogger;

/**
 * <<Singleton>> klasse, stellt jedem nutzenden thread eigene objekte für die
 * message-basierte kommunikation zur verfügung
 * 
 * @author georg beier
 * 
 */
public class CommunicationProvider {

	/**
	 * Singleton instanz
	 */
	private static final CommunicationProvider provider = new CommunicationProvider();

	private static final SimpleLogger log = new SimpleLogger(
		CommunicationProvider.class);

	private Connection commonConnection;

	/**
	 * verwalte sessions, eine pro thread
	 */
	private ConcurrentHashMap<Thread, ComPool> sessions =
		new ConcurrentHashMap<Thread, ComPool>();

	/**
	 * hole die instanz
	 * 
	 * @return die singleton instanz
	 */
	public static CommunicationProvider getProvider() {
		return provider;
	}

	/**
	 * hole Session für den aktuellen thread aus der map. wenn noch
	 * keine angelegt wurde, lege sie jetzt an.
	 * 
	 * @return Session objekt exklusiv für diesen thread
	 */
	public Session getSession() {
		Thread thread = Thread.currentThread();
		if (sessions.containsKey(thread)) {
			return sessions.get(thread).getSession();
		} else if (commonConnection != null) {
			ComPool cp = new ComPool(commonConnection);
			sessions.put(thread, cp);
			return cp.getSession();
		} else {
			throw new RuntimeException("connection wurde nicht initialisiert");
		}
	}

	/**
	 * hole MessageProducer für den aktuellen thread aus der map. wenn noch
	 * keine angelegt wurde, lege sie jetzt an.
	 * 
	 * @return MessageProducer objekt exklusiv für diesen thread
	 */
	public MessageProducer getProducer(String dest) {
		Thread thread = Thread.currentThread();
		if (sessions.containsKey(thread)) {
			return sessions.get(thread).getProducer(dest);
		} else if (commonConnection != null) {
			ComPool cp = new ComPool(commonConnection);
			sessions.put(thread, cp);
			return cp.getProducer(dest);
		} else {
			throw new RuntimeException("connection wurde nicht initialisiert");
		}
	}

	/**
	 * übergib eine connection für die processor objekte
	 * 
	 * @param conn
	 *            eine existierende connection
	 */

	public void setConnection(
		Connection conn) {
		commonConnection = conn;
	}

	private static class ComPool {
		private Session session;
		private HashMap<String, MessageProducer> producers =
			new HashMap<String, MessageProducer>();

		public ComPool(Connection connection) {
			try {
				session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			} catch (JMSException e) {
				log.errorException(e);
			}
		}

		public Session getSession() {
			return session;
		}

		public MessageProducer getProducer(String forQ) {
			if (forQ == null)
				forQ = "";
			if (!producers.containsKey(forQ)) {
				try {
					MessageProducer producer;
					if (forQ.length() == 0)
						producer = session.createProducer(null);
					else
						producer = session.createProducer(session
							.createQueue(forQ));
					producers.put(forQ, producer);
					return producer;
				} catch (JMSException e) {
					log.errorException(e);
					return null;
				}
			} else {
				return producers.get(forQ);
			}
		}
	}

}
