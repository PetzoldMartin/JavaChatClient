package g13.gui;

import g13.message.logic.ChatGUIAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

	protected static ChatGUIAdapter guiAdapter = new ChatGUIAdapter();
	protected static boolean isTestGUI = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		guiAdapter.setGui(this);
		
		gotoMainView();			
		
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
		
	}

}
