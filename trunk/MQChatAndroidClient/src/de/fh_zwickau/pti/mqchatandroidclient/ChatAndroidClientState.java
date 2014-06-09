/**
 * 
 */
package de.fh_zwickau.pti.mqchatandroidclient;

import android.util.Log;
import de.fh_zwickau.pti.chatclientcommon.ChatClientState;

/**
 * @author georg beier
 * 
 */
public class ChatAndroidClientState extends ChatClientState {

	public ChatAndroidClientState(String n) {
		super(n);
	}

	public void onConnect() {
		logError("onConnect");
	}

	public void onDisconnect() {
		logError("onDisconnect");
	}

	public void connectSuccess() {
		logError("connectSuccess");
	}

	public void connectFailure() {
		logError("connectFailure");
	}
	
	public void enterState() {
		Log.v("State trace", "entering " + name);
	}

	public void exitState() {
	}

	public void serviceBound() {
	}

	@Override
	protected void logError(String... evt) {
		Log.e("ChatAndroidClientState", "unexpected event "
				+ (evt.length > 0 ? evt[0] : "")
				+ (name.length() > 0 ? " in state " + name : ""));
	}
}
