package de.fh_zwickau.pti.mqchatandroidclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import de.fh_zwickau.android.base.architecture.BindServiceHelper;
import de.fh_zwickau.pti.jms.chatclient.android.R;

public class ChatActivity extends Activity {

	private ClientStateManager stateManager;
	private ChatStompAdapter stompAdapter;
	private BindServiceHelper<ISendStompMessages, IReceiveStompMessages, ChatActivity> stompServiceHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);

		stateManager = new ClientStateManager(this);
		stompAdapter = new ChatStompAdapter();
		stompServiceHelper = new BindServiceHelper<ISendStompMessages, IReceiveStompMessages, ChatActivity>(
				stompAdapter, this, new Intent(this,
						StompCommunicationService.class));
		stompAdapter.setServiceHelper(stompServiceHelper);
		stompAdapter.setMessageReceiver(stateManager);
		stateManager.setMessageProducer(stompAdapter);
		stompServiceHelper.bindService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	public void onExit(MenuItem item) {
		try {
			stompAdapter.logout();
		} catch (Exception e) {
		}
		stompAdapter.disconnect();
		stompServiceHelper.stopService();
		stompServiceHelper.unbindService();
		this.finish();
	}
}
