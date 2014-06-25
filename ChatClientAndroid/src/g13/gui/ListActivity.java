/**
 * 
 */
package g13.gui;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A Basic List of items that can be selected
 *  @author Andre Furchner
 */
public class ListActivity extends Activity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_list);
		
		ListView list = (ListView) findViewById(R.id.list);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.getItemList());		
		list.setAdapter(arrayAdapter);
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MainActivity.gotSelectedItem((String)parent.getItemAtPosition(position));
				finish();
			}
			
		});		

	}
}
