/**
 * 
 */
package de.fh_zwickau.pti.mqchatandroidclient;

import java.io.Serializable;

import android.app.Activity;
import android.content.ComponentName;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.fh_zwickau.pti.chatclientcommon.ChatServerMessageProducer;
import de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver;
import de.fh_zwickau.pti.jms.chatclient.android.R;

/**
 * @author georg beier
 * 
 */
public class ClientStateManager implements IReceiveStompMessages,
		ChatServerMessageReceiver {

	private ChatAndroidClientState currentState;

	protected ChatServerMessageProducer messageProducer;

	private EditText urlEditText;
	private EditText portEditText;
	private EditText unameEditText;
	private EditText pwordEditText;
	private TextView outputText;
	private ToggleButton connectToggleButton;
	private Button loginButton;
	private Button logoutButton;
	private Button registerButton;
	private Button getChattersButton;
	private Button getChatroomsButton;

	/**
	 * initialize access to gui elements and state chart
	 */
	public ClientStateManager(Activity owner) {

		urlEditText = (EditText) owner.findViewById(R.id.urlEditText);
		portEditText = (EditText) owner.findViewById(R.id.portEditText);
		unameEditText = (EditText) owner.findViewById(R.id.unameText);
		pwordEditText = (EditText) owner.findViewById(R.id.pwordText);
		outputText = (TextView) owner.findViewById(R.id.outputText);
		connectToggleButton = (ToggleButton) owner
				.findViewById(R.id.connectToggleButton);
		loginButton = (Button) owner.findViewById(R.id.loginButton);
		logoutButton = (Button) owner.findViewById(R.id.logoutButton);
		registerButton = (Button) owner.findViewById(R.id.registerButton);
		getChattersButton = (Button) owner.findViewById(R.id.getChattersButton);
		getChatroomsButton = (Button) owner
				.findViewById(R.id.getChatroomsButton);
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v("Message trace", "onLogin");
				currentState.onLogin();
			}
		});
		logoutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v("Message trace", "onLogout");
				currentState.onLogout();
			}
		});
		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v("Message trace", "onRegister");
				currentState.onRegister();
			}
		});
		getChattersButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v("Message trace", "onGetChatters");
				currentState.onGetChatters();
			}
		});
		getChatroomsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v("Message trace", "onGetChatrooms");
				currentState.onGetChatrooms();
			}
		});
		connectToggleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(connectToggleButton.isChecked()) {
					Log.v("Message trace", "onConnect");
					currentState.onConnect();
				} else {
					Log.v("Message trace", "onDisconnect");
					currentState.onDisconnect();
				}
			}
		});
		// TODO set state from activities saved state
		setState(notBound);
	}

	/**
	 * setState must be called once after construction to initialize state
	 * chart!
	 * 
	 * @param newState
	 */
	private void setState(ChatAndroidClientState newState) {
		if (currentState != null)
			currentState.exitState();
		currentState = newState;
		if (currentState != null)
			currentState.enterState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver#gotSuccess()
	 */
	@Override
	public void gotSuccess() {
		currentState.gotSuccess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver#gotFail()
	 */
	@Override
	public void gotFail() {
		currentState.gotFail();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver#gotLogout()
	 */
	@Override
	public void gotLogout() {
		currentState.gotLogout();
	}

	/* (non-Javadoc)
	 * @see de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver#gotChatters()
	 */
	@Override
	public void gotChatters(String[] c) {
		currentState.gotChatters(c);
	}

	/* (non-Javadoc)
	 * @see de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver#gotChatrooms()
	 */
	@Override
	public void gotChatrooms(String[] c) {
		currentState.gotChatrooms(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.mqchatandroidclient.IReceiveStompMessages#onConnection
	 * (boolean)
	 */
	@Override
	public void onConnection(boolean success) {
		if (success)
			currentState.connectSuccess();
		else
			currentState.connectFailure();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.android.base.architecture.IBindingCallbacks#onServiceBound
	 * (android.content.ComponentName)
	 */
	@Override
	public void onServiceBound(ComponentName name) {
		currentState.serviceBound();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.android.base.architecture.IBindingCallbacks#onServiceUnbound
	 * (android.content.ComponentName)
	 */
	@Override
	public void onServiceUnbound(ComponentName name) {
		Log.e("ClientStateManager.onServiceUnbound", "should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.mqchatandroidclient.IReceiveStompMessages#onStompMessage
	 * (java.io.Serializable)
	 */
	@Override
	public void onStompMessage(Serializable message) {
		Log.e("ClientStateManager.onStompMessage", "should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fh_zwickau.pti.mqchatandroidclient.IReceiveStompMessages#onError(java
	 * .lang.String)
	 */
	@Override
	public void onError(String error) {
		outputText.setText(error);
	}

	public void setMessageProducer(ChatServerMessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}

	ChatAndroidClientState notBound = new ChatAndroidClientState(
			"notBound") {

		@Override
		public void enterState() {
			super.enterState();
			connectToggleButton.setChecked(false);
			connectToggleButton.setEnabled(false);
			loginButton.setEnabled(false);
			registerButton.setEnabled(false);
			logoutButton.setEnabled(false);
			getChatroomsButton.setEnabled(false);
			getChattersButton.setEnabled(false);
		}
		
		@Override
		public void serviceBound() {
			setState(notConnected);
		}
		
	};

	ChatAndroidClientState notConnected = new ChatAndroidClientState(
			"notConnected") {

		@Override
		public void enterState() {
			super.enterState();
			connectToggleButton.setEnabled(true);
		}

		@Override
		public void onConnect() {
			if (messageProducer instanceof IBrokerConnection) {
				IBrokerConnection msgSender = (IBrokerConnection) messageProducer;
				String url = urlEditText.getText().toString();
				int port = Integer.parseInt(portEditText.getText().toString());
				msgSender.connect(url, port, "sys", "man");
				setState(connecting);
			} else {
				Log.e("ClientStateManager", "cannot send  connect message");
			}
		}
	};

	ChatAndroidClientState connecting = new ChatAndroidClientState("connecting") {

		@Override
		public void enterState() {
			super.enterState();
			connectToggleButton.setEnabled(false);
		}

		@Override
		public void exitState() {
			connectToggleButton.setEnabled(true);
		}

		@Override
		public void connectSuccess() {
			setState(notLoggedIn);
		}

		@Override
		public void connectFailure() {
			setState(notConnected);
		}

	};

	ChatAndroidClientState notLoggedIn = new ConnectedState("notLoggedIn") {
	
		@Override
		public void enterState() {
			super.enterState();
			loginButton.setEnabled(true);
			registerButton.setEnabled(true);
			logoutButton.setEnabled(false);
		}
	
		@Override
		public void onRegister() {
			String un = unameEditText.getText().toString();
			String pw = pwordEditText.getText().toString();
			pwordEditText.setText("");
			try {
				messageProducer.register(un, pw);
			} catch (Exception e) {
				Log.e(getClass().getCanonicalName(), "register() throws ", e);
			}
		}
	
		@Override
		public void onLogin() {
			String un = unameEditText.getText().toString();
			String pw = pwordEditText.getText().toString();
			pwordEditText.setText("");
			try {
				messageProducer.login(un, pw);
			} catch (Exception e) {
				Log.e(getClass().getCanonicalName(), "login() throws ", e);
			}
	
		}
	
		@Override
		public void gotSuccess() {
			setState(loggedIn);
			outputText.setText("got success");
		}
	
		@Override
		public void gotFail() {
			outputText.setText("got fail");
		}
	
		@Override
		public void gotLogout() {
			outputText.setText("logged out");
		}
	};

	ChatAndroidClientState loggedIn = new ConnectedState("loggedIn") {
	
		@Override
		public void enterState() {
			super.enterState();
			loginButton.setEnabled(false);
			registerButton.setEnabled(false);
			logoutButton.setEnabled(true);
			getChattersButton.setEnabled(true);
			getChatroomsButton.setEnabled(true);
		}
		
		@Override
		public void exitState() {
			getChattersButton.setEnabled(false);
			getChatroomsButton.setEnabled(false);
		}
	
		@Override
		public void onLogout() {
			logoutButton.setEnabled(false);
			setState(notLoggedIn);
			try {
				messageProducer.logout();
			} catch (Exception e) {
				Log.e(getClass().getCanonicalName(), "logout() throws ", e);
			}
		}
		
		@Override
		public void onGetChatrooms() {
			messageProducer.getChatrooms();
		}
		
		@Override
		public void onGetChatters() {
			messageProducer.getChatters();
		}
	};

	private class ConnectedState extends ChatAndroidClientState {

		public ConnectedState(String n) {
			super(n);
		}

		@Override
		public void onDisconnect() {
			if (messageProducer instanceof IBrokerConnection) {
				IBrokerConnection msgSender = (IBrokerConnection) messageProducer;
				msgSender.disconnect();
				setState(notConnected);
			} else {
				Log.e("ClientStateManager", "cannot send disconnect message");
			}
		}
		
		@Override
		public void gotChatters(String[] chatters) {
			StringBuilder text = new StringBuilder("gotChatters\n");
			for (int i = 0; i < chatters.length; i++) {
				String c = chatters[i];
				text.append(c).append('\n');
			}
			outputText.setText(text);
		}

		@Override
		public void gotChatrooms(String[] chatrooms) {
			StringBuilder text = new StringBuilder("gotChatrooms\n");
			for (int i = 0; i < chatrooms.length; i++) {
				String c = chatrooms[i];
				text.append(c).append('\n');
			}
			outputText.setText(text);
		}
	}

}
