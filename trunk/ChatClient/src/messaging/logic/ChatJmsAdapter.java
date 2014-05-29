package messaging.logic;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

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

import messaging.interfaces.ChatServerMessageProducer;

import org.apache.activemq.ActiveMQConnectionFactory;

import States.ChatClientState;
import de.fh_zwickau.pti.mqgamecommon.MQConstantDefs;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

public class ChatJmsAdapter implements ChatServerMessageProducer {

	private ChatClientState state;

	private String authToken = "";
	private Destination chatServiceQ;
	private Destination loginQ, reply;
	private Session session;
	private MessageProducer requestProducer;

	private String chatroomId, referenceID, messageText;
	private ArrayList<String> chatters, chatsWithOwners;

	private ArrayList<ChatChatterRelationship> chatsAndChatters;

	public String getCID() {
		return chatroomId;
	}

	public String getRefID() {
		return referenceID;
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

	private final ExceptionListener excListener = new ExceptionListener() {
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
	public void deny(String Chatroomid) throws JMSException {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(), MessageKind.chatterMsgDeny.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setStringProperty(MessageHeader.ChatroomID.toString(), Chatroomid);
		message.setStringProperty(MessageHeader.RefID.toString(), referenceID);
		requestProducer.send(chatServiceQ, message);
	}

	@Override
	public void requestParticipian(String chatterID) throws JMSException {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.chatterMsgRequestParticipation.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setStringProperty(MessageHeader.RefID.toString(), chatterID);
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
	public void acceptInvitation(String request) throws JMSException {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.chatterMsgAcceptInvitation.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setStringProperty(MessageHeader.RefID.toString(), request);
		message.setStringProperty(MessageHeader.ChatroomID.toString(), chatroomId);
		requestProducer.send(chatServiceQ, message);
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
	public void invite(String chatterID) throws JMSException {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.chatterMsgInvite.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setStringProperty(MessageHeader.RefID.toString(), chatterID);
		message.setStringProperty(MessageHeader.ChatroomID.toString(), chatroomId);
		requestProducer.send(chatServiceQ, message);

	}

	@Override
	public void reject(String chatterID) throws JMSException {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.chatterMsgReject.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setStringProperty(MessageHeader.RefID.toString(), chatterID);
		message.setStringProperty(MessageHeader.ChatroomID.toString(), chatroomId);
		requestProducer.send(chatServiceQ, message);

	}

	@Override
	public void accept(String chatterID) throws JMSException {
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(),
				MessageKind.chatterMsgAccept.toString());
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setStringProperty(MessageHeader.RefID.toString(), chatterID);
		message.setStringProperty(MessageHeader.ChatroomID.toString(), chatroomId);
		requestProducer.send(chatServiceQ, message);

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

	/**
	 * get instance of {@link ChatJmsAdapter}
	 * 
	 * @return instance of {@link ChatJmsAdapter}
	 */
	// public static ChatJmsAdapter getInstance() {
	// if (chatJmsAdapter == null) {
	// chatJmsAdapter = new ChatJmsAdapter();
	// }
	// return chatJmsAdapter;
	// }
	
	/**
	 * 
	 * @param state
	 */
	@Override
	public void setState(ChatClientState state) {
		this.state = state;
	}

	private TextMessage createMessage(Destination destination)
			throws JMSException {
		TextMessage textMessage = session.createTextMessage();
		// eine Message besitzt optional verschiedene Properties
		textMessage.setJMSReplyTo(reply);
		textMessage.setJMSDestination(destination);
		return textMessage;
	}

	/**
	 * get instance of {@link ChatJmsAdapter}
	 * 
	 * @return instance of {@link ChatJmsAdapter}
	 */
	// public static ChatJmsAdapter getInstance() {
	// if (chatJmsAdapter == null) {
	// chatJmsAdapter = new ChatJmsAdapter();
	// }
	// return chatJmsAdapter;
	// }
	
	/**
	 * method for an parameterless Msg
	 * 
	 * @param Msgkind
	 * @throws JMSException
	 */
	private void sendParameterLessSimpleRequest(String Msgkind)
			throws JMSException {
		// TODO commit
		TextMessage message = createMessage(chatServiceQ);
		message.setStringProperty(MessageHeader.MsgKind.toString(), Msgkind);
		message.setStringProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setStringProperty(MessageHeader.ChatroomID.toString(), chatroomId);
		message.setStringProperty(MessageHeader.RefID.toString(), referenceID);
		requestProducer.send(chatServiceQ, message);
	}

	/**
	 * read chatters out of a string
	 * 
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
	 * read chatrooms with his owners out of a string depreced for futur
	 * implementation
	 * 
	 * @param chatsAndChatters
	 */
	private void setChatsAndChatters(String chatsAndChatters) {
		this.chatsAndChatters = new ArrayList<ChatChatterRelationship>();
		Scanner scanner = new Scanner(chatsAndChatters);
		while (scanner.hasNextLine()) {
			String[] segs = scanner.nextLine().split(Pattern.quote(":"));
			if (segs.length > 1)
				this.chatsAndChatters.add(new ChatChatterRelationship(segs[0],
						segs[1]));
			else
				this.chatsAndChatters.add(new ChatChatterRelationship(segs[0],
						""));
		}
		scanner.close();
	}

	/**
	 * read chatrooms with his owners out of a string
	 * 
	 * @param chatsAndChatters
	 */
	private void setChatsWithOwner(String chatsAndChatters) {
		this.chatsWithOwners = new ArrayList<String>();
		Scanner scanner = new Scanner(chatsAndChatters);
		while (scanner.hasNextLine()) {
			String[] segs = scanner.nextLine().split(Pattern.quote(":"));
			this.chatsWithOwners.add(segs[0]);
		}
		scanner.close();
	}

	private final MessageListener msgListener = new MessageListener() {

		@Override
		public void onMessage(Message replyMessage) {
			System.out.println("Client: " + replyMessage.toString());
			try {
				if (replyMessage instanceof Message) {
					Message textMessage = replyMessage;
					// System.out.println("Client: "+textMessage.toString());

					String msgKind = textMessage
							.getStringProperty(MessageHeader.MsgKind.toString());
					referenceID = textMessage.getStringProperty(MessageHeader.RefID
							.toString());
					chatroomId = textMessage
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
									state.gotChatStarted(chatroomId);
								}
							});
						break;
					case clientNewChat:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotNewChat(referenceID, messageText);
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
									state.gotRequest(referenceID);
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
									state.gotRejected(referenceID);
								}
							});
						break;
					case clientRequestCancelled:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotRequestCancelled(referenceID);
								}
							});
						break;
					case clientInvitation:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotInvite(referenceID, chatroomId);
								}
							});
						break;
					case clientAccepted:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotAccepted(referenceID);
								}
							});
						break;
					case clientDenied:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotDenied(referenceID);
								}
							});
						break;
					case clientParticipantEntered:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotParticipantEntered(referenceID);
								}
							});
						break;
					case clientParticipantLeft:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									state.gotParticipantLeft(referenceID);
								}
							});
						break;
					case clientAnswerChats:
						if (state != null)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {

									/**
									 * setChatsAndChatters(messageText); for
									 * future
									 */
									setChatsWithOwner(messageText);
									state.gotChats(chatsWithOwners);
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

	

}
