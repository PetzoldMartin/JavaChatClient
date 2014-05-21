package States;

import gui.ChatSwingClient;
import messaging.ChatJmsAdapter;

/**
 * @author Peter
 *
 */
/**
 * @author Peter
 *
 */
public class ChatClientState {
	
	protected ChatJmsAdapter chatJmsAdapter;
	protected ChatSwingClient chatSwingClient;
	
	public ChatClientState() {
		chatJmsAdapter = chatJmsAdapter.getInstance();
		chatSwingClient = chatSwingClient.getInstance();
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
		System.err.println("unexpected event");
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

	public void onCancel() {
		System.err.println("unexpected event");
	};

	public void onChatClose() {
		System.err.println("unexpected event");
	};

	public void onLeave() {
		System.err.println("unexpected event");
	};

	public void onAcceptInvitation() {
		System.err.println("unexpected event");
	};

	public void onChat() {
		System.err.println("unexpected event");
	};

	public void onInvite() {
		System.err.println("unexpected event");
	};

	public void onAccept() {
		System.err.println("unexpected event");
	};

	public void onReject() {
		System.err.println("unexpected event");
	};

	public void gotFail() {
		System.err.println("unexpected event");
	};

	public void gotChatClosed() {
		System.err.println("unexpected event");
	};

	public void gotInvite() {
		System.err.println("unexpected event");
	};

	public void gotSucess() {
		System.err.println("unexpected event");
	};

	public void gotReject() {
		System.err.println("unexpected event");
	};

	public void gotChatStarted() {
		System.err.println("unexpected event");
	};

	public void gotParticipating() {
		System.err.println("unexpected event");
	};

	public void gotNewChat() {
		System.err.println("unexpected event");
	};

	public void gotParticipantEntered() {
		System.err.println("unexpected event");
	};

	public void gotParticipantLeft() {
		System.err.println("unexpected event");
	};

	public void gotRequestCancelled() {
		System.err.println("unexpected event");
	};

	public void gotDenied() {
		System.err.println("unexpected event");
	};

	public void gotAccepted() {
		System.err.println("unexpected event");
	};
}
