/**
 * 
 */
package de.fh_zwickau.pti.mqchatandroidclient;

import java.io.Serializable;
import java.util.HashMap;

import android.content.ComponentName;
import android.util.Log;
import de.fh_zwickau.android.base.architecture.BindServiceHelper;
import de.fh_zwickau.informatik.stompj.StompMessage;
import de.fh_zwickau.informatik.stompj.internal.MessageImpl;
import de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer;
import de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver;
import de.fh_zwickau.pti.mqgamecommon.MQConstantDefs;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * Implements methods for all messages that shall be sent to the chat server.
 * For that, appropriate stomp messages are created and sent using the Binder
 * interface of theStompCommunicationService.<br>
 * Also, incoming Stomp messages are received and decoded and the appropriate
 * methods of a ChatServerMessageReceiver are called.
 * 
 * @author georg beier
 * 
 */
public class ChatStompAdapter implements ChatServerMessageProducer, IBrokerConnection,
		IReceiveStompMessages {

	private static final String LOGINQ = "/queue/" + MQConstantDefs.LOGINQ;

	private String authToken = "";

	private ISendStompMessages stompServiceBinder;

	private BindServiceHelper<ISendStompMessages, IReceiveStompMessages, ChatActivity> serviceHelper;

	private ChatServerMessageReceiver messageReceiver;

	private String chatServiceQ;

	/**
	 * 
	 */
	public ChatStompAdapter() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer#login(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void login(String uname, String pword) throws Exception {
		MessageImpl loginMessage = makeMessage(MessageKind.login);
		setUid(loginMessage, uname, pword);
		if (stompServiceBinder != null)
			stompServiceBinder.sendMessage(loginMessage, LOGINQ);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer#register
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public void register(String uname, String pword) throws Exception {
		MessageImpl registerMessage = makeMessage(MessageKind.register);
		setUid(registerMessage, uname, pword);
		if (stompServiceBinder != null)
			stompServiceBinder.sendMessage(registerMessage, LOGINQ);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer#logout()
	 */
	@Override
	public void logout() throws Exception {
		MessageImpl logoutMessage = makeMessage(MessageKind.logout);
		if (stompServiceBinder != null)
			stompServiceBinder.sendMessage(logoutMessage, LOGINQ);
	}

	/* (non-Javadoc)
	 * @see de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer#getChatters()
	 */
	@Override
	public void getChatters() {
		MessageImpl msg = makeMessage(MessageKind.chatterMsgChatters);
		if (stompServiceBinder != null)
			stompServiceBinder.sendMessage(msg, chatServiceQ);
	}

	/* (non-Javadoc)
	 * @see de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer#getChatrooms()
	 */
	@Override
	public void getChatrooms() {
		MessageImpl msg = makeMessage(MessageKind.chatterMsgChats);
		if (stompServiceBinder != null)
			stompServiceBinder.sendMessage(msg, chatServiceQ);
	}

	/* (non-Javadoc)
	 * @see de.fh_zwickau.pti.mqchatandroidclient.IBrokerConnection#connect(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void connect(String url, int port, String user, String pw) {
		if (stompServiceBinder != null)
			stompServiceBinder.connect(url, port, user, pw);
	}

	/* (non-Javadoc)
	 * @see de.fh_zwickau.pti.mqchatandroidclient.IBrokerConnection#disconnect()
	 */
	@Override
	public void disconnect() {
		if (stompServiceBinder != null)
			stompServiceBinder.disconnect();
	}

	/* (non-Javadoc)
	 * @see de.fh_zwickau.pti.mqchatandroidclient.IBrokerConnection#isConnected()
	 */
	@Override
	public boolean isConnected() {
		if (stompServiceBinder != null)
			return stompServiceBinder.isConnected();
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer#
	 * setMessageReceiver
	 * (de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver)
	 */
	@Override
	public void setMessageReceiver(ChatServerMessageReceiver msgReceiver) {
		messageReceiver = msgReceiver;
	}

	public void setServiceHelper(
			BindServiceHelper<ISendStompMessages, IReceiveStompMessages, ChatActivity> serviceHelper) {
		this.serviceHelper = serviceHelper;
	}

	/**
	 * create Stomp message and initialize some properties
	 * 
	 * @param kind
	 *            meaning of this message
	 * @return partly initialized message
	 */
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

	/**
	 * set user identity into message properties
	 * 
	 * @param message
	 *            stomp message
	 * @param uname
	 *            user name
	 * @param pword
	 *            password
	 */
	private void setUid(MessageImpl message, String uname, String pword) {
		message.setProperty(MessageHeader.LoginUser.toString(), uname);
		message.setProperty(MessageHeader.LoginPassword.toString(), pword);
		message.setProperty(MessageHeader.ChatterNickname.toString(), uname);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.android.base.architecture.IBindingCallbacks#onServiceBound
	 * (android.content.ComponentName)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.android.base.architecture.IBindingCallbacks#onServiceUnbound
	 * (android.content.ComponentName)
	 */
	@Override
	public void onServiceUnbound(ComponentName name) {
		stompServiceBinder = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.mqchatandroidclient.IReceiveStompMessages#onStompMessage
	 * (java.io.Serializable)
	 */
	@Override
	public void onStompMessage(Serializable message) {
		if (message instanceof StompMessage) {
			StompMessage stompMessage = (StompMessage) message;
			String msgKind = stompMessage.getProperty(MessageHeader.MsgKind
					.toString());
			MessageKind messageKind = MessageKind.valueOf(msgKind);
			Log.v("Message trace", msgKind);
			switch (messageKind) {
			case authenticated:
				authToken = stompMessage
						.getProperty(MessageHeader.AuthToken
								.toString());
				chatServiceQ = stompMessage.getProperty("reply-to");
				if (messageReceiver != null)
					messageReceiver.gotSuccess();
				break;
			case failed:
				if (messageReceiver != null)
					messageReceiver.gotFail();
				break;
			case loggedOut:
				if (messageReceiver != null)
					messageReceiver.gotLogout();
				break;
			case clientAnswerChats:
				if (messageReceiver != null) {
					String[] s = stompMessage.getContentAsString().split("[\\n\\r]+");
					messageReceiver.gotChatrooms(s);
				}
				break;
			case clientAnswerChatters:
				if (messageReceiver != null) {
					String[] s = stompMessage.getContentAsString().split("[\\n\\r]+");
					messageReceiver.gotChatters(s);
				}
				break;
			default:
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.mqchatandroidclient.IReceiveStompMessages#onConnection
	 * (boolean)
	 */
	@Override
	public void onConnection(boolean success) {
		if (messageReceiver instanceof IReceiveStompMessages) {
			((IReceiveStompMessages) messageReceiver).onConnection(success);
		} else {
			Log.e("ChatStompAdapter.onConnection",
					"no receiver for connect message");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.mqchatandroidclient.IReceiveStompMessages#onError(java
	 * .lang.String)
	 */
	@Override
	public void onError(String error) {
		if (messageReceiver instanceof IReceiveStompMessages) {
			((IReceiveStompMessages) messageReceiver).onError(error);
		} else {
			Log.e("ChatStompAdapter.onError", "no receiver for error message: "
					+ error);
		}
	}

}
