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
	
	void deny() throws JMSException;
	void startChat() throws JMSException;
	void cancel() throws JMSException;
	void leave() throws JMSException;
	void acceptInvitation() throws JMSException;
	void close() throws JMSException;
	void chat(String messageText) throws JMSException;
	void reject() throws JMSException;
	void accept() throws JMSException;


	void connectToServer(String brokerUri);

	void askForChats() throws JMSException;

	void requestParticipian(String cID, String refID) throws JMSException;

	void askForChatters() throws JMSException;

	void invite(String CNN) throws JMSException;
}
