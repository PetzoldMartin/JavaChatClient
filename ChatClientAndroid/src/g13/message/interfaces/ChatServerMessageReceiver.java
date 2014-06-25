/**
 * 
 */
package g13.message.interfaces;

import g13.state.ChatClientState;

import java.util.ArrayList;

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

	/**
	 * if a chat is closed this is called every time on every state
	 */
	void gotChatClosed();

	void gotInvite(String chatter, String chatID);

	/**
	 * a requested invitation was rejected from the target
	 * 
	 * @param chatterID
	 *            , the target
	 * @throws JMSException
	 */
	void gotRejected(String chatterID);

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

	void gotChatters(ArrayList<String> chatters);

	void debug(String debug);

	void error(String error);

	void gotConnectSuccess();

	void gotConnectFail();

	void setWaiting();

}
