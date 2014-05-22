package messaging;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;

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
import javax.swing.SwingUtilities;

import org.apache.activemq.ActiveMQConnectionFactory;

import States.ChatClientState;
import de.fh_zwickau.pti.mqgamecommon.MQConstantDefs;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;


public class ChatJmsAdapter implements ChatServerMessageProducer{

	private static ChatJmsAdapter chatJmsAdapter = null;
	private String authToken = "";
	private Destination chatServiceQ;
	private Destination loginQ, reply;
	private Session session;
	private MessageProducer requestProducer;
	private ChatServerMessageReceiver messageReceiver;
	private ChatClientState state;
	
	public ChatJmsAdapter(){
	
	}	
	public void connectToServer(String brokerUri) {
		try {
			// Factory für Verbindungen zu einem JMS Server
			ActiveMQConnectionFactory connectionFactory =
					new ActiveMQConnectionFactory("sys", "man",
							brokerUri);
			// connection aufbauen, konfigurieren und starten
			Connection connection = connectionFactory.createConnection();
			connection.setExceptionListener(excListener);
			connection.start();
			// session, queue und temporary queue anlegen
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			loginQ = session.createQueue(MQConstantDefs.LOGINQ);
			reply = session.createTemporaryQueue();
			// producer ohne bestimmte queue anlegen und konfigurieren
			requestProducer = session.createProducer(null);
			requestProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// consumer für die reply (temporary) queue anlegen und mit einem
			// MessageListener verbinden
			MessageConsumer consumer = session.createConsumer(reply);
		consumer.setMessageListener(msgListener);
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
	@Override
	public void register(String uname, String pword) throws JMSException {
		Message message = createMessage(loginQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.register.toString());
		message.setStringProperty(
				MessageHeader.LoginUser.toString(), uname);
		message.setStringProperty(
				MessageHeader.ChatterNickname.toString(), uname);
		message.setStringProperty(
				MessageHeader.LoginPassword.toString(), pword);
		requestProducer.send(loginQ, message);
		
		
	}

	@Override
	public void login(String uname, String pword) throws JMSException {
		
		Message message = createMessage(loginQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.login.toString());
		message.setStringProperty(
				MessageHeader.LoginUser.toString(), uname);
		message.setStringProperty(
				MessageHeader.ChatterNickname.toString(), uname);
		message.setStringProperty(
				MessageHeader.LoginPassword.toString(), pword);
		requestProducer.send(loginQ, message);
		
		
	}

	@Override
	public void logout() throws JMSException {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.logout.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		requestProducer.send(loginQ, message);
		
	}

	@Override
	public void setMessageReceiver(ChatServerMessageReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
		
	}

	@Override
	public void deny() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestParticipian() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startChat() throws JMSException {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.chatterMsgStartChat.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		requestProducer.send(chatServiceQ, message);
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void leave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void acceptInvitation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chat() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invite() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reject() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept() {
		// TODO Auto-generated method stub
		
	}

	private TextMessage createMessage(Destination destination) throws JMSException {
		TextMessage textMessage = session
				.createTextMessage();
		// eine Message besitzt optional verschiedene Properties
		textMessage.setJMSReplyTo(reply);
		textMessage.setJMSDestination(destination);
		return textMessage;
	}
	
	private MessageListener msgListener = new MessageListener() {
		@Override
		public void onMessage(Message replyMessage) {
			System.out.println("Client: "+replyMessage.toString());
			try {
				if (replyMessage instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) replyMessage;
					String msgKind = textMessage
							.getStringProperty(MessageHeader.MsgKind.toString());
					MessageKind messageKind = MessageKind.valueOf(msgKind);
					System.out.println("client2: "+messageKind);
					switch (messageKind) {
					case authenticated:
						authToken = textMessage
								.getStringProperty(MessageHeader.AuthToken
										.toString());
						chatServiceQ = textMessage.getJMSReplyTo();
						if(state != null)
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									state.gotSucess();
									
								}
							});
						break;
					case failed:
						if(state != null)
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									
									state.gotFail();
								}
							});
						break;
					case loggedOut:
						if(state != null)
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									state.gotLogout();
								}
							});
						break;

					default:
						System.out.println(messageKind.toString() );
						break;
					}
				}//else{
					//if(replyMessage instanceof Message)
					//System.out.println("onlymessage");
					//Message textMessage = (Message) replyMessage;
					//String msgKind = textMessage
					//		.getStringProperty(MessageHeader.MsgKind.toString());
					//MessageKind messageKind = MessageKind.valueOf(msgKind);
					//System.out.println(messageKind);
				//}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	public static ChatJmsAdapter getInstance()
	{
	
		if(chatJmsAdapter==null){
			chatJmsAdapter= new ChatJmsAdapter();
		}
		return chatJmsAdapter;
	}
	public void setState(States.ChatClientState state) {
		this.state=state;
		
	}
}
