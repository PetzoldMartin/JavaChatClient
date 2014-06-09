package g13.message.logic;

import g13.gui.MainActivity;
import g13.message.ChatChatterRelationship;
import g13.message.interfaces.ChatServerMessageProducer;
import g13.message.interfaces.IBrokerConnection;
import g13.message.interfaces.IReceiveStompMessages;
import g13.message.interfaces.ISendStompMessages;
import g13.message.interfaces.StompCommunicationService;
import g13.state.ChatClientState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;








import de.fh_zwickau.android.base.architecture.BindServiceHelper;
import de.fh_zwickau.informatik.stompj.StompMessage;
import de.fh_zwickau.informatik.stompj.internal.MessageImpl;
import de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver;
import de.fh_zwickau.pti.mqgamecommon.MQConstantDefs;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;



public class ChatStompAdaptertwo implements ChatServerMessageProducer , IBrokerConnection,
IReceiveStompMessages{

	private ChatClientState state;
	private static final String LOGINQ = "/queue/" + MQConstantDefs.LOGINQ;
	
	private String authToken = "";
	private ISendStompMessages stompServiceBinder;

	private BindServiceHelper<ISendStompMessages, IReceiveStompMessages, MainActivity> serviceHelper;

	private ChatServerMessageReceiver messageReceiver;

	private String chatServiceQ;

	private String chatroomId, referenceID, messageText;
	private ArrayList<String> chatters, chatsWithOwners;

	private ArrayList<ChatChatterRelationship> chatsAndChatters;

	public String getCID() {
		return chatroomId;
	}

	public String getRefID() {
		return referenceID;
	}

	public ChatStompAdaptertwo() {
		
	}

	@Override
	public void connectToServer(String url, int port, String user, String pw) {
		if (stompServiceBinder != null)
			stompServiceBinder.connect(url, port, user, pw);

	}



	@Override
	public void register(String uname, String pword) {
		MessageImpl registerMessage = makeMessage(MessageKind.register);
		setUid(registerMessage, uname, pword);
		if (stompServiceBinder != null)
			stompServiceBinder.sendMessage(registerMessage, LOGINQ);
	}

	@Override
	public void login(String uname, String pword) {
		MessageImpl loginMessage = makeMessage(MessageKind.login);
		setUid(loginMessage, uname, pword);
		if (stompServiceBinder != null)
			stompServiceBinder.sendMessage(loginMessage, LOGINQ);
	}

	@Override
	public void logout() {
		MessageImpl logoutMessage = makeMessage(MessageKind.logout);
		if (stompServiceBinder != null)
			stompServiceBinder.sendMessage(logoutMessage, LOGINQ);
	}

	@Override
	public void deny(String Chatroomid) {
		MessageImpl message = makeMessage(MessageKind.chatterMsgDeny);
		message.setProperty(MessageHeader.MsgKind.toString(), MessageKind.chatterMsgDeny.toString());
		message.setProperty(MessageHeader.AuthToken.toString(), authToken);
		message.setProperty(MessageHeader.ChatroomID.toString(), Chatroomid);
		message.setProperty(MessageHeader.RefID.toString(), referenceID);
		stompServiceBinder.sendMessage(message, chatServiceQ);
	}

	@Override
	public void requestParticipian(String chatterID) {

	}

	@Override
	public void startChat() {

	}

	@Override
	public void cancel() {
	}

	@Override
	public void leave() {
	}

	@Override
	public void acceptInvitation(String request) {

	}

	@Override
	public void close() {

	}

	@Override
	public void chat(String messageText) {

	}

	@Override
	public void invite(String chatterID) {

	}

	@Override
	public void reject(String chatterID) {

	}

	@Override
	public void accept(String chatterID) {

	}

	@Override
	public void askForChats() {
		MessageImpl msg = makeMessage(MessageKind.chatterMsgChats);
		if (stompServiceBinder != null)
			stompServiceBinder.sendMessage(msg, chatServiceQ);
	}

	@Override
	public void askForChatters() {
		MessageImpl msg = makeMessage(MessageKind.chatterMsgChatters);
		if (stompServiceBinder != null)
			stompServiceBinder.sendMessage(msg, chatServiceQ);
	}

	
	/**
	 * 
	 * @param state
	 */
	@Override
	public void setState(ChatClientState state) {
		this.state = state;
	}



	
	
	/**
	 * method for an parameterless Msg
	 * 
	 * @param Msgkind
	 * @throws JMSException
	 */
	private void sendParameterLessSimpleRequest(String Msgkind)
 {
		// TODO commit

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

	@Override
	public void onServiceBound(ComponentName name) {
		if (messageReceiver instanceof IReceiveStompMessages) {
			((IReceiveStompMessages) messageReceiver).onServiceBound(name);
			if (serviceHelper != null) {
				serviceHelper.bindMessageHandler();
				stompServiceBinder = serviceHelper.service();
			}
		} else {
			Log.e("ChatStompAdapter.onServiceBound",
					"no receiver for onBound message");
		}
		
	}

	@Override
	public void onServiceUnbound(ComponentName name) {
		stompServiceBinder = null;
		
	}

	@Override
	public void onStompMessage(Serializable message) {
		System.out.println("Client: " + message.toString());
		try {
			if (message instanceof StompMessage) {
				StompMessage textMessage = (StompMessage) message;
				// System.out.println("Client: "+textMessage.toString());

				String msgKind = textMessage
						.getProperty(MessageHeader.MsgKind.toString());
				referenceID = textMessage.getProperty(MessageHeader.RefID
						.toString());
				chatroomId = textMessage
						.getProperty(MessageHeader.ChatroomID
								.toString());
				MessageKind messageKind = MessageKind.valueOf(msgKind);

				// get Text if we had Some
				if (message instanceof StompMessage) {
					StompMessage messageIn = (StompMessage) message;
					messageText = messageIn.getContentAsString();
				}
				switch (messageKind) {
				case authenticated:
					authToken = textMessage
							.getProperty(MessageHeader.AuthToken
									.toString());
					chatServiceQ = textMessage.getProperty("reply-to");
					if (state != null)
						{	state.gotSucess();	
						};
					break;
				case failed:
					if (state != null)
					{		state.gotFail();	
						};
					break;
				case loggedOut:
					if (state != null)
						 {	
								state.gotLogout();	
						};
					break;
				case clientChatStarted:
					if (state != null)
						{
								state.gotChatStarted(chatroomId);	
						};
					break;
				case clientNewChat:
					if (state != null)
						{
								state.gotNewChat(referenceID, messageText);	
						};
					break;
				case clientChatClosed:
					if (state != null)
						{
								state.gotChatClosed();	
						};
					break;
				case clientRequest:
					if (state != null)
						{
								state.gotRequest(referenceID);	
						};
					break;
				case clientParticipating:
					if (state != null)
						{
								state.gotParticipating();	
						};
					break;
				case clientRejected:
					if (state != null)
						{
								state.gotRejected(referenceID);	
						};
					break;
				case clientRequestCancelled:
					if (state != null)
						{
								state.gotRequestCancelled(referenceID);	
						};
					break;
				case clientInvitation:
					if (state != null)
						{
								state.gotInvite(referenceID, chatroomId);	
						};
					break;
				case clientAccepted:
					if (state != null)
						{
								state.gotAccepted(referenceID);	
						};
					break;
				case clientDenied:
					if (state != null)
						{
								state.gotDenied(referenceID);
						};
					break;
				case clientParticipantEntered:
					if (state != null)
						{
								state.gotParticipantEntered(referenceID);	
						};
					break;
				case clientParticipantLeft:
					if (state != null)
						{
								state.gotParticipantLeft(referenceID);			
						};
					break;
				case clientAnswerChats:
					if (state != null)
						{
								/**
								 * setChatsAndChatters(messageText); for
								 * future
								 */
								setChatsWithOwner(messageText);
								state.gotChats(chatsWithOwners);
						};
					break;
				case clientAnswerChatters:
					if (state != null)
						{
								setChatters(messageText);
								state.gotChatters(chatters);	
						};
					break;
				default:
					System.out.println(" Kind: " + messageKind.toString()
							+ " Text: " + messageText);
					break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onConnection(boolean success) {
		if (messageReceiver instanceof IReceiveStompMessages) {
			((IReceiveStompMessages) messageReceiver).onConnection(success);
		} else {
			Log.e("ChatStompAdapter.onConnection",
					"no receiver for connect message");
		}
		
	}

	@Override
	public void onError(String error) {
		if (messageReceiver instanceof IReceiveStompMessages) {
			((IReceiveStompMessages) messageReceiver).onError(error);
		} else {
			Log.e("ChatStompAdapter.onError", "no receiver for error message: "
					+ error);
		}
		
	}

	

	@Override
	public void disconnect() {
		if (stompServiceBinder != null)
			stompServiceBinder.disconnect();
		
	}

	@Override
	public boolean isConnected() {
		if (stompServiceBinder != null)
			return stompServiceBinder.isConnected();
		else
			return false;
	}

	private void setUid(MessageImpl message, String uname, String pword) {
		message.setProperty(MessageHeader.LoginUser.toString(), uname);
		message.setProperty(MessageHeader.LoginPassword.toString(), pword);
		message.setProperty(MessageHeader.ChatterNickname.toString(), uname);
	}

	private MessageImpl makeMessage(MessageKind kind) {
		MessageImpl msg = new MessageImpl();
		// message properties werden als HashMap übergeben
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("reply-to", StompCommunicationService.STOMP_REPLY);
		props.put(MessageHeader.MsgKind.toString(), kind.toString());
		props.put(MessageHeader.AuthToken.toString(), authToken);
		msg.setProperties(props);
		msg.setContent("");
		return msg;
	}



	@Override
	public void connect(String url, int port, String user, String pw) {
		if (stompServiceBinder != null)
			stompServiceBinder.connect(url, port, user, pw);
		
	}

	public void setServiceHelper(
			BindServiceHelper<ISendStompMessages, IReceiveStompMessages, MainActivity> serviceHelper) {
		this.serviceHelper = serviceHelper;
	}

	@Override
	public void connectToServer(String brokerUri) {
		// TODO Auto-generated method stub
		
	}

	
}
