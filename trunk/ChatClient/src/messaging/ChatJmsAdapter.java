package messaging;

import java.awt.TrayIcon.MessageType;

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
	@Override
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
				MessageHeader.RefID.toString(), uname);
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
	public void deny() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterMsgDeny.toString());
	}

	@Override
	public void requestParticipian(String cID,String refID) throws JMSException {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.chatterMsgRequestParticipation.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);

		message.setStringProperty(MessageHeader.ChatroomID.toString(), cID);
		message.setStringProperty(MessageHeader.ChatterNickname.toString(), refID);
		requestProducer.send(chatServiceQ, message);
		
	}

	@Override
	public void startChat() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterMsgStartChat.toString());
	}

	@Override
	public void cancel() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterMsgCancel.toString());
		
	}

	@Override
	public void leave() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterMsgLeave.toString());
		
	}

	@Override
	public void acceptInvitation() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterMsgAcceptInvitation.toString());
		
	}

	@Override
	public void close() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterMsgClose.toString());	
	}

	@Override
	public void chat(String messageText) throws JMSException {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.chatterMsgChat.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setText(messageText);
		requestProducer.send(chatServiceQ, message);
	}

	@Override
	public void invite(String CNN) throws JMSException {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.chatterMsgChat.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setStringProperty(MessageHeader.ChatterNickname.toString(), CNN);
		requestProducer.send(chatServiceQ, message);
		
		
	}

	@Override
	public void reject() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterMsgReject.toString());
		
	}

	@Override
	public void accept() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterAccepted.toString());
		
	}
	@Override
	public void askForChats() throws JMSException{
		sendParameterLessSimpleRequest(MessageKind.chatterMsgChats.toString());
	}
	
	@Override
	public void askForChatters() throws JMSException{
		sendParameterLessSimpleRequest(MessageKind.chatterMsgChatters.toString());
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
		private String messageText,messageCID;

		@Override
		public void onMessage(Message replyMessage) {
			System.out.println("Client: "+replyMessage.toString());
			try {
				if (replyMessage instanceof Message) {
					Message textMessage = (Message) replyMessage;
					//System.out.println("Client: "+textMessage.toString());

					String msgKind = textMessage
							.getStringProperty(MessageHeader.MsgKind.toString());
					MessageKind messageKind = MessageKind.valueOf(msgKind);
					messageCID=replyMessage.getStringProperty(MessageHeader.ChatroomID.toString());

					//get Text if we had Some
					if (replyMessage instanceof TextMessage) {
					TextMessage messageIn=(TextMessage)replyMessage;
					messageText = messageIn.getText();
					//System.out.println("Client: "+messageIn.toString());

					}
					//System.out.println("client2: "+messageKind);
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
					case clientChatStarted:
						if(state != null)
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									state.gotChatStarted();
									System.out.println("CID: "+messageCID);

								}
							});
						break;
					case clientNewChat:
						if(state != null)
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									state.gotNewChat(messageText);
								}
							});
						break;
					default:
						
						System.out.println(" Kind: "+messageKind.toString() +" Text: "+ messageText);
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
	
	private void sendParameterLessSimpleRequest(String Msgkind) throws JMSException{
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				Msgkind);
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		requestProducer.send(chatServiceQ, message);
	}
}
