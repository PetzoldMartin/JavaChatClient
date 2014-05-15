/**
 * 
 */
package dispatcher.example;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import dispatcher.CommunicationProvider;
import dispatcher.RequestProcessor;

/**
 * @author georg beier
 * 
 */
public class ExampleRequestServer {

	public static final String REQUEST_Q = "request";
	public static final String tcpPort = ":61606";
	public static final String stompPort = ":61603";
	private static String ipAddr = "localhost";

	private Session session;
	private Destination requestDestination, serviceReplyDestination;
//	private MessageProducer replyProducer;

	/**
	 * main programm stellt JMS Message Broker Service zur Verfügung und startet
	 * den RequestServer
	 */
	public static void main(String[] args) throws Exception {
		BrokerService broker = new BrokerService();
		broker.setUseJmx(false);
		broker.setPersistent(false);
		try {
			InetAddress addr = InetAddress.getLocalHost();
			// Get IP Address
			ipAddr = addr.getHostAddress();
			System.out.format("ip %s%n", ipAddr);
		} catch (UnknownHostException e) {
		}
		broker.addConnector("tcp://localhost:61616");
		broker.addConnector("stomp://localhost:61613");
		broker.addConnector("tcp://" + ipAddr + tcpPort);
		broker.addConnector("stomp://" + ipAddr + stompPort);
		broker.deleteAllMessages();
		broker.start();
		System.out.println("isDeleteAllMessagesOnStartup -> "
			+ broker.isDeleteAllMessagesOnStartup());//
		ExampleRequestServer requestServer = new ExampleRequestServer();
		requestServer.runServer();
	}

	public void runServer() {
		try {
			// verbinde Server mit dem lokalen JMS Broker (aus main)
			ActiveMQConnectionFactory connectionFactory =
				new ActiveMQConnectionFactory("sys", "man", "tcp://" + ipAddr
					+ tcpPort);
			// connection aufbauen, konfigurieren und starten
			Connection connection = connectionFactory.createConnection();
			// connection.setExceptionListener(excListener);
			connection.start();
			// connection für die request prozessoren bekannt machen
			CommunicationProvider.getProvider().setConnection(connection);
			// session anlegen, dazu mehrere Queues
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			requestDestination = session.createQueue(REQUEST_Q);
			serviceReplyDestination = session.createTemporaryQueue();
			RequestProcessor.setServiceReplyQ(serviceReplyDestination);
			// consumer für die queues anlegen und mit Listenern verknüpfen
			session.createConsumer(requestDestination).setMessageListener(
				requestListener);
			session.createConsumer(serviceReplyDestination).setMessageListener(
				serviceReplyListener);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * bearbeitet requests von android clients
	 */
	private MessageListener requestListener = new MessageListener() {

		@Override
		public void onMessage(Message request) {
			try {
//				System.out.println("requested");
				RequestProcessor requestProcessor = new ExampleRequestProcessor(
					request);
				RequestProcessor.process(requestProcessor);
			} catch (JMSException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * bearbeite service replies von service prozessen
	 */
	private MessageListener serviceReplyListener = new MessageListener() {

		@Override
		public void onMessage(Message serviceReply) {
			RequestProcessor.process(serviceReply);
		}
	};

}
