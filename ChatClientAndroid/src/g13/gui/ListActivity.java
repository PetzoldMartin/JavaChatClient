/**
 * 
 */
package g13.gui;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author Andre Furchner
 *
 */
public class ListActivity extends MainActivity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_list);
		
		ListView list = (ListView) findViewById(R.id.list);
		
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_list, itemList);
		
		list.setAdapter(arrayAdapter);
		//ArrayList<Button> buttons = new ArrayList<>();
		//buttons.add(new Button(this));
		//list.addChildrenForAccessibility(buttons);

	}

}
