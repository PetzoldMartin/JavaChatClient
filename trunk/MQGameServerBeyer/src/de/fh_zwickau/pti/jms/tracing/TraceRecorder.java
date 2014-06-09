/**
 * 
 */
package de.fh_zwickau.pti.jms.tracing;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.fh_zwickau.pti.jms.userservice.chat.ChatServer;

/**
 * @author georg beier
 * 
 */
public class TraceRecorder {

	private static final String brokerUri = "tcp://localhost:61616";

	private Connection connection;
	private Session session;
	private Queue tracingDestination;
	private MessageConsumer tracingConsumer;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TraceRecorder().run();
	}

	public void run() {
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
			tracingDestination = session.createQueue(TraceGenerator.TRACEQ);
			tracingConsumer = session.createConsumer(tracingDestination);
			tracingConsumer.setMessageListener(tracingListener);
		} catch (Exception e) {
			Logger.getRootLogger().log(Level.ERROR,
					"TraceRecorder startup error " + e);
		}

	}

	/**
	 * Listener für Error Messages
	 */
	private static ExceptionListener exceptionListener = new ExceptionListener() {
		@Override
		public void onException(JMSException e) {
			Logger.getRootLogger().log(Level.ERROR,
					"TraceRecorder error " + e);
		}
	};

	/**
	 * listener für tracing messages
	 */
	private MessageListener tracingListener = new MessageListener() {
		/**
		 * wird per callback für jede eingehende Methode der verbundenen Queue
		 * aufgerufen
		 * 
		 * @param message
		 *            Tracing Message
		 */
		@Override
		public void onMessage(Message message) {
			try {
				if (message instanceof ObjectMessage) {
					ObjectMessage traceMsg = (ObjectMessage) message;
					TraceRecord traceRecord = (TraceRecord) traceMsg
							.getObject();
//					Logger.getRootLogger().log(Level.DEBUG, traceRecord);
					System.err.println(traceRecord);
				}
			} catch (JMSException e) {
			}
		}
	};

}
