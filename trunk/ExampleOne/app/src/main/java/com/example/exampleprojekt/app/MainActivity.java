package com.example.exampleprojekt.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private Button acceptButton;
    private Button changeButton;
    private TextView greeting;
    private EditText prompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        greeting = (TextView) findViewById(R.id.textView);
        acceptButton = (Button) findViewById(R.id.AcceptButton);
        changeButton = (Button) findViewById(R.id.ChangeButton);
        prompt = (EditText) findViewById(R.id.Prompt);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.Exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onButtonClick(View view) {
        // change greeting text
        CharSequence newGreeting = prompt.getText();
        greeting.setText(newGreeting);
        // and have a popup (called a toast on android)
        Toast.makeText(this, "Greeting changed to " + newGreeting,
                Toast.LENGTH_LONG).show();
    }
    public void onChangeButtonClick(View view) {
        Toast.makeText(this, "change Activity", Toast.LENGTH_LONG).show();
	    startActivity(new Intent(this,Other.class));
        //startActivityForResult(new Intent(this, Other.class), 1);
    }


}
