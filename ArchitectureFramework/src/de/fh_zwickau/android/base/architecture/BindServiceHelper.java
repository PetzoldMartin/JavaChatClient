package de.fh_zwickau.android.base.architecture;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * Helper Klasse für die Verwaltung und Kommunikation mit Services: bind/unbind,
 * start/stop, Methodenaufrufe und asynchrone Messages werden von dieser Klasse
 * behandelt. Asynchrone Messages werden in Methodenaufrufe an die ICallback
 * Implementierung umgewandelt. Damit dies funktioniert, muss jedes Message
 * Bundle zwei Eintrage haben:
 * <ol>
 * <li>den Methodennamen der Callback Methode, die gerufen werden soll als
 * String mit dem Key IBindingCallbacks.CBMETH</li>
 * <li>den übergebenen Wert mit dem Key IBindingCallbacks.CBVAL</li>
 * </ol>
 * Die Callback-Methoden müssen genau einen Parameter vom Typ Object haben.
 * 
 * @author georg beier
 * 
 * @param <IServiceBinder>
 *            Interface Typ, der die Binder-Methoden des Service definiert, die
 *            über das Helper-Objekt aufgerufen werden können
 * @param <ICallback>
 *            Interface Typ, der die Callback Methoden für Messages definiert
 * @param <CTW>
 *            ContextWrapper Typ, der den Kontext angibt, in dem der Service
 *            verwendet wird.
 */
public class BindServiceHelper<IServiceBinder extends IBindMessageHandler, ICallback extends IBindingCallbacks, CTW extends ContextWrapper> {

	/**
	 * Schnittstelle zu den aufrufbaren Methoden des Service
	 */
	private IServiceBinder serviceBinder;
	/**
	 * callback Methoden der Activity oder einer anderen Klasse
	 */
	private ICallback callbackProvider;
	/**
	 * Der Applikationskontext (d.h. Activity, Service o.ä.), in den der Service
	 * gebunden wird
	 */
	private ContextWrapper contextWrapper;
	/**
	 * Identifikation des Service durch einen Intent
	 */
	private Intent serviceIntent;
	/**
	 * Name des gebundenen Service
	 */
	private ComponentName serviceName;
	/**
	 * Callback Handler bereitstellen: Handler kann vom Service über das OS
	 * aufgerufen werden und dann asynchron messages im Main Thread verarbeiten
	 */
	private Handler messageHandler;

	/**
	 * Zugriff auf alle Callbackmethoden über ihren Namen
	 */
	// private HashMap<String, Method> callbacks = new HashMap<String,
	// Method>();

	/**
	 * initialisierender Konstruktor
	 * 
	 * @param cb
	 *            Objekt, das Callback Methoden bereitstellt
	 * @param cw
	 *            Kontext, in den Service gebunden wird
	 * @param intent
	 *            Identifikation des Service
	 */
	public BindServiceHelper(ICallback cb, ContextWrapper cw, Intent intent) {
		messageHandler = new CallbackHandler<ICallback>(cb);
		serviceIntent = intent;
		callbackProvider = cb;
		contextWrapper = cw;
		// Method[] methods = cb.getClass().getMethods();
		// for (Method method : methods) {
		// if (method.getParameterTypes().length == 1
		// && Modifier.isPublic(method.getModifiers())) {
		// callbacks.put(method.getName(), method);
		// }
		// }
		// Method[] bindingMethods = IBindingCallbacks.class.getMethods();
		// for (Method method : bindingMethods) {
		// callbacks.remove(method.getName());
		// }
	}

	/**
	 * Objekt, das Callbacks vom Service empfängt, wenn dieser verbunden oder
	 * getrennt wird.
	 */
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceBinder = null;
		}

		@SuppressWarnings("unchecked")
		@Override
		/**
		 * @param service
		 *            Referenz auf Objekt, das die IBinder Methoden (und mehr)
		 *            zur Verfügung stellt
		 */
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceName = name;
			serviceBinder = (IServiceBinder) service;
		}
	};

	/**
	 * über diese Methode können die Methoden des gebundenen Service aufgerufen
	 * werden
	 * 
	 * @return Implementierung des IServiceBinder Interface, die die
	 *         Methodenaufrufe an den Service weiterleitet
	 */
	public IServiceBinder service() {
		return serviceBinder;
	}

	/**
	 * Stelle fest, ob ein serviceBinder vorhanden ist. Wenn ja, ist der
	 * verwaltete Service gebunden
	 * 
	 * @return bind Status
	 */
	public boolean isBound() {
		return serviceBinder != null;
	}

	/**
	 * Zugriff auf den MessageHandler
	 * 
	 * @return MessageHandler für Callback Messages
	 */
	public Handler getMessageHandler() {
		return messageHandler;
	}

	/**
	 * Baue eine Verbindung zum Service auf. Wenn der noch nicht existiert, wird
	 * er erzeugt (aber nicht gestartet!). Das serviceConnection Objekt empfängt
	 * Callbacks über das OS, wenn der Service verbunden bzw. getrennt wurde. Da
	 * dies einige Zeit dauern kann, soll der Verbindungsaufbau nicht im Main
	 * Thread stattfinden! Android stellt für derartige Aufgaben die
	 * Service-Klasse AsyncTask zur Verfügung, die asynchrone Aufgaben
	 * übernehmen und anschließend Aktionen im Main-Thread ausführen kann.
	 */
	public void bindService() {

		if (serviceBinder == null) { // Service ist noch nicht gebunden

			AsyncTask<Void, Void, Void> checkConnect = new AsyncTask<Void, Void, Void>() {

				/**
				 * Warte bis Service gestartet ist
				 */
				@Override
				protected Void doInBackground(Void... params) {

					contextWrapper.bindService(serviceIntent,
							serviceConnection,
							Context.BIND_AUTO_CREATE);

					while (serviceBinder == null) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
						}
					}
					return null;
				}

				/**
				 * Code der nach dem Start ausgeführt werden soll, <br>
				 * z.B. enable buttons oder callback handler verbinden
				 * 
				 * @param result
				 *            doInBackground liefert kein result
				 */
				@Override
				protected void onPostExecute(Void result) {
					callbackProvider.onServiceBound(serviceName);
					super.onPostExecute(result);
				}

			};
			checkConnect.execute();
		}
	}

	/**
	 * löse Bindung zum verwalteten service, wenn er vorher gebunden wurde
	 */
	public void unbindService() {
		if (serviceBinder != null)
			contextWrapper.unbindService(serviceConnection);
	}

	/**
	 * starte den verwalteten service
	 */
	public void startService() {
		contextWrapper.startService(serviceIntent);
	}

	/**
	 * stoppe den verwalteten service
	 */
	public void stopService() {
		contextWrapper.stopService(serviceIntent);
	}
	
	/**
	 * verbinde Service Callbacks über den Message Handler <br/>
	 * denkbar wäre, dies automatisch nach dem bindService zu machen.
	 */
	public void bindMessageHandler() {
		if(serviceBinder != null)
			serviceBinder.setMessageHandler(messageHandler);
	}
	
	/**
	 * löse Verbindung für Service Callbacks über den Message Handler <br/>
	 * denkbar wäre, dies automatisch vor dem unbindService zu machen.
	 */
	public void unbindMessageHandler() {
		if(serviceBinder != null)
			serviceBinder.setMessageHandler(null);
	}

	/**
	 * Callback Handler bereitstellen: Handler kann vom Service über das OS
	 * aufgerufen werden und dann asynchron messages im Main Thread verarbeiten.
	 * Siehe Beschreibung der umgebenden Klasse.
	 * 
	 * @author georg beier
	 * 
	 * @param <T>
	 *            Interface Typ des Objekts, das Callbacks empfängt
	 */
	private static class CallbackHandler<T> extends Handler {
		/**
		 * Zugriff auf alle Callbackmethoden über ihren Namen
		 */
		private HashMap<String, Method> callbacks = new HashMap<String, Method>();
		/**
		 * implementiert callback Methoden in der Activity oder einer anderen
		 * Klasse
		 */
		private T callbackProvider;

		/**
		 * Map von Callback Methoden für Messages aufbauen
		 * 
		 * @param cb
		 *            Objekt, das Message-Callbacks implementiert
		 */
		public CallbackHandler(T cb) {
			callbackProvider = cb;
			Method[] methods = cb.getClass().getMethods();
			for (Method method : methods) {
				if (method.getParameterTypes().length == 1
						&& Modifier.isPublic(method.getModifiers())) {
					callbacks.put(method.getName(), method);
				}
			}
			Method[] bindingMethods = IBindingCallbacks.class.getMethods();
			for (Method method : bindingMethods) {
				callbacks.remove(method.getName());
			}
		}

		@Override
		/**
		 * Message analysieren und passende callback Methode aufrufen
		 */
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String methodname = bundle.getString(IBindingCallbacks.CBMETH);
			if (callbacks.containsKey(methodname)) {
				Method method = callbacks.get(methodname);
				Object[] args = { bundle.get(IBindingCallbacks.CBVAL) };
				try {
					method.invoke(callbackProvider, args);
				} catch (Exception e) {
					Log.e("callback error", "callback invocation error on "
							+ methodname, e);
				}
			}
			super.handleMessage(msg);
		}
	}

}
