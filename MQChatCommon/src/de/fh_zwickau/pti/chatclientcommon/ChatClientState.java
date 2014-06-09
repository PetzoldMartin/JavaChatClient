/**
 * 
 */
package de.fh_zwickau.pti.chatclientcommon;

/**
 * defines all methods that are implemented by ObjectForState classes
 * 
 * @author georg beier
 * 
 */
public abstract class ChatClientState {

	protected String name;

	public ChatClientState() {
		this("");
	}

	public ChatClientState(String stateName) {
		name = stateName;
	}

	// events from gui
	public void onRegister() {
		logError("onRegister");
	}

	public void onLogin() {
		logError("onLogin");
	}

	public void onLogout() {
		logError("onlogout");
	}

	public void onGetChatters() {
		logError("onGetChatters");
	}

	public void onGetChatrooms() {
		logError("onGetChatrooms");
	}

	// events from server
	/**
	 * login succeeded
	 */
	public void gotSuccess() {
		logError("gotSuccess");
	}

	/**
	 * login failed
	 */
	public void gotFail() {
		logError("gotFail");
	}
	
	/**
	 * session ended
	 */
	public void gotLogout() {
		logError("gotLogout");
	}

	/**
	 * got chatters
	 */
	public void gotChatters(String[] chatters) {
		logError("gotChatters");
	}

	/**
	 * got chatrooms
	 */
	public void gotChatrooms(String[] chatrooms) {
		logError("gotChatrooms");
	}

	protected void logError(String... evt) {
		System.err.println("unexpected event " + (evt.length > 0 ? evt[0] : "")
				+ (name.length() > 0 ? " in state " + name : ""));
	}

}
