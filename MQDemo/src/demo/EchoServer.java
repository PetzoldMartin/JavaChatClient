package demo;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.ImageObserver;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.ArrayList;
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
import org.apache.activemq.broker.BrokerService;

/**
 * JMS Service Provider und Demo-Programm für unterschiedliche ActiveMQ Features
 * 
 * @author georg beier
 * 
 */
public class EchoServer {

	private Session session;
	private Destination textDestination, binDestination, photoDestination;
	private MessageProducer replyProducer;
	private static int sequenceNo = 0;

	private static synchronized int getSequenceNo() {
		return sequenceNo++;
	}

	/**
	 * main programm stellt JMS Message Broker Service zur Verfügung und startet
	 * den EchoServer
	 */
	public static void main(String[] args) throws Exception {
		BrokerService broker = new BrokerService();
		broker.setUseJmx(false);
		broker.addConnector("tcp://localhost:61616");
		broker.addConnector("stomp://localhost:61613");
		Enumeration<NetworkInterface> nis = NetworkInterface
				.getNetworkInterfaces();
		while (nis.hasMoreElements()) {
			NetworkInterface ni = nis.nextElement();
			Enumeration<InetAddress> ifs = ni.getInetAddresses();
			while (ifs.hasMoreElements()) {
				InetAddress myIp = ifs.nextElement();
				if (!myIp.isLoopbackAddress() && myIp.getAddress().length == 4) {
					System.out.print(myIp.getHostAddress() + " -> ");
					System.out.println(myIp.getCanonicalHostName());
					broker.addConnector("tcp://" + myIp.getHostAddress() + ":61616");
					broker.addConnector("stomp://" + myIp.getHostAddress() + ":61613");
				} 
			}
		}
//		InetAddress ownIP=InetAddress.getLocalHost();
//		String myIp = ownIP.getHostAddress();
		broker.start();
		EchoServer echoServer = new EchoServer();
		echoServer.runServer();
	}

	public void runServer() {
		try {
			// verbinde Server mit dem lokalen JMS Broker (aus main)
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"sys", "man", "tcp://localhost:61616");
			// connection aufbauen, konfigurieren und starten
			Connection connection = connectionFactory.createConnection();
			connection.setExceptionListener(excListener);
			connection.start();
			// session anlegen, dazu mehrere Queues
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			textDestination = session.createQueue("eq");
			binDestination = session.createQueue("binq");
			photoDestination = session.createQueue("photoq");
			// producer anlegen, der nicht an eine bestimmte textDestination
			// gebunden ist
			replyProducer = session.createProducer(null);
			replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// consumer für die queues anlegen und mit Listenern verknüpfen
			session.createConsumer(textDestination).setMessageListener(
				msgListener);
			session.createConsumer(binDestination).setMessageListener(
				binListener);
			session.createConsumer(photoDestination).setMessageListener(
				imgListener);

		} catch (Exception e) {
			System.out.println("[EchoRequestQ] Caught: " + e);
			e.printStackTrace();
		}

	}

	private ExceptionListener excListener = new ExceptionListener() {
		@Override
		public void onException(JMSException arg0) {
			arg0.printStackTrace();
		}
	};

	/**
	 * listener für text messages, die geechot werden
	 */
	private MessageListener msgListener = new MessageListener() {
		/**
		 * wird per callback für jede eingehende Methode der verbundenen Queue
		 * aufgerufen
		 */
		@Override
		public void onMessage(Message message) {
			try {
				@SuppressWarnings("unchecked")
				Enumeration<String> props = message.getPropertyNames();
				String prop;
				while (props.hasMoreElements()) {
					prop = props.nextElement();
					System.out.println(prop + ": "
						+ message.getStringProperty(prop));
				}
				String text = ((TextMessage) message).getText();
				// was ist das für eine Message aus Java-Sicht
				String type = message.getClass().getName();
				System.out.println("Message Type: " + type);
				System.out.println("type-> " + message.getJMSType()
					+ "\nCorrelationID-> " + message.getJMSCorrelationID()
					+ "\nreplyTo-> " + message.getJMSReplyTo()
					+ "\nMessageID-> " + message.getJMSMessageID()
					+ "\nDestination-> " + message.getJMSDestination());
				System.out.println("StompMessage: " + text);

				TextMessage reply = session.createTextMessage();
				reply.setText("received " + text);
				reply.setJMSCorrelationID(message.getJMSCorrelationID());
				reply.setStringProperty("event-name", "echo");
				reply.setIntProperty("sequence-no", getSequenceNo());
				replyProducer.send(message.getJMSReplyTo(), reply);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/**
	 * listener für Byte-Messages
	 */
	private MessageListener binListener = new MessageListener() {
		/**
		 * wird per callback für jede eingehende Methode der verbundenen Queue
		 * aufgerufen
		 */
		@Override
		public void onMessage(Message message) {
			try {
				@SuppressWarnings("unchecked")
				// gib alle Properties der Message aus
				Enumeration<String> props = message.getPropertyNames();
				String prop;
				while (props.hasMoreElements()) {
					prop = props.nextElement();
					System.out.println(prop + ": "
						+ message.getObjectProperty(prop));
				}
				// was ist das für eine Message aus Java-Sicht
				String type = message.getClass().getName();
				System.out.println("Message Type: " + type);
				// was ist das für eine Message aus JMS-Sicht
				System.out.println("type-> " + message.getJMSType()
					// und andere Message-Attribute
					+ "\nCorrelationID-> " + message.getJMSCorrelationID()
					+ "\nreplyTo-> " + message.getJMSReplyTo()
					+ "\nMessageID-> " + message.getJMSMessageID()
					+ "\nDestination-> ");
				// hol die Bytes aus der Message
				if (message instanceof BytesMessage) {
					BytesMessage binMessage = (BytesMessage) message;
					byte[] buf = new byte[819200];
					System.out.println("read # of bytes: "
						+ binMessage.readBytes(buf));
				}
				// schick eine Bytes-Message zurück an die ReplyTo Queue
				BytesMessage reply = session.createBytesMessage();
				reply.writeBytes(new byte[] { 4, 3, 2, 1, 0, 7, 6, 5 });
				replyProducer.send(message.getJMSReplyTo(), reply);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private MessageListener imgListener = new ImgMessageListener();

	private class ImgMessageListener implements MessageListener, ImageObserver {
		private Image image;
		private int width = -1, height = -1;
		private ArrayList<byte[]> fragments = new ArrayList<byte[]>();
		private int bytes = 0;

		@Override
		public void onMessage(Message message) {
			if (message instanceof BytesMessage) {
				BytesMessage binMessage = (BytesMessage) message;
				try {
					int imgSize = (int) binMessage.getBodyLength();
					byte[] imageData = new byte[imgSize];
					String frag = binMessage.getStringProperty("fragment");
					int read = binMessage.readBytes(imageData);
					bytes += read;
					if (frag == null || frag.length() == 0) {
						showImage(imageData);
						TextMessage reply = session.createTextMessage("b*h: "
							+ width + " * " + height);
						replyProducer.send(message.getJMSReplyTo(), reply);
					} else if(frag.equals("FRAGMENT")) {
						fragments.add(imageData);
					} else if(frag.equals("ENDFRAGMENT")) {
						fragments.add(imageData);
						byte[] img = new byte[bytes];
						int ins = 0;
						for (byte[] fragment : fragments) {
							for(int i = 0; i < fragment.length; i++) {
								img[ins++] = fragment[i];
							}
						}
						for (int i = 0; i < fragments.size(); i++) {
							fragments.set(i, null);
						}
						fragments.clear();
						bytes = 0;
						showImage(img);
					}
				} catch (JMSException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// nix
				}
			}
		}

		/**
		 * @param imageData
		 * @throws InterruptedException
		 */
		private void showImage(byte[] imageData) throws InterruptedException {
			image = PhotoFrame.getInstance().showImage(imageData);
			MediaTracker tracker = new MediaTracker(
				PhotoFrame.getInstance());
			tracker.addImage(image, 0);
			image.getHeight(this);
			tracker.waitForAll();
			System.out.println("Image size is " + width + " * "
				+ height);
			PhotoFrame.getInstance().repaint();
		}

		@Override
		public boolean imageUpdate(Image img, int flags, int x, int y, int w,
			int h) {
			width = w;
			height = h;
			return true;
		}
	};
}
