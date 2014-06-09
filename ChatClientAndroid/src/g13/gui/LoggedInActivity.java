/**
 * 
 */
package g13.gui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author foxel
 *
 */
public class LoggedInActivity extends MainActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loggedin);
		
		findViewById(R.id.create).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// FIXME: DEBUG IF ELSE
				if(isTestGUI) SetActivity(ChatActivity.class);
				//else // TODO:
			}
			
		});
	}
	
	@Override
	/**
	 * User Want to logout
	 */
	public void onBackPressed() {
		// FIXME: DEBUG IF ELSE
		if(isTestGUI) finish();
		else guiAdapter.buttonLogoutPressed();
	}
		
}
