/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice;

import javax.jms.Message;

import de.fh_zwickau.pti.jms.userservice.chat.ChatterState;

/**
 * Base class for Chatter Mock objects that can be used in test cases
 * 
 * @author georg beier
 * 
 */
public class ChatterMock {

	private ChatterState state;
	private String ownedChat;
	private String chat;

	/**
	 * handle all incoming jms messages by delegating message to appropriate
	 * method of current instance of state
	 * 
	 * @param message
	 *            a jms message
	 */
	public boolean processMessage(Message message) {
		return state.processMessage(message);
	}

	public void setState(ChatterState state) {
		this.state = state;
	}

	public String getOwnedChat() {
		return ownedChat;
	}

	public void setOwnedChat(String ownedChat) {
		this.ownedChat = ownedChat;
	}

	public String getChat() {
		return chat;
	}

	public void setChat(String chat) {
		this.chat = chat;
	}
}
