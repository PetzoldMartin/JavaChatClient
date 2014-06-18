/**
 * 
 */
package g13.message.interfaces;



import g13.state.ChatClientState;

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
	 * @throws Exception
	 */
	void login(String uname, String pword) throws Exception;

	/**
	 * logout from server
	 * 
	 * @throws Exception
	 */
	void logout() throws Exception;


	/**
	 * deny an request
	 * @param chatroomid 
	 * 
	 * @throws Exception
	 */
	void deny(String chatroomid) throws Exception;

	/**
	 * start a new chat
	 * 
	 * @throws Exception
	 */
	void startChat() throws Exception;

	/**
	 * cancel an own Request
	 * 
	 * @throws Exception
	 */
	void cancel() throws Exception;

	/**
	 * leave an Chat
	 * 
	 * @throws Exception
	 */
	void leave() throws Exception;

	/**
	 * accept an Invitation
	 * 
	 * @param request
	 * 
	 * @throws Exception
	 */
	void acceptInvitation(String request) throws Exception;

	/**
	 * close a chat you own
	 * 
	 * @throws Exception
	 */
	void close() throws Exception;

	/**
	 * send achat message
	 * 
	 * @param messageText
	 * @throws Exception
	 */
	void chat(String messageText) throws Exception;

	/**
	 * reject an Invitation
	 * 
	 * @param chatterID
	 * 
	 * @throws Exception
	 */
	void reject(String chatterID) throws Exception;

	/**
	 * accept an request
	 * 
	 * @param chatterID
	 * 
	 * @throws Exception
	 */
	void accept(String chatterID) throws Exception;

	/**
	 * 
	 * @param brokerUri
	 */
	void connectToServer(String brokerUri);

	/**
	 * ask for available chats
	 * 
	 * @throws Exception
	 */
	void askForChats() throws Exception;

	/**
	 * ask for available chatters not in chat
	 * 
	 * @throws Exception
	 */
	void askForChatters() throws Exception;

	/**
	 * invite a chatter
	 * 
	 * @param CNN
	 * @throws Exception
	 */
	void invite(String CNN) throws Exception;

	/**
	 * request for a Chatroom request
	 * 
	 * @param cID
	 * @throws Exception
	 */
	void requestParticipian(String chatterID) throws Exception;

	/**
	 * Sets the Aktuall state of the chatter to the Message Producer
	 * 
	 * @param chatClientState
	 */
	void setState(ChatClientState chatClientState);

	/**
	 * Connecting to server with url, port, username and password
	 * 
	 * @param url
	 * @param port
	 * @param user
	 * @param pw
	 */
	void connectToServer(String url, int port, String user, String pw);

	/**
	 * set the message receiver for this Producer
	 * 
	 * @param msgReceiver
	 */
	void setMessageReceiver(ChatServerMessageReceiver msgReceiver);

}
