package g13.gui;

import g13.message.logic.ChatGUIAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * The Popup class is an DialogFragment.
 * He is used to invoke an accept or deny for different client states.
 * @author Andre Furchner
 *
 */
public class Popup extends DialogFragment {
	private AlertDialog.Builder builder;
	private ChatGUIAdapter guiAdapter;
	private String type, item, msg;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		builder = new AlertDialog.Builder(getActivity());
		switch (type) {
		case "gotInvite":
		case "accUser":
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							guiAdapter.popupCancelPressed(type, item);
						}
					});
			break;
		}

		// Use the Builder class for convenient dialog construction
		builder.setMessage(msg);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				guiAdapter.popupOkPressed(type, item);
			}
		});
				
        // Create the AlertDialog object and return it
        return builder.create();
    }
    
	/**
	 * Set the communication GUIAdapter. 
	 * @param cga The ChatGUIAdaper
	 */
    public void setGUIAdapter(ChatGUIAdapter cga) {
    	guiAdapter = cga;
    }
    
    /**
     * Set Message for Popup window
     * @param type The Popup type to identify
     * @param item The Item to accept or deny
     * @param msg A Message to display
     */
    public void setMessage(String type, String item, String msg) {
    	this.msg = msg;
    	this.type = type;
    	this.item = item;
    }
}