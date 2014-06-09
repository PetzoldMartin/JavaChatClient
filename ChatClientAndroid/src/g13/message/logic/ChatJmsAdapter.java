package g13.message.logic;

import g13.message.ChatChatterRelationship;
import g13.message.interfaces.ChatServerMessageProducer;
import g13.state.ChatClientState;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.jms.JMSException;

public class ChatJmsAdapter implements ChatServerMessageProducer {

	private ChatClientState state;

	private final String authToken = "";
	// private Destination chatServiceQ;
	// private Destination loginQ, reply;
	// private Session session;
	// private MessageProducer requestProducer;

	private String chatroomId, referenceID, messageText;
	private ArrayList<String> chatters, chatsWithOwners;

	private ArrayList<ChatChatterRelationship> chatsAndChatters;

	public String getCID() {
		return chatroomId;
	}

	public String getRefID() {
		return referenceID;
	}

	public ChatJmsAdapter() {

	}

	@Override
	public void connectToServer(String brokerUri) {


	}



	@Override
	public void register(String uname, String pword) {

	}

	@Override
	public void login(String uname, String pword) {

	}

	@Override
	public void logout() {

	}

	@Override
	public void deny(String Chatroomid) {

	}

	@Override
	public void requestParticipian(String chatterID) {

	}

	@Override
	public void startChat() {

	}

	@Override
	public void cancel() {
	}

	@Override
	public void leave() {
	}

	@Override
	public void acceptInvitation(String request) {

	}

	@Override
	public void close() {

	}

	@Override
	public void chat(String messageText) {

	}

	@Override
	public void invite(String chatterID) {

	}

	@Override
	public void reject(String chatterID) {

	}

	@Override
	public void accept(String chatterID) {

	}

	@Override
	public void askForChats() {

	}

	@Override
	public void askForChatters() {

	}

	/**
	 * get instance of {@link ChatJmsAdapter}
	 * 
	 * @return instance of {@link ChatJmsAdapter}
	 */
	// public static ChatJmsAdapter getInstance() {
	// if (chatJmsAdapter == null) {
	// chatJmsAdapter = new ChatJmsAdapter();
	// }
	// return chatJmsAdapter;
	// }
	
	/**
	 * 
	 * @param state
	 */
	@Override
	public void setState(ChatClientState state) {
		this.state = state;
	}



	/**
	 * get instance of {@link ChatJmsAdapter}
	 * 
	 * @return instance of {@link ChatJmsAdapter}
	 */
	// public static ChatJmsAdapter getInstance() {
	// if (chatJmsAdapter == null) {
	// chatJmsAdapter = new ChatJmsAdapter();
	// }
	// return chatJmsAdapter;
	// }
	
	/**
	 * method for an parameterless Msg
	 * 
	 * @param Msgkind
	 * @throws JMSException
	 */
	private void sendParameterLessSimpleRequest(String Msgkind)
 {
		// TODO commit

	}

	/**
	 * read chatters out of a string
	 * 
	 * @param chatters
	 */
	private void setChatters(String chatters) {
		this.chatters = new ArrayList<String>();
		Scanner scanner = new Scanner(chatters);
		while (scanner.hasNextLine())
			this.chatters.add(scanner.nextLine());
		scanner.close();
	
	}

	/**
	 * read chatrooms with his owners out of a string depreced for futur
	 * implementation
	 * 
	 * @param chatsAndChatters
	 */
	private void setChatsAndChatters(String chatsAndChatters) {
		this.chatsAndChatters = new ArrayList<ChatChatterRelationship>();
		Scanner scanner = new Scanner(chatsAndChatters);
		while (scanner.hasNextLine()) {
			String[] segs = scanner.nextLine().split(Pattern.quote(":"));
			if (segs.length > 1)
				this.chatsAndChatters.add(new ChatChatterRelationship(segs[0],
						segs[1]));
			else
				this.chatsAndChatters.add(new ChatChatterRelationship(segs[0],
						""));
		}
		scanner.close();
	}

	/**
	 * read chatrooms with his owners out of a string
	 * 
	 * @param chatsAndChatters
	 */
	private void setChatsWithOwner(String chatsAndChatters) {
		this.chatsWithOwners = new ArrayList<String>();
		Scanner scanner = new Scanner(chatsAndChatters);
		while (scanner.hasNextLine()) {
			String[] segs = scanner.nextLine().split(Pattern.quote(":"));
			this.chatsWithOwners.add(segs[0]);
		}
		scanner.close();
	}



	

}
