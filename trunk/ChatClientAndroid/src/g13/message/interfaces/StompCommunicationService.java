package g13.message.interfaces;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import de.fh_zwickau.android.base.architecture.IBindingCallbacks;
import de.fh_zwickau.informatik.stompj.Connection;
import de.fh_zwickau.informatik.stompj.ErrorMessage;
import de.fh_zwickau.informatik.stompj.MessageHandler;
import de.fh_zwickau.informatik.stompj.StompMessage;

public class StompCommunicationService extends Service {

	public static final String MESSAGE_CONTENT = "msg-content";
	public static final String STOMP_REPLY = "/temp-queue/replyq";

	private Connection stompConnection;

	public StompCommunicationService() {
	}

	/**
	 * konfiguriere Message System
	 * 
	 * @param brokerUrl
	 *            URI of JMS Message broker
	 * @param port
	 *            connection port for stomp messages
	 * @param uname
	 *            login am Broker
	 * @param pw
	 *            password am Broker
	 */
	private void stompConnect(String brokerUrl, int port, String uname,
			String pw) {
		// connection zu einem Message Broker aufbauen
		stompConnection = new Connection(brokerUrl, port, uname, pw);
		ErrorMessage emsg;
		try {
			emsg = stompConnection.connect();
			// wenn null zurückkommt, hat die Verbindung geklappt
			if (emsg != null) {
				callbackProvider.onError(emsg.getContentAsString());
				callbackProvider.onConnection(false);
				Log.e("StompConnectError", emsg.getContentAsString());
			} else {
				// subsription zu einer queue
				stompConnection.subscribe(STOMP_REPLY, true);
				// message handler verbinden, können mehrere sein
				stompConnection.addMessageHandler(STOMP_REPLY, stompHandler);
				callbackProvider.onConnection(true);
			}
		} catch (Exception e) {
			callbackProvider.onError(e.toString());
			callbackProvider.onConnection(false);
			Log.e("StompConnectError", "Stomp Connection Error");
		}
	}

	/**
	 * test message handler
	 */
	private MessageHandler stompHandler = new MessageHandler() {

		@Override
		public void onMessage(StompMessage message) {
			callbackProvider.onStompMessage(message);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	private StompBinder binder = new StompBinder();

	public class StompBinder extends Binder implements ISendStompMessages {

		@Override
		public void connect(final String url, final int port,
				final String user, final String pw) {
			Thread t = new Thread() {
				@Override
				public void run() {
					stompConnect(url, port, user, pw);
				}
			};
			t.start();
		}

		@Override
		/**
		 * dauert länger, daher als AsyncTask ausführen
		 */
		public void disconnect() {
			AsyncTask<Void, Void, Void> x;
			if (stompConnection != null && stompConnection.isConnected()) {
				x = new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						stompConnection.disconnect();
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						callbackProvider.onConnection(false);
						super.onPostExecute(result);
					}
				};
				x.execute();
			}
		}

		@Override
		public boolean isConnected() {
			return (stompConnection != null && stompConnection.isConnected());
		}

		@Override
		public void sendMessage(StompMessage message, String... destination) {
			if (stompConnection != null && stompConnection.isConnected())
				stompConnection.send(message, destination);
		}

		@Override
		public void setMessageHandler(Handler msgHandler) {
			callbackProvider.setCallbackHandler(msgHandler);
		}

	}

	private StompCallbackProvider callbackProvider = new StompCallbackProvider();

	private class StompCallbackProvider {

		private Handler callbackHandler;

		public void onStompMessage(StompMessage message) {
			Bundle bundledMessage = new Bundle();
			bundledMessage
					.putString(IBindingCallbacks.CBMETH, "onStompMessage");
			bundledMessage.putSerializable(IBindingCallbacks.CBVAL, message);
			send(bundledMessage);
		}

		public void onConnection(boolean success) {
			Bundle bundle = new Bundle();
			bundle.putString(IBindingCallbacks.CBMETH, "onConnection");
			bundle.putBoolean(IBindingCallbacks.CBVAL, success);
			send(bundle);
		}

		public void onError(String error) {
			Bundle bundle = new Bundle();
			bundle.putString(IBindingCallbacks.CBMETH, "onError");
			bundle.putString(IBindingCallbacks.CBVAL, error);
			send(bundle);
		}

		private void send(Bundle b) {
			if (callbackHandler != null) {
				Message msg = new Message();
				msg.setData(b);
				callbackHandler.sendMessage(msg);
			}
		}

		public void setCallbackHandler(Handler callbackHandler) {
			this.callbackHandler = callbackHandler;
		}

	}
}
