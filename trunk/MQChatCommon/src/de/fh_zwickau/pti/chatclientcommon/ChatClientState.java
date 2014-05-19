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

	// events from gui
	public void onRegister() {
		System.err.println("unexpected event");
	}

	public void onLogin() {
		System.err.println("unexpected event");
	}

	public void onlogout() {
		System.err.println("unexpected event");
	}

	// events from server
	/**
	 * login succeeded
	 */
	public void gotSuccess() {
		System.err.println("unexpected event");
	}

	/**
	 * login failed
	 */
	public void gotFail() {
		System.err.println("unexpected event");
	}

	/**
	 * session ended
	 */
	public void gotLogout() {
		System.err.println("unexpected event");
	}

}
