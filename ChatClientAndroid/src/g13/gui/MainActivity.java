package g13.gui;

import g13.message.interfaces.IReceiveStompMessages;
import g13.message.interfaces.ISendStompMessages;
import g13.message.logic.ChatGUIAdapter;
import g13.message.logic.ChatStompAdapter;
import g13.message.logic.service.StompCommunicationService;
import g13.state.ChatClientState;
import g13.state.client.connection.NotConnected;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import de.fh_zwickau.android.base.architecture.BindServiceHelper;


/**
 * This is the Main Activity for the chat client
 * @author Andre Furchner
 */
public class MainActivity extends Activity {

	private ChatClientState savedState = null;

	protected static boolean isTestGUI = true;
	private ChatGUIAdapter guiAdapter;
	
	private ChatStompAdapter stompAdapter;
	private BindServiceHelper<ISendStompMessages, IReceiveStompMessages, MainActivity> stompServiceHelper;
	
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
		if (savedState!=null) {
			savedState.register(stompAdapter, guiAdapter);
		} else {
			new NotConnected(stompAdapter, guiAdapter);
		}
		stompServiceHelper.bindService();
		
		//while (!stompServiceHelper.isBound()) {
			
		//Log.e("nop", "nop");
		// }


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
				isTestGUI = box.isChecked();
				// FIXME: DEBUG IF ELSE
				if(isTestGUI) {
					gotoNotLoggedInView();
				}
				else {
							// guiAdapter.onConnect(ip); //TODO implement on
							// connect
				}
			}
			
		});
		
		// DEBUG TEST BUTTON ////////////////////////////////////////////////// DEBUG AREA //
		findViewById(R.id.test).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO: TEST FUNCTION
				Log.i("main", "testclick");
				// stompAdapter.connectToServer("10.0.2.2", 61613, "hut",
				// "schnur");
				// stompAdapter.login("hut", "schnur");
				gotoTestView();
				// stompAdapter.askForChats();
				// stompAdapter.askForChatters();
			}
		});
		// DEBUG AREA ///////////////////////////////////////////////////////// DEBUG AREA //
	}

	// DEBUG AREA /////////////////////////////////////////////////////////
	// DEBUG AREA //
	public void gotoTestView() {
		setContentView(R.layout.activity_test);

		// Button to create a new chat room
		findViewById(R.id.toMain).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoMainView();
			}
		});

		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stompAdapter
.connectToServer("10.0.2.2", 61613, "", "");
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

	// DEBUG AREA /////////////////////////////////////////////////////////
	// DEBUG AREA //
	/**
	 * Create Listener for Not logged in view
	 */
	public void gotoNotLoggedInView() {
		setContentView(R.layout.activity_not_logged_in);	
		
		final TextView name = (TextView)findViewById(R.id.name);
		final TextView password = (TextView)findViewById(R.id.password);
		
		// Login Button
		findViewById(R.id.btn_main_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// FIXME: DEBUG IF ELSE
				if(isTestGUI) gotoLoggedInView();
				else guiAdapter.buttonLoginPressed(name.getText().toString(), password.getText().toString());
			}
			
		});
		
		// Register Button
		findViewById(R.id.btn_register).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// FIXME: DEBUG IF ELSE
				if(isTestGUI) gotoLoggedInView();
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
				if(isTestGUI) gotoOwnChatView();
				else guiAdapter.buttonCreateChatPressed();
			}
			
		});
		
		// Button Join Pressed
		findViewById(R.id.join).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// FIXME: DEBUG IF ELSE
				if(isTestGUI) {
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
		
	}
	
	public void gotoOwnChatView() {
		setContentView(R.layout.activity_chat);	
	}
	
	/**
	 * Activate a new Activity with specific type
	 * @param activity Class Name
	 * @param type specific type
	 */
	public void SetActivity(Class<?> activity, ArrayList<String> items) {
		Intent intent = new Intent (this, activity);
		Bundle bundle = new Bundle();
		
		bundle.putStringArrayList("items", items);
		
		startActivity(intent, bundle);
	}
	
	/**
	 * Add a sting line to chat log
	 * @param log
	 */
	public void AddLineToLog(String log) {
		TextView debugText = (TextView) findViewById(R.id.debugLog);
		debugText.setText(log);
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
		savedState = guiAdapter.getState();
		super.onPause();
	}
}
