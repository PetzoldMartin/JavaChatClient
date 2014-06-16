package g13.gui;

import g13.message.logic.ChatGUIAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class Popup extends DialogFragment {
	private AlertDialog.Builder builder;
	private ChatGUIAdapter guiAdapter;
	private String type, item;
	
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
        // Use the Builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("bla bla blub")
               .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       guiAdapter.popupOkPressed(type, item);
                   }
               })
               .setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   guiAdapter.popupCanclePressed(type, item);
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    
    public void setGUIAdapter(ChatGUIAdapter cga) {
    	guiAdapter = cga;
    }
    
    public void setMessage(String type, String item, String msg) {
    	builder.setMessage("msg");
    	this.type = type;
    	this.item = item;
    }
}