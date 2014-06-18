package de.fh_zwickau.pti.jms.userservice;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.fh_zwickau.pti.jms.userservice.chat.ChatterFactory;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * JMS Service Provider und Demo-Programm für unterschiedliche ActiveMQ
 * Features
 * 
 * @author georg beier
 * 
 */
public class AuthenticationServer {

	public static final String LOGINQ = "loginq";
	public static final String USERQ = "userq";
	private Session session;
	private Destination logInOutDestination, userDestination;
	private MessageProducer replyProducer;
	private final String brokerUri;
	private final UserFactory userFactory;

	/**
	 * Servewr anlegen
	 * 
	 * @param brUri
	 *            URI des MessageBrokers
	 * @param pf
	 *            factory object to create players or subclasses of player
	 */
	public AuthenticationServer(String brUri, UserFactory pf) {
		brokerUri = brUri;
		userFactory = pf;
	}

	/**
	 * main programm stellt JMS Message Broker Service zur Verfügung und
	 * startet den AuthenticationServer, wenn Connectoren als
	 * Kommandozeilenargumente angegeben sind
	 * 
	 * @param args
	 *            args[0] URI des AMQ Brokers. Wenn args.length == 0 wird
	 *            lokaler Broker angelegt.
	 */
	public static void main(String[] args) throws Exception {
		String brokerUri;
		if (args.length == 0 || args.length == 1) {
			String[] localConnections = { "tcp://localhost:61616",
					"stomp://localhost:61613" };
			brokerUri = localConnections[0];
			// lokalen AMQ Server anlegen
			ArrayList<String> interfaces = new ArrayList<>();
			Enumeration<NetworkInterface> nis = NetworkInterface
					.getNetworkInterfaces();
			// Liste der IP-Adressen der Netzwerkinterfaces aufbauen
			while (nis.hasMoreElements()) {
				NetworkInterface ni = nis.nextElement();
				Enumeration<InetAddress> ifs = ni.getInetAddresses();
				while (ifs.hasMoreElements()) {
					InetAddress myIp = ifs.nextElement();
					if (!myIp.isLoopbackAddress()
							&& myIp.getAddress().length == 4) {
						interfaces.add(myIp.getHostAddress());
						System.out.print(myIp.getHostAddress() + " -> ");
						System.out.println(myIp.getCanonicalHostName());
					}
				}
			}
			BrokerService broker = new BrokerService();
			broker.setUseJmx(false);
			for (String connection : localConnections) {
				if (connection.matches("\\w+://localhost:\\d{4,5}")) {
					broker.addConnector(connection);
					for (String hostAddress : interfaces) {
						String conn = connection.replaceFirst("://.*:", "://"
								+ hostAddress + ":");
						broker.addConnector(conn);
						System.out.println("Connector on " + conn);
					}
				}

			}
			broker.start();
		} else {
			brokerUri = args[1];
		}
		UserFactory factory;
		if (args.length > 0) {
			factory = new UserFactory();
			System.out.println("creating players");
		} else {
			factory = new ChatterFactory();
			System.out.println("creating chatters");
		}
		AuthenticationServer authenticationServer = new AuthenticationServer(
				brokerUri, factory);
		authenticationServer.runServer();
	}

	public void runServer() {
		try {
			// verbinde Server mit dem JMS Broker
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					"sys", "man", brokerUri);
			// connection aufbauen, konfigurieren und starten
			Connection connection = connectionFactory.createConnection();
			connection.setExceptionListener(exceptionListener);
			connection.start();
			// session anlegen
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			logInOutDestination = session.createQueue(LOGINQ);
			userDestination = session.createQueue(USERQ);
			// producer anlegen, der nicht an eine bestimmte logInOutDestination
			// gebunden ist
			replyProducer = session.createProducer(null);
			replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// consumer für die queues anlegen und mit Listenern verknüpfen
			session.createConsumer(logInOutDestination).setMessageListener(
					logInOutListener);

		} catch (Exception e) {
			Logger.getRootLogger()
					.log(Level.ERROR, "Server startup error " + e);
		}

	}

	private final ExceptionListener exceptionListener = new ExceptionListener() {
		@Override
		public void onException(JMSException e) {
			Logger.getRootLogger().log(Level.ERROR, "Server error " + e);
		}
	};

	/**
	 * listener für login messages
	 */
	private final MessageListener logInOutListener = new MessageListener() {
		/**
		 * wird per callback für jede eingehende Methode der verbundenen Queue
		 * aufgerufen
		 * 
		 * @param message
		 *            Nachricht vn einem Client (direkt oder via PlayerService)
		 */
		@Override
		public void onMessage(Message message) {
			System.out.println(message.toString());
			String msgKind = "";
			try {
				msgKind = message.getStringProperty(MessageHeader.MsgKind
						.toString());
				MessageKind kind = MessageKind.valueOf(msgKind);
				switch (kind) {
				case login:
				case register:
					String uname = message
							.getStringProperty(MessageHeader.LoginUser
									.toString());
					String pword = message
							.getStringProperty(MessageHeader.LoginPassword
									.toString());
					User p;
					UserDto dto = new UserDto();
					dto.setUsername(uname);
					dto.setChatter(userFactory instanceof ChatterFactory);
					if (kind == MessageKind.register)
						p = userFactory.registerUser(uname, pword);
					else
						p = userFactory.createUser(uname, pword);
					if (p != null) {
						ObjectMessage replyMessage = session
								.createObjectMessage(dto);
						// sende neue Message an den PlayerService
						replyMessage.setJMSDestination(userDestination);
						// setze ReplyDestination auf die TempQueue des Clients
						replyMessage.setJMSReplyTo(message.getJMSReplyTo());
						// Informationen im Message Header setzen
						replyMessage.setStringProperty(
								MessageHeader.AuthToken.toString(),
								p.getToken());
						replyMessage.setStringProperty(
								MessageHeader.MsgKind.toString(),
								MessageKind.authenticated.toString());
						// Message absenden
						replyProducer.send(userDestination, replyMessage);
					} else {
						TextMessage textMessage = session.createTextMessage();
						if (kind == MessageKind.login)
							textMessage.setText("Login failed");
						else
							textMessage.setText("Register failed");
						// direkt zurück an den Client
						textMessage.setJMSDestination(message.getJMSReplyTo());
						// Informationen im Message Header setzen
						textMessage.setStringProperty(
								MessageHeader.MsgKind.toString(),
								MessageKind.failed.toString());
						// Message absenden
						replyProducer.send(textMessage.getJMSDestination(),
								textMessage);
					}
					break;
				case logout:
					String token = message
							.getStringProperty(MessageHeader.AuthToken
									.toString());
					TextMessage replyMessage = session.createTextMessage();
					Destination replyDestination = message.getJMSReplyTo();
					replyMessage.setJMSDestination(replyDestination);
					replyMessage.setStringProperty(
							MessageHeader.MsgKind.toString(),
							MessageKind.loggedOut.toString());
					replyProducer.send(replyDestination, replyMessage);
					ObjectMessage invalidateMessage = session
							.createObjectMessage();
					invalidateMessage.setStringProperty(
							MessageHeader.AuthToken.toString(), token);
					invalidateMessage.setStringProperty(
							MessageHeader.MsgKind.toString(),
							MessageKind.loggedOut.toString());
					invalidateMessage.setJMSDestination(userDestination);
					replyProducer.send(userDestination, invalidateMessage);
					break;
				default:
					break;
				}
			} catch (IllegalArgumentException e) {
				Logger.getRootLogger().log(Level.ERROR,
						"Unknown message kind " + msgKind);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void loadUserFromDatabase() {
			// TODO Auto-generated method stub

		}
	};
}
