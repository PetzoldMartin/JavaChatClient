package messaging;

import java.awt.TrayIcon.MessageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

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

public class ChatJmsAdapter implements ChatServerMessageProducer {

	private static ChatJmsAdapter chatJmsAdapter = null;
	private String authToken = "";
	private Destination chatServiceQ;
	private Destination loginQ, reply;
	private Session session;
	private MessageProducer requestProducer;
	private ChatServerMessageReceiver messageReceiver;
	private ChatClientState state;
	private String CID, RefID, messageText;
	private ArrayList<String> chatters;

	private ArrayList<ChatChatterRelationship> chatsAndChatters;

	public String getCID() {
		return CID;
	}

	public String getRefID() {
		return RefID;
	}

	public ChatJmsAdapter() {

	}

	@Override
	public void connectToServer(String brokerUri) {
		try {
			// Factory für Verbindungen zu einem JMS Server
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					"sys", "man", brokerUri);
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
		message.setStringProperty(MessageHeader.LoginUser.toString(), uname);
		message.setStringProperty(MessageHeader.ChatterNickname.toString(),
				uname);
		message.setStringProperty(MessageHeader.RefID.toString(), uname);
		message.setStringProperty(MessageHeader.LoginPassword.toString(), pword);
		requestProducer.send(loginQ, message);

	}

	@Override
	public void login(String uname, String pword) throws JMSException {
		Message message = createMessage(loginQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.login.toString());
		message.setStringProperty(MessageHeader.LoginUser.toString(), uname);
		message.setStringProperty(MessageHeader.ChatterNickname.toString(),
				uname);
		message.setStringProperty(MessageHeader.LoginPassword.toString(), pword);
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
	public void requestParticipian(String cID) throws JMSException {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.chatterMsgRequestParticipation.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setStringProperty(MessageHeader.RefID.toString(), cID);
		requestProducer.send(chatServiceQ, message);

	}

	@Override
	public void startChat() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterMsgStartChat
				.toString());
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
		sendParameterLessSimpleRequest(MessageKind.chatterMsgAcceptInvitation
				.toString());
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
				MessageKind.chatterMsgInvite.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setStringProperty(MessageHeader.RefID.toString(), CNN);
		message.setStringProperty(MessageHeader.ChatroomID.toString(), CID);
		requestProducer.send(chatServiceQ, message);

	}

	@Override
	public void reject() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterMsgReject.toString());

	}

	@Override
	public void accept() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterMsgAccept.toString());

	}

	@Override
	public void askForChats() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterMsgChats.toString());
	}

	@Override
	public void askForChatters() throws JMSException {
		sendParameterLessSimpleRequest(MessageKind.chatterMsgChatters
				.toString());
	}

	private TextMessage createMessage(Destination destination)
			throws JMSException {
		TextMessage textMessage = session.createTextMessage();
		// eine Message besitzt optional verschiedene Properties
		textMessage.setJMSReplyTo(reply);
		textMessage.setJMSDestination(destination);
		return textMessage;
	}

	private MessageListener msgListener = new MessageListener() {

		@Override
		public void onMessage(Message replyMessage) {
			System.out.println("Client: " + replyMessage.toString());
			try {
				if (replyMessage instanceof Message) {
					Message textMessage = (Message) replyMessage;
					// System.out.println("Client: "+textMessage.toString());

					String msgKind = textMessage
							.getStringProperty(MessageHeader.MsgKind.toString());
					RefID = textMessage.getStringProperty(MessageHeader.RefID
							.toString());
					CID = textMessage
							.getStringProperty(MessageHeader.ChatroomID
									.toString());
					MessageKind messageKind = MessageKind.valueOf(msgKind);

					// get Text if we had Some
					if (replyMessage instanceof TextMessage) {
						TextMessage messageIn = (TextMessage) replyMessage;
						messageText = messageIn.getText();
					}
					switch (messageKind) {
					case authenticated:
						authToken = textMessage
								.getStringProperty(MessageHeader.AuthToken
										.toString());
						chatServiceQ = textMessage.getJMSReplyTo();
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									state.gotSucess();
								}
							});
						break;
					case failed:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotFail();
								}
							});
						break;
					case loggedOut:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotLogout();
								}
							});
						break;
					case clientChatStarted:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotChatStarted(CID);
								}
							});
						break;
					case clientNewChat:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotNewChat(RefID, messageText);
								}
							});
						break;
					case clientChatClosed:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotChatClosed();
								}
							});
						break;
					case clientRequest:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotRequest(RefID);
								}
							});
						break;
					case clientParticipating:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotParticipating();
								}
							});
						break;
					case clientRejected:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotRejected();
								}
							});
						break;
					case clientRequestCancelled:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotRequestCancelled(RefID);
								}
							});
						break;
					case clientInvitation:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotInvite(RefID, CID);
								}
							});
						break;
					case clientAccepted:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotAccepted(RefID);
								}
							});
						break;
					case clientDenied:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotDenied(RefID);
								}
							});
						break;
					case clientParticipantEntered:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotParticipantEntered(RefID);
								}
							});
						break;
					case clientParticipantLeft:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotParticipantLeft(RefID);
								}
							});
						break;
					case clientAnswerChats:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									setChatsAndChatters(messageText);
									state.gotChats(chatsAndChatters);
								}
							});
						break;
					case clientAnswerChatters:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									setChatters(messageText);
									state.gotChatters(chatters);
								}
							});
						break;
					default:
						System.out.println(" Kind: " + messageKind.toString()
								+ " Text: " + messageText);
						break;
					}
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/**
	 * get instance of {@link ChatJmsAdapter}
	 * @return instance of {@link ChatJmsAdapter}
	 */
	public static ChatJmsAdapter getInstance() {
		if (chatJmsAdapter == null) {
			chatJmsAdapter = new ChatJmsAdapter();
		}
		return chatJmsAdapter;
	}
	/**
	 * 
	 * @param state
	 */
	public void setState(States.ChatClientState state) {
		this.state = state;
	}
	/**
	 * method for an parameterless Msg
	 * @param Msgkind
	 * @throws JMSException
	 */
	private void sendParameterLessSimpleRequest(String Msgkind)
			throws JMSException {
		// TODO commit
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(), Msgkind);
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setStringProperty(MessageHeader.ChatroomID.toString(), CID);
		message.setStringProperty(MessageHeader.RefID.toString(), RefID);
		requestProducer.send(chatServiceQ, message);
	}
	
	/**
	 * read chatters out of a string
	 * @param chatters
	 */
	private void setChatters(String chatters) {
		this.chatters = new ArrayList<String>();
		Scanner scanner = new Scanner(chatters);
		while (scanner.hasNextLine())
			this.chatters.add(scanner.nextLine());
		scanner.close();

	}
	/**
	 *  read chatrooms with his owners out of a string
	 * @param chatsAndChatters
	 */
	private void setChatsAndChatters(String chatsAndChatters) {
		this.chatsAndChatters = new ArrayList<ChatChatterRelationship>();
		Scanner scanner = new Scanner(chatsAndChatters);
		while (scanner.hasNextLine()) {
			String[] segs = scanner.nextLine().split(Pattern.quote(":"));
			if(segs.length>1)
			this.chatsAndChatters.add(new ChatChatterRelationship(segs[0],
					segs[1]));
			else this.chatsAndChatters.add(new ChatChatterRelationship(segs[0],
				""));
		}
		scanner.close();
	}
}
