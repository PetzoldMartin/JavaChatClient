/**
 * 
 */
package messaging;

import javax.jms.JMSException;


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
	 * @param chatSwingClient
	 */
	void setMessageReceiver(ChatServerMessageReceiver messageReceiver);
	
	void deny();
	void startChat() throws JMSException;
	void cancel();
	void leave();
	void acceptInvitation();
	void close() throws JMSException;
	void chat(String messageText) throws JMSException;
	void invite();
	void reject();
	void accept();

	void requestParticipian(String ParticipianName) throws JMSException;
}
