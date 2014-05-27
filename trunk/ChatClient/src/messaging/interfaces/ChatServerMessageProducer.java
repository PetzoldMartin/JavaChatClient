/**
 * 
 */
package messaging.interfaces;

import javax.jms.JMSException;

import States.ChatClientState;

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
	 * 
	 * @throws Exception
	 */
	void logout() throws Exception;

	// /**
	// * @param chatSwingClient
	// */
	// void setMessageReceiver(ChatServerMessageReceiver messageReceiver);

	/**
	 * deny an request
	 * 
	 * @throws JMSException
	 */
	void deny() throws JMSException;

	/**
	 * start a new chat
	 * 
	 * @throws JMSException
	 */
	void startChat() throws JMSException;

	/**
	 * cancel an own Request
	 * 
	 * @throws JMSException
	 */
	void cancel() throws JMSException;

	/**
	 * leave an Chat
	 * 
	 * @throws JMSException
	 */
	void leave() throws JMSException;

	/**
	 * accept an Invitation
	 * 
	 * @param request
	 * 
	 * @throws JMSException
	 */
	void acceptInvitation(String request) throws JMSException;

	/**
	 * close a chat you own
	 * 
	 * @throws JMSException
	 */
	void close() throws JMSException;

	/**
	 * send achat message
	 * 
	 * @param messageText
	 * @throws JMSException
	 */
	void chat(String messageText) throws JMSException;

	/**
	 * reject an Invitation
	 * 
	 * @param chatterID
	 * 
	 * @throws JMSException
	 */
	void reject(String chatterID) throws JMSException;

	/**
	 * accept an request
	 * 
	 * @param chatterID
	 * 
	 * @throws JMSException
	 */
	void accept(String chatterID) throws JMSException;

	/**
	 * 
	 * @param brokerUri
	 */
	void connectToServer(String brokerUri);

	/**
	 * ask for available chats
	 * 
	 * @throws JMSException
	 */
	void askForChats() throws JMSException;

	/**
	 * ask for available chatters not in chat
	 * 
	 * @throws JMSException
	 */
	void askForChatters() throws JMSException;

	/**
	 * invite a chatter
	 * 
	 * @param CNN
	 * @throws JMSException
	 */
	void invite(String CNN) throws JMSException;

	/**
	 * request for a Chatroom request
	 * 
	 * @param cID
	 * @throws JMSException
	 */
	void requestParticipian(String chatterID) throws JMSException;

	void setState(ChatClientState chatClientState);

}
