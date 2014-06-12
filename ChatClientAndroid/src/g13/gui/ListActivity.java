/**
 * 
 */
package g13.gui;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author foxel
 *
 */
public class ListActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ListView list = (ListView) findViewById(R.id.list);
		ArrayList<Button> buttons = new ArrayList<>();
		buttons.add(new Button(this));
		//list.addChildrenForAccessibility(buttons);

	}

}
