/**
 * 
 */
package de.fh_zwickau.pti.chatclientcommon;

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
	 * got chatters
	 */
	public void gotChatters(String[] chatters);

	/**
	 * got chatrooms
	 */
	public void gotChatrooms(String[] chatrooms);


}
