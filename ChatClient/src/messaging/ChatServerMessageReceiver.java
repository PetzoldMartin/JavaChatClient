/**
 * 
 */
package messaging;

import States.ChatClientState;

/**
 * interface defines methods for all messages that could be sent from chat
 * server to a chat client
 * 
 * @author georg beier
 * 
 */
public interface ChatServerMessageReceiver {
	/**
	 * login succeeded
	 */
	void gotSuccess();

	/**
	 * login failed
	 */
	void gotFail();

	/**
	 * session ended
	 */
	void gotLogout();

	void gotChatClosed();
	void gotInvite();
	void gotReject();
	void gotChatStarted();
	void gotParticipating();
	void gotNewChat();
	void gotParticipantEntered();
	void gotParticipantLeft();
	void gotRequestCancelled();
	void gotRequest();
	void gotDenied();
	void gotAccepted();

	void setState(ChatClientState chatClientState);
}
