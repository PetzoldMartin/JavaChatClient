package g13.gui;

import g13.gui.R;
import g13.message.logic.ChatGUIAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

	ChatGUIAdapter guiAdapter;
	
	public MainActivity() {
		super();
		guiAdapter = new ChatGUIAdapter();
		guiAdapter.setGui(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		
		final TextView name = (TextView)findViewById(R.id.name);
		final TextView password = (TextView)findViewById(R.id.password);
		
		// Login Button
		findViewById(R.id.btn_login).setOnClickListener(new OnClickListener() {
			
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

}
