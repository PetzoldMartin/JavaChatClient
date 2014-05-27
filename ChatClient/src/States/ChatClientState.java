package States;

import java.util.ArrayList;

import messaging.interfaces.ChatServerMessageProducer;
import messaging.interfaces.ChatServerMessageReceiver;

/**
 * @author Peter 33141 aufgetretene fragen: s.h. TODO
 */
public abstract class ChatClientState {

	protected ChatServerMessageProducer messageProducer;
	protected ChatServerMessageReceiver messageReceiver;

	public ChatClientState(ChatClientState oldState) {
		this(oldState.messageProducer, oldState.messageReceiver);
	}

	protected ChatClientState(ChatServerMessageProducer messageProducer,
			ChatServerMessageReceiver messageReceiver) {
		this.messageProducer = messageProducer;
		this.messageReceiver = messageReceiver;
		messageReceiver.setState(this);
		messageProducer.setState(this);
		System.out.println("change state to " + getName());
	}

	public ChatServerMessageProducer getProducer() {
		return messageProducer;
	}

	public ChatServerMessageReceiver getReceiver() {
		return messageReceiver;
	}

	public String getName() {
		return this.getClass().getName();
	}

	/**
	 * Try to execute a Registration on the current State
	 * 
	 * @param username
	 * @param passwort
	 */
	public void onRegister(String username, String passwort) {
		unexpectedEvent();
	};

	/**
	 * Try to execute a LogIn on the current State
	 * 
	 * @param username
	 * @param passwort
	 */
	public void onLogin(String username, String passwort) {
		unexpectedEvent();
	};

	/**
	 * LogOut this user
	 */
	public void onLogout() {
		unexpectedEvent();
	};

	/**
	 * make a request to chat with the given participant
	 * 
	 * @param theParticipant
	 */
	public void onRequest(String theParticipant) {
		unexpectedEvent();
	};

	/**
	 * Start to Chat, waiting for a ChatRoom
	 */
	public void onStartChat() {
		unexpectedEvent();
	};

	/**
	 * deny a chat request
	 * 
	 * @param request
	 */
	public void onDeny(String request) {
		unexpectedEvent();
	};

	/**
	 * Abbort a request, leave state "Waiting"
	 * 
	 * @param request
	 */
	public void onCancel() {
		unexpectedEvent();
	};

	/**
	 * Leave and close the owned chatroom, go to state loggedIn
	 */
	public void onChatClose() {
		unexpectedEvent();
	};

	/**
	 * Leave the foreign chatroom, go to state loggedIn
	 */
	public void onLeave() {
		unexpectedEvent();
	};

	/**
	 * accept an invitation from a username, go from state waiting to chatting
	 * 
	 * @param request
	 */
	public void onAcceptInvitation(String request) {
		unexpectedEvent();
	};

	/**
	 * send a textMessage to the Server, while staying in state 'chatting'
	 * 
	 * @param textMessage
	 */
	public void onChat(String textMessage) {
		unexpectedEvent();
	};

	/**
	 * invite a given username in the owned chatroom, make a request to chat
	 * 
	 * @param username
	 */
	public void onInvite(String username) {
		unexpectedEvent();
	};

	/**
	 * Accept a request, go to state in "other chat" a substate of "chatting"
	 * 
	 */
	public void onAccept(String chatterID) {
		unexpectedEvent();
	};

	/**
	 * reject an invitation from a given username,go from state "waiting" to
	 * "chatting"
	 * 
	 * @param username
	 */
	public void onReject(String username) {
		unexpectedEvent();
	};

	public void onAskForChats() {
		unexpectedEvent();
	};

	public void onAskForChatters() {
		unexpectedEvent();
	}

	/**
	 * logIn / register request got a fail from Server, stay in "notloggedIn"
	 */
	public void gotFail() {
		unexpectedEvent();
	};

	/**
	 * the current chatroom was closed, go to state "requesting" in "waiting"
	 * 
	 * @param chatRoomName
	 */
	public void gotChatClosed() {
		unexpectedEvent();
	};

	/**
	 * got invitation from??? TODO kein parameter im UML spezifiziert, vermute
	 * username, sonst macht es keinen sinn will ja wissen wer mich einl�dt
	 * 
	 * @param username
	 * @param cID
	 */
	public void gotInvite(String CNN, String cID) {
		unexpectedEvent();
	};

	/**
	 * positive signal from server, the user is now logged in and if not done
	 * before, registered switch state "notloggedIn" to "loggedIn"
	 */
	public void gotSucess() {
		unexpectedEvent();
	};

	/**
	 * the given invitation was rejected from the target username
	 * 
	 * @param username
	 *            , who reject the invitation
	 */
	public void gotRejected(String username) {
		unexpectedEvent();
	};

	/**
	 * the owned chatRoom is now open, switch from state "waitingForChat" to
	 * "inOwnedChat"
	 * 
	 * @param cID
	 */
	public void gotChatStarted(String cID) {
		unexpectedEvent();
	};

	/**
	 * a request was accepted switch from "requesting" state to in "otherChat"
	 */
	public void gotParticipating() {
		unexpectedEvent();
	}

	/**
	 * a new chat, the chatRoom, appeared
	 * 
	 * @param chatRoom
	 * @param messageText
	 */
	public void gotNewChat(String Chatter, String messageText) {
		unexpectedEvent();
	};

	/**
	 * a new Participant entered the chattroom.
	 * 
	 * @param chatterID
	 *            , the new participants ID
	 */
	public void gotParticipantEntered(String chatterID) {
		unexpectedEvent();
	};

	/**
	 * a participant leave the chatroom.
	 * 
	 * @param chatterID
	 *            , the participant who left
	 */
	public void gotParticipantLeft(String chatterID) {
		unexpectedEvent();
	};

	/**
	 * TODO , ist das gleich wie request deny ?
	 * 
	 * @param CNN
	 * 
	 */
	public void gotRequestCancelled(String CNN) {
		unexpectedEvent();
	};

	/**
	 * return from invited to loggedIn, cause a invitation was denied
	 * 
	 * @param CNN
	 */
	public void gotDenied(String CNN) {
		unexpectedEvent();
	};

	/**
	 * a invite was accepted, TODO hier muss man entscheiden, im chatting state
	 * bekommt man einen neuen participant hinzu im waiting state wird ein
	 * chatraum ge�ffnent .. oder existiert der chatraum schon auch ohne das
	 * mehr als 1 anwesend sind ?
	 * 
	 * @param CNN
	 */
	public void gotAccepted(String CNN) {
		unexpectedEvent();
	}

	public void gotLogout() {
		unexpectedEvent();
	};

	protected void unexpectedEvent() {
		Exception exp = new Exception();
		System.err.println("unexpected event: " + getName() + "->"
				+ (exp.getStackTrace())[1].getMethodName());
	}

	protected void handleExpeption(Exception e) {
		e.printStackTrace();
	}

	public void gotRequest(String CNN) {
		unexpectedEvent();
	}

	public void gotRejected() {
		unexpectedEvent();
	}

	public void gotChatters(ArrayList<String> chatters) {
		unexpectedEvent();
	}

	/**
	 * 
	 * @param chatsWithOwners
	 */

	public void gotChats(ArrayList<String> chatsWithOwners) {
		unexpectedEvent();
	}

	public void OnAskForChatters() {
		unexpectedEvent();
	}
}
