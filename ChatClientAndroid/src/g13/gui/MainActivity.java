package g13.gui;

import g13.message.interfaces.IReceiveStompMessages;
import g13.message.interfaces.ISendStompMessages;
import g13.message.interfaces.StompCommunicationService;
import g13.message.logic.ChatGUIAdapter;
import g13.message.logic.ChatStompAdaptertwo;
import de.fh_zwickau.android.base.architecture.BindServiceHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;


/**
 * This is the Main Activity for the chat client
 * @author Andre Furchner
 */
public class MainActivity extends Activity {

	protected static ChatGUIAdapter guiAdapter = new ChatGUIAdapter();
	protected static boolean isTestGUI = true;
	
	private ChatStompAdaptertwo stompAdapter;
	private BindServiceHelper<ISendStompMessages, IReceiveStompMessages, MainActivity> stompServiceHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		guiAdapter.setGui(this);
		gotoMainView();	
		
		
		stompAdapter = new ChatStompAdaptertwo();
		stompServiceHelper = new BindServiceHelper<ISendStompMessages, IReceiveStompMessages, MainActivity>(
				stompAdapter, this, new Intent(this,
						StompCommunicationService.class));
		stompAdapter.setServiceHelper(stompServiceHelper);
		
		
		stompServiceHelper.bindService();
		
		
		
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
					ip = "localhost:61616";
				}
				
				// DEBUG FLAG
				isTestGUI = box.isChecked();
				// FIXME: DEBUG IF ELSE
				if(isTestGUI) {
					gotoNotLoggedInView();
				}
				else {
					guiAdapter.Connect(ip);
				}
			}
			
		});
		
		// DEBUG TEST BUTTON ////////////////////////////////////////////////// DEBUG AREA //
		findViewById(R.id.test).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO: TEST FUNCTION
				
			}
		});
		// DEBUG AREA ///////////////////////////////////////////////////////// DEBUG AREA //
	}
	
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
				//else // TODO:
			}
			
		});
		
	}
	
	public void gotoOwnChatView() {
		setContentView(R.layout.activity_own_chat);	
	}
	
	/**
	 * Activate A new Activity
	 * @param activity Class Name
	 */
	public void SetActivity(Class<?> activity) {
		SetActivity(activity, "");
	}
	
	/**
	 * Activate a new Activity with specific type
	 * @param activity Class Name
	 * @param type specific type
	 */
	public void SetActivity(Class<?> activity, String type) {
		Intent intent = new Intent (this, activity);
		Bundle bundle = new Bundle();
		bundle.putString("type", type);
		
		startActivity(intent, bundle);
	}
	
	/**
	 * Add a sting line to chat log
	 * @param log
	 */
	public void AddLineToLog(String log) {
		TextView txt = (TextView)findViewById(R.string.str_chatlog);
		txt.setText(txt.getText() + "\n" + log);
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
	
}
