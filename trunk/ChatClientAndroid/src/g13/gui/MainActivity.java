package g13.gui;

import g13.message.interfaces.IReceiveStompMessages;
import g13.message.interfaces.ISendStompMessages;
import g13.message.logic.ChatGUIAdapter;
import g13.message.logic.ChatStompAdapter;
import g13.message.logic.service.StompCommunicationService;
import g13.state.ChatClientState;
import g13.state.client.LoggedIn;
import g13.state.client.chat.InOwnChat;
import g13.state.client.connection.NotConnected;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import de.fh_zwickau.android.base.architecture.BindServiceHelper;


/**
 * The Main Activity for the chat client
 * @author Andre Furchner
 */
public class MainActivity extends Activity {

	private ChatClientState savedState = null;

	private static boolean debug_isTestGUI = true;
	
	private static ArrayList<String> itemList = new ArrayList<String>();
	private static ChatGUIAdapter guiAdapter;
	
	private ChatStompAdapter stompAdapter;
	private BindServiceHelper<ISendStompMessages, IReceiveStompMessages, MainActivity> stompServiceHelper;

	private TextView textOut=null;
	private TextView debugLog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		gotoMainView();
		
		
		stompAdapter = new ChatStompAdapter();
		guiAdapter = new ChatGUIAdapter(this);
		
		stompServiceHelper = new BindServiceHelper<ISendStompMessages, IReceiveStompMessages, MainActivity>(
				stompAdapter, this, new Intent(this,
						StompCommunicationService.class));
		stompAdapter.setServiceHelper(stompServiceHelper);
		stompAdapter.setMessageReceiver(guiAdapter);
		restoreState();

		stompServiceHelper.bindService();

		debugLog = (TextView) findViewById(R.id.debugLog);
		// debugLog.;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	public void onExit(MenuItem item) {
		try {
			stompAdapter.logout();
		} catch (Exception e) {

		}
		stompAdapter.disconnect();
		stompServiceHelper.stopService();
		stompServiceHelper.unbindService();
		this.finish();
	}

	@Override
	public void onPause() {
		saveState();
		super.onPause();
	}

	/**
	 * saves the old state for restore
	 */
	private void saveState() {
		// TODO how to save the state when app is totally erased from memory!!!
		savedState = guiAdapter.getState();
	}

	/**
	 * restore the saved state
	 */
	private void restoreState() {
		if (savedState != null) {
			// TODO restore old state ? in bind service ?
			savedState.register(stompAdapter, guiAdapter);
			savedState.restore();
		} else {
			new NotConnected(stompAdapter, guiAdapter);
		}
	}

	/**
	 * Create Listener for Main view
	 */
	public void gotoMainView() {

		setContentView(R.layout.activity_main);	

		final TextView server = (TextView)findViewById(R.id.server);
		
		// DEBUG
		final CheckBox box = (CheckBox)findViewById(R.id.isDebug);
		
		// Button to connect to server
		findViewById(R.id.btn_main_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String ip = server.getText().toString();
				if(ip.equals("")) {
							ip = getString(R.string.server);
				}
				
				// DEBUG FLAG
				debug_isTestGUI = box.isChecked();
				// FIXME: DEBUG IF ELSE
				if(debug_isTestGUI) {
					gotoNotLoggedInView();
				}
				else {
					guiAdapter.onConnect(ip, 61613, "", "");
				}
			}
			
		});
		
		// DEBUG TEST BUTTON ////////////////////////////////////////////////// DEBUG AREA //
		findViewById(R.id.test).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO: TEST FUNCTION
				Log.i("main", "testclick");
				gotoTestView();
				

			}
		});
		// DEBUG AREA ///////////////////////////////////////////////////////// DEBUG AREA //
	}

	// DEBUG AREA /////////////////////////////////////////////////////////// DEBUG AREA //
	public void gotoTestView() {
		setContentView(R.layout.clear);

		setContentView(R.layout.activity_test);
		// Button to create a new chat room
		findViewById(R.id.toMain).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoMainView();
			}
		});

		findViewById(R.id.btn_leave).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stompAdapter
.connectToServer("192.168.1.128", 61613, "", "");
			}
		});
		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stompAdapter.login("xx", "xy");
			}
		});
		findViewById(R.id.button3).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stompAdapter.askForChats();
			}
		});
		findViewById(R.id.button4).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stompAdapter.askForChatters();
			}
		});
		findViewById(R.id.button5).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stompAdapter.logout();
			}
		});
		findViewById(R.id.button6).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stompAdapter.register("xx", "xy");
				;
			}
		});
	}

	// DEBUG AREA /////////////////////////////////////////////////////////// DEBUG AREA //
	/**
	 * Create Listener for Not logged in view
	 */
	public void gotoNotLoggedInView() {
		setContentView(R.layout.clear);

		setContentView(R.layout.activity_not_logged_in);	
		
		final TextView name = (TextView)findViewById(R.id.name);
		final TextView password = (TextView)findViewById(R.id.password);
		
		// Login Button
		findViewById(R.id.btn_main_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// FIXME: DEBUG IF ELSE
				if(debug_isTestGUI) guiAdapter.gotSuccess();
				else guiAdapter.buttonLoginPressed(name.getText().toString(), password.getText().toString());
			}
			
		});
		
		// Register Button
		findViewById(R.id.btn_register).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// FIXME: DEBUG IF ELSE
				if(debug_isTestGUI) guiAdapter.gotSuccess();
				else guiAdapter.buttonRegisterPressed(name.getText().toString(), password.getText().toString());
			}
			
		});
		
	}
	
	/**
	 * Create Listener for logged in view
	 */
	public void gotoLoggedInView() {
		setContentView(R.layout.activity_loggedin);
		
		// Button to create a new chat room
		findViewById(R.id.create).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// FIXME: DEBUG IF ELSE
				if(debug_isTestGUI) guiAdapter.gotChatStarted("Debug chatroom");
				else guiAdapter.buttonCreateChatPressed();
			}
			
		});
		
		// Button Join Pressed
		findViewById(R.id.join).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// FIXME: DEBUG IF ELSE
				if(debug_isTestGUI) {
					ArrayList<String> chatRooms = new ArrayList<>();
					chatRooms.add("Peters Chat");
					chatRooms.add("Martins Chat");
					chatRooms.add("Markus Chat");
					chatRooms.add("Andre Chat");
					guiAdapter.gotChats(chatRooms);
				} else {
					guiAdapter.buttonJoinPressed();
				}
			}
			
		});
		
		// Button Logout Pressed
		findViewById(R.id.logout).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// FIXME: DEBUG IF ELSE
				if(debug_isTestGUI) {
					guiAdapter.gotLogout();
				} else {
					guiAdapter.buttonLogoutPressed();
				}
			}
			
		});
		
	}
	
	/**
	 * Create the Listener for the other chat view
	 */
	public void gotoOtherChatView() {
		setContentView(R.layout.activity_chat);
		final TextView textView = (TextView)findViewById(R.id.send_textfield);
		
		// hide and show some buttons
		findViewById(R.id.btn_close).setVisibility(View.INVISIBLE);
		findViewById(R.id.btn_invite).setVisibility(View.INVISIBLE);
		findViewById(R.id.btn_leave).setVisibility(View.VISIBLE);
		
		// Button Send Pressed
		findViewById(R.id.btn_send).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String chatText = textView.getText().toString();
				if(chatText.equals("")) {
							chatText = getString(R.string.server);
				}
				
				// FIXME: DEBUG IF ELSE
				if(debug_isTestGUI) {
					guiAdapter.gotNewChat("Me", chatText);
				}
				else {
					guiAdapter.buttonSendPressed(chatText);
					textView.setText("");
				}
			}
		});
		
		// Button Leave Pressed
		findViewById(R.id.btn_leave).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// FIXME: DEBUG IF ELSE
				if(debug_isTestGUI) {
					gotoLoggedInView();
				}
				else {
					guiAdapter.buttonLeavePressed();
				}
			}
		});
		
		textOut = (TextView)findViewById(R.id.txt_chatlog);
	}
	
	/**
	 * Create the Listener and View for the own chat
	 */
	public void gotoOwnChatView() {
		setContentView(R.layout.activity_chat);
		final TextView textView = (TextView)findViewById(R.id.send_textfield);

		// hide and show some buttons
		findViewById(R.id.btn_close).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_invite).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_leave).setVisibility(View.INVISIBLE);
		
		// Button Send Pressed
		findViewById(R.id.btn_send).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String chatText = textView.getText().toString();
				if(chatText.equals("")) {
							chatText = getString(R.string.server);
				}
				

				if(debug_isTestGUI) {
					guiAdapter.gotNewChat("Me", chatText);
				}
				else {
					guiAdapter.buttonSendPressed(chatText);
					textView.setText("");
				}
			}
		});
		
		textOut = (TextView)findViewById(R.id.txt_chatlog);
		 
		// Button Invite Pressed
		findViewById(R.id.btn_invite).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// FIXME: DEBUG IF ELSE
				if(debug_isTestGUI) {
					ArrayList<String> user = new ArrayList<>();
					user.add("Peter");
					user.add("Martin");
					user.add("Marku");
					user.add("Andre");
					guiAdapter.gotChatters(user);
				} else {
					guiAdapter.buttonInvitePressed();
				}
				
			}
				
		});
		
		// Button Close Pressed
		findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// FIXME: DEBUG IF ELSE
				if(debug_isTestGUI) {
					guiAdapter.gotChatClosed();
				} else {
					guiAdapter.buttonClosePressed();
				}
			}
				
		});
			
					
	}
	
	/**
	 * Activate a new Activity with specific type
	 * @param activity Class Name
	 * @param type specific type
	 */
	public void SetActivity(Class<?> activity, ArrayList<String> items) {
		itemList = items;
		Intent intent = new Intent (this, activity);
		startActivity(intent);
	}
	
	/**
	 * Add a sting line to chat log
	 * @param log
	 */
	public void DebugLog(String log) {
		debugLog.setText(log + "\n" + debugLog.getText());
	}

	public void setChatinChatlog(String chatter, String messageText) {
		textOut.setText(textOut.getText().toString()+"\n"+chatter+": "+messageText);
		
	}
	
	public static ArrayList<String> getItemList() {
		return itemList;
	}
	
	public static void gotSelectedItem(String item) {
		// FIXME: DEBUG IF ELSE
		if(debug_isTestGUI) {
			if(guiAdapter.debugGetState() instanceof LoggedIn) guiAdapter.gotParticipating();
			if(guiAdapter.debugGetState() instanceof InOwnChat) guiAdapter.gotAccepted(item);
			// TODO: REMOVE THIS
			guiAdapter.gotParticipating();
		} else {
			guiAdapter.listItemSelected(item);
		}
	}
}
