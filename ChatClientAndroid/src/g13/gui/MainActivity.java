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
		
		if(this instanceof MainActivity) {
		
			setContentView(R.layout.activity_main);		
			
			final TextView server = (TextView)findViewById(R.id.server);
			
			// DEBUG
			final CheckBox box = (CheckBox)findViewById(R.id.isDebug);

			
			// Main OK Button
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
						SetActivity(NotLoggedInActivity.class);
					}
					else {
						guiAdapter.Connect(ip);
					}
				}
				
			});
			
		}
		
	}
	
	public void SetActivity(Class<?> activity) {
		Intent intent = new Intent (this, activity);
		startActivity(intent);
	}
	
	public void AddLineToLog(String log) {
		TextView txt = (TextView)findViewById(R.string.str_chatlog);
		
	}

}
