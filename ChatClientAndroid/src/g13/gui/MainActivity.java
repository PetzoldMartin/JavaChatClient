package g13.gui;

import g13.message.interfaces.IReceiveStompMessages;
import g13.message.interfaces.ISendStompMessages;
import g13.message.logic.ChatGUIAdapter;
import g13.message.logic.ChatStompAdapter;
import g13.message.logic.service.StompCommunicationService;
import g13.state.client.connection.NotConnected;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import de.fh_zwickau.android.base.architecture.BindServiceHelper;

/**
 * The Main Activity for the chat client
 * He is working together with the ListActivity
 * @author Andre Furchner
 */
public class MainActivity extends Activity {

	// static fields
	private static ArrayList<String> itemList = new ArrayList<String>();
	private static ChatGUIAdapter guiAdapter;
	
	// member fields
	private ChatStompAdapter stompAdapter;
	private BindServiceHelper<ISendStompMessages, IReceiveStompMessages, MainActivity> stompServiceHelper;
	private TextView textOut = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		stompAdapter = new ChatStompAdapter();
		guiAdapter = new ChatGUIAdapter(this);
		stompServiceHelper = new BindServiceHelper<ISendStompMessages, IReceiveStompMessages, MainActivity>(
				stompAdapter, this, new Intent(this,
						StompCommunicationService.class));
		stompAdapter.setServiceHelper(stompServiceHelper);
		stompAdapter.setMessageReceiver(guiAdapter);
		stompServiceHelper.bindService();
		initState();
	}

	public void onExit(MenuItem item) {
		try {
			stompAdapter.logout();
		} catch (Exception e) {
			Log.e("Stomp-Adapter", "logout fail");
		}
		stompAdapter.disconnect();
		stompServiceHelper.unbindService();
		stompServiceHelper.stopService();
		Log.d("MainActivity", "onExit");
		this.finish();
	}

	/**
	 * restore the saved state
	 */
	private void initState() {
		new NotConnected(stompAdapter, guiAdapter);
	}
	/**
	 * Create Listener for Main view
	 */
	public void gotoConnectView() {
		
		setContentView(R.layout.clear);
		setContentView(R.layout.activity_main);	

		final TextView server = (TextView)findViewById(R.id.server);
		// server.setText("141.32.24.100");
		server.setText("192.168.99.100");
		// server.setText("192.168.0.7");
		
		// Button to connect to server
		findViewById(R.id.btn_main_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String ip = server.getText().toString();
				if(ip.equals("")) {
							ip = getString(R.string.server);
				}
				guiAdapter.onConnect(ip, 61613, "", "");
			}
		});
	}

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
				guiAdapter.buttonLoginPressed(name.getText().toString(), password.getText().toString());
			}
		});
		
		// Register Button
		findViewById(R.id.btn_register).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				guiAdapter.buttonRegisterPressed(name.getText().toString(), password.getText().toString());
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
				guiAdapter.buttonCreateChatPressed();
			}
		});
		
		// Button Join Pressed
		findViewById(R.id.join).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				guiAdapter.buttonJoinPressed();
			}
		});
		
		// Button Logout Pressed
		findViewById(R.id.logout).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				guiAdapter.buttonLogoutPressed();
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
				guiAdapter.buttonSendPressed(chatText);
				textView.setText("");
			}
		});
		
		// Button Leave Pressed
		findViewById(R.id.btn_leave).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				guiAdapter.buttonLeavePressed();
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
				guiAdapter.buttonSendPressed(chatText);
				textView.setText("");
			}
		});
		
		textOut = (TextView)findViewById(R.id.txt_chatlog);
		 
		// Button Invite Pressed
		findViewById(R.id.btn_invite).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				guiAdapter.buttonInvitePressed();
			}
		});
		
		// Button Close Pressed
		findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				guiAdapter.buttonClosePressed();
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
	 * Add a line to the chat log
	 * @param chatter user name
	 * @param messageText message from user
	 */
	public void addLineToChatLog(String chatter, String messageText) {
		if(textOut != null) textOut.setText(textOut.getText().toString()+"\n"+chatter+": "+messageText);
	}
	
	/**
	 * Add a line to the chat log
	 * @param line a line to add
	 */
	public void addLineToChatLog(String line) {
		if(textOut != null) textOut.setText(textOut.getText().toString() + "\n" + line);
	}
	
	/**
	 * Returns the item list for the ListActivity
	 * @return items
	 */
	public static ArrayList<String> getItemList() {
		return itemList;
	}
	
	/**
	 * Called when a ListActivity has selected an item
	 * @param item selected Item
	 */
	public static void gotSelectedItem(String item) {
		guiAdapter.listItemSelected(item);
	}

	public void setWaiting() {
		ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
		}
	}
