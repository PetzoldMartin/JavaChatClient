package States;

import States.StatesClasses.NotLoggedIn;
import gui.ChatSwingClient;
import messaging.ChatJmsAdapter;
import messaging.ChatServerMessageProducer;
import messaging.ChatServerMessageReceiver;

/**
 * @author Peter 33141
 * aufgetretene fragen:
 * s.h. TODO
 */
public abstract class ChatClientState  {
	
	protected ChatJmsAdapter messageProducer;
	protected ChatSwingClient messageReceiver;
	
	public ChatClientState(ChatJmsAdapter messageProducer, ChatSwingClient messageReceiver) {
		this.messageProducer=messageProducer;
		this.messageReceiver=messageReceiver;
		
	}
	/**
	 * Try to execute a Registration on the current State 
	 * @param username
	 * @param passwort
	 */
	public void onRegister(String username , String passwort) {
		System.err.println("unexpected event");
	};

	/**
	 * Try to execute a LogIn on the current State 
	 * @param username
	 * @param passwort
	 */
	public void onLogin(String username , String passwort) {
		
		System.err.println("unexpected event");
	};
	/**
	 * LogOut this user
	 */
	public void onLogout() {
		System.err.println("unexpected event:onLogout");
	};


	/**
	 * make a request to chat with the given participant
	 * @param theParticipant
	 */
	public void onRequest(String theParticipant) {
		System.err.println("unexpected event");
	};

	/**
	 * Start to Chat, waiting for a ChatRoom
	 */
	public void onStartChat() {
		System.err.println("unexpected event");
	};

	/**
	 * deny a chat request
	 * @param request
	 */
	public void onDeny(String request) {
		System.err.println("unexpected event");
	};

	/**
	 * Abbort a request, leave state "Waiting"
	 * @param request
	 */
	public void onCancel(String request) {
		System.err.println("unexpected event");
	};

	/**
	 * Leave and close the owned chatroom, go to state loggedIn 
	 */
	public void onChatClose() {
		System.err.println("unexpected event");
	};

	/**
	 * Leave the foreign chatroom,  go to state loggedIn
	 */
	public void onLeave() {
		System.err.println("unexpected event");
	};

	/**
	 * Accept a request, go to state in "other chat" a substate of "chatting" 
	 * @param request
	 */
	public void onAcceptInvitation(String request) {
		System.err.println("unexpected event");
	};

	/**
	 * send a textMessage to the Server, while staying in state 'chatting' 
	 * @param textMessage
	 */
	public void onChat(String textMessage) {
		System.err.println("unexpected event");
	};

	/**
	 * invite a given username in the owned chatroom, make a request to chat
	 * @param username
	 */
	public void onInvite(String username) {
		System.err.println("unexpected event");
	};

	/**
	 * accept an invitation from a username, go from state waiting to chatting
	 */
	public void onAccept() {
		System.err.println("unexpected event");
	};

	/**
	 * reject an invitation from a given username,go from state "waiting" to "chatting"
	 * @param username
	 */
	public void onReject(String username) {
		System.err.println("unexpected event");
	};

	/**
	 * logIn / register request got a fail from Server, stay in "notloggedIn" 
	 */
	public void gotFail() {
		System.err.println("unexpected event");
	};

	/**
	 * the current chatroom was closed, go to state "requesting" in "waiting"
	 * @param chatRoomName
	 */
	public void gotChatClosed() {
		System.err.println("unexpected event");
	};

	/**
	 * got invitation from??? TODO kein parameter im UML 
	 * spezifiziert, vermute username, sonst macht es keinen sinn
	 * will ja wissen wer mich einlädt
	 * @param username
	 */
	public void gotInvite(String username) {
		System.err.println("unexpected event");
	};

	/**
	 * positive signal from server, the user is now logged in and if not done before, registered
	 * switch state "notloggedIn" to "loggedIn"
	 */
	public void gotSucess() {
		System.err.println("unexpected event");
	};

	/**
	 * the given invitation was rejected from the target username
	 * TODO gibt es mehrfache invitations / chatrooms , kann ich mehrfache rooms besitzen ?
	 * kann ich mehrfach eingeladen werden ?
	 * switch
	 * @param the rejected username
	 */
	public void gotReject(String username) {
		System.err.println("unexpected event");
	};

	/**
	 * the owned chatRoom is now open, switch from state "waitingForChat" to "inOwnedChat"
	 */
	public void gotChatStarted() {
		System.err.println("unexpected event");
	};

	/**
	 * a request was accepted
	 * switch from "requesting" state to in "otherChat"
	 */
	public void gotParticipating() {
		System.err.println("unexpected event");
	};

	/**
	 * a new chat, the chatRoom, appeared
	 * @param chatRoom
	 */
	public void gotNewChat(String chatRoom) {
		System.err.println("unexpected event");
	};


	/**
	 * TODO entweder der chatraum hat ne liste der mitglieder,
	 * ODER die clients haben die infos über jeden teilgenommenen chat
	 * was zur folge hat das hier auch ein array vom server kommen könnte....
	 * do not switch states
	 * @param theNewPartizipant
	 */
	public void gotParticipantEntered(String theNewPartizipant) {
		System.err.println("unexpected event");
	};

	/**
	 * TODO entweder der chatraum hat ne liste der mitglieder,
	 * ODER die clients haben die infos über jeden teilgenommenen chat
	 * was zur folge hat das hier auch ein array vom server kommen könnte....
	 * sonst hab ich ständig solche anfragen.
	 * do not switch states
	 * @param leftPartizipant
	 */
	public void gotParticipantLeft(String leftPartizipant) {
		System.err.println("unexpected event");
	};

	/**
	 * TODO , ist das gleich wie request deny ?
	 * 
	 */
	public void gotRequestCancelled() {
		System.err.println("unexpected event");
	};

	/**
	 * return from invited to loggedIn, cause a invitation was denied
	 */
	public void gotDenied() {
		System.err.println("unexpected event");
	};

	/**
	 * a invite was accepted, TODO hier muss man entscheiden, im 
	 * chatting state bekommt man einen neuen participant hinzu
	 * im waiting state wird ein chatraum geöffnent .. oder existiert
	 * der chatraum schon auch ohne das mehr als 1 anwesend sind ? 
	 */
	public void gotAccepted() {
		System.err.println("unexpected event");
	}
	public void gotLogout() {
		System.err.println("unexpected event:gotLogout");
		
	};
	protected void changeState(ChatClientState cCS){
		messageProducer.setState(cCS);
		messageReceiver.setState(cCS);
	}
}
