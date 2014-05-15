package demo;

import java.util.Enumeration;

import javax.jms.BytesMessage;
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

public class BytesInvoker implements ExceptionListener, MessageListener {

	private Session session;
	private Destination destination, reply;
	private MessageProducer requestProducer;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BytesInvoker echoInvoker = new BytesInvoker();
		echoInvoker.run();
	}

	public void run() {
		try {

			ActiveMQConnectionFactory connectionFactory =
					new ActiveMQConnectionFactory("sys", "man",
							// "tcp://192.168.111.102:61616");
							"tcp://localhost:61616");
			Connection connection = connectionFactory.createConnection();
			connection.setExceptionListener(this);
			connection.start();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue("binq");
			reply = session.createTemporaryQueue();

			requestProducer = session.createProducer(destination);
			requestProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			MessageConsumer consumer = null;
			consumer = session.createConsumer(reply);

			consumer.setMessageListener(this);

			byte[] load = new byte[409600];

			for (int i = 0; i < load.length; i++) {
				load[i] = (byte) i;
			}

			while (true) {
				String id = "" + System.currentTimeMillis();
				BytesMessage testMessage = session.createBytesMessage();
				testMessage.setJMSReplyTo(reply);
				testMessage.setJMSCorrelationID(id);
				testMessage.writeBytes(load);
				requestProducer.send(testMessage);
				Thread.sleep(5000);
			}

		} catch (Exception e) {
			System.out.println("[EchoRequestQ] Caught: " + e);
			e.printStackTrace();
		}

	}

	@Override
	public void onException(JMSException arg0) {
		arg0.printStackTrace();
	}

	@Override
	public void onMessage(Message replyMessage) {
		try {
			@SuppressWarnings("unchecked")
			Enumeration<String> props = replyMessage.getPropertyNames();
			String prop;
			while (props.hasMoreElements()) {
				prop = props.nextElement();
				System.out.println(prop + ": "
						+ replyMessage.getStringProperty(prop));
			}
			byte[] load = new byte[128];
			int count = ((BytesMessage) replyMessage).readBytes(load);
			System.out.print("bytes received: [");
			for (int i = 0; i < count; i++) {
				System.out.print(load[i] + ", ");
			}
			System.out.println("]");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
