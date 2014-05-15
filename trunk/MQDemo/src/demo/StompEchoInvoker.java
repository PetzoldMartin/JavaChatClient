package demo;

import java.util.HashMap;

import de.fh_zwickau.informatik.stompj.Connection;
import de.fh_zwickau.informatik.stompj.ErrorMessage;
import de.fh_zwickau.informatik.stompj.StompMessage;
import de.fh_zwickau.informatik.stompj.MessageHandler;
import de.fh_zwickau.informatik.stompj.StompJException;
import de.fh_zwickau.informatik.stompj.internal.MessageImpl;

/**
 * Demo-Klasse für die Verwendung des Stomp-Protokolls
 * 
 * @author georg beier
 * 
 */
public class StompEchoInvoker {

	private Connection stompConnection;
	private static String stompQ = "/queue/eq";
	private static String stompReply = "/temp-queue/xxx";

	/**
	 * etwas ausführlicherer message handler
	 */
	private MessageHandler m0 = new MessageHandler() {
		@Override
		public void onMessage(StompMessage message) {
			String[] keys = message.getPropertyNames();
			for (String key : keys) {
				System.out.println(key + ": " + message.getProperty(key));
			}
			System.out.println("Queue: " + message.getDestination());
			System.out.println("ID: " + message.getMessageId() + "\nContent: |"
				+ message.getContentAsString() + "|\n");
		}
	};

	/**
	 * minimaler message handler
	 */
	private MessageHandler m1 = new MessageHandler() {
		@Override
		public void onMessage(StompMessage message) {
			System.out
				.println("\nm1: |" + message.getContentAsString() + "|\n");
		}
	};

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		StompEchoInvoker echoInvoker = new StompEchoInvoker();
		echoInvoker.stomp();
	}

	/**
	 * konfiguriere Message System und schicke alle 5 sec eine Message
	 */
	private void stomp() {
		// message properties werden als HashMap übergeben
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("reply-to", stompReply);
		// connection zu einem Message Broker aufbauen
		stompConnection = new Connection("localhost", 61613, "sys", "man");
//		stompConnection = new Connection("192.168.111.102", 61613, "sys", "man");
		ErrorMessage emsg;
		try {
			emsg = stompConnection.connect();
			// wenn null zurückkommt, hat die Verbindung geklappt
			System.out.println("Connect error message: " + emsg);
			// subsription zu einer queue
			stompConnection.subscribe(stompReply, true);
			// message handler verbinden, können mehrere sein
			stompConnection.addMessageHandler(stompReply, m0);
			stompConnection.addMessageHandler(stompReply, m1);

			MessageImpl message;
			while (true) {
				String id = "" + System.currentTimeMillis();
				props.put("correlation-id", id);
				message = new MessageImpl();
				// der message-inhalt wird als byte array übertragen
				message.setContent(("hurz@" + id).getBytes());
				// die properties map hinzufügen
				message.setProperties(props);
				// und an eine serviceRequestQ senden
				stompConnection.send(message, stompQ);
				Thread.sleep(5000);
			}
		} catch (StompJException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
