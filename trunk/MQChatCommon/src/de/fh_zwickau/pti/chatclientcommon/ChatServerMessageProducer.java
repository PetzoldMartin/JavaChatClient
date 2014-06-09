/**
 * 
 */
package de.fh_zwickau.pti.chatclientcommon;


/**
 * interface defines methodes for each possible message that could be sent to
 * the chat server
 * 
 * @author georg beier
 * 
 */
public interface ChatServerMessageProducer {
	/**
	 * register at the server
	 * 
	 * @param uname
	 *            user nickname
	 * @param pword
	 *            user password
	 * @throws Exception 
	 */
	void register(String uname, String pword) throws Exception;

	/**
	 * login at the server
	 * 
	 * @param uname
	 *            user nickname
	 * @param pword
	 *            user password
	 * @throws JMSException 
	 */
	void login(String uname, String pword) throws Exception;

	/**
	 * logout from server
	 * @throws Exception 
	 */
	void logout() throws Exception;
	
	/**
	 * request a list of all idle chatters
	 */
	void getChatters();
	
	/**
	 * request a list of all active chatrooms
	 */
	void getChatrooms();

	/**
	 * @param chatSwingClient
	 */
	void setMessageReceiver(ChatServerMessageReceiver messageReceiver);
}
