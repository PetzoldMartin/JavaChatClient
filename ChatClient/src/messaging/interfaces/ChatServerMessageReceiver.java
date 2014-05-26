/**
 * 
 */
package messaging.interfaces;

import java.util.ArrayList;

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

	void gotInvite(String chatter, String chatID);

	void gotReject();

	void gotChatStarted(String chatID);

	void gotParticipating();

	void gotNewChat(String chatter, String messageText);

	void gotParticipantEntered(String chatterID);

	void gotParticipantLeft(String chatterID);

	void gotRequestCancelled(String chatterID);

	void gotRequest(String chatterID);

	void gotAccepted(String chatterID);

	void gotDenied(String chatterID);

	void gotChats(ArrayList<String> chatsWithOwner);

	void setState(ChatClientState chatClientState);
}
