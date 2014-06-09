package de.fh_zwickau.pti.jms.userservice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.fh_zwickau.informatik.stompj.Connection;
import de.fh_zwickau.informatik.stompj.ErrorMessage;
import de.fh_zwickau.informatik.stompj.MessageHandler;
import de.fh_zwickau.informatik.stompj.StompJException;
import de.fh_zwickau.informatik.stompj.StompMessage;
import de.fh_zwickau.informatik.stompj.internal.MessageImpl;
import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * Demo-Klasse für die Verwendung des Stomp-Protokolls
 * 
 * @author georg beier
 * 
 */
public class StompTestClient {

	private Connection stompConnection;
	private static String stompQ = "/queue/loginq";// + AuthenticationServer.LOGINQ;
	private static String stompReply = "/temp-queue/replyq";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String brokerUrl = "localhost";
		int port = 61613;
		if (args.length == 2) {
			brokerUrl = args[0];
			port = Integer.parseInt(args[1]);
		}
		StompTestClient testClient = new StompTestClient();
		testClient.runTest(brokerUrl, port);
	}

	/**
	 * konfiguriere Message System und schicke alle 5 sec eine Message
	 * 
	 * @param brokerUrl
	 *            URI of JMS Message broker
	 * @param port
	 *            connection port for stomp messages
	 */
	private void runTest(String brokerUrl, int port) {
		// connection zu einem Message Broker aufbauen
		stompConnection = new Connection(brokerUrl, port, "sys", "man");
		ErrorMessage emsg;
		try {
			emsg = stompConnection.connect();
			// wenn null zurückkommt, hat die Verbindung geklappt
			Logger.getRootLogger().log(Level.ERROR,
					"Connect error message: " + emsg);
			// subsription zu einer queue
			stompConnection.subscribe(stompReply, true);
			// message handler verbinden, können mehrere sein
			stompConnection.addMessageHandler(stompReply, messageHandler);

			MessageImpl testMessage;
			testMessage = makeLoginMsg("schlapp", "hut");
			stompConnection.send(testMessage, stompQ);
			Thread.sleep(2000);
			testMessage = makeLoginMsg("hut", "schnur");
			stompConnection.send(testMessage, stompQ);
			Thread.sleep(2000);
			testMessage = makeLoginMsg("potz", "blitzp");
			stompConnection.send(testMessage, stompQ);
			Thread.sleep(2000);
			testMessage = makeLoginMsg("muetze", "cap");
			stompConnection.send(testMessage, stompQ);
			Thread.sleep(2000);
			System.exit(0);
		} catch (StompJException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * lege testweise login message an
	 * 
	 * @param un
	 *            user name
	 * @param pw
	 *            passwort
	 * @return die fertige message
	 */
	private MessageImpl makeLoginMsg(String un, String pw) {
		MessageImpl loginMessage = new MessageImpl();
		// message properties werden als HashMap übergeben
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("reply-to", stompReply);
		props.put(MessageHeader.MsgKind.toString(),
				MessageKind.login.toString());
		props.put(MessageHeader.LoginUser.toString(), un);
		props.put(MessageHeader.LoginPassword.toString(), pw);
		loginMessage.setContent("login");
		// die properties map hinzufügen
		loginMessage.setProperties(props);
		return loginMessage;
	}

	/**
	 * test message handler
	 */
	private MessageHandler messageHandler = new MessageHandler() {
		
		private String playerServiceQ = "";
		private HashSet<String> tokens = new HashSet<>();
		
		@Override
		public void onMessage(StompMessage message) {
			String replyQueue = message.getProperty("reply-to");
			String msgKind = message.getProperty(MessageHeader.MsgKind.toString());
			String token = message.getProperty(MessageHeader.AuthToken.toString());			
			if (token != null && msgKind.equals(MessageKind.authenticated.toString())) {
				tokens.add(token);
				playerServiceQ = replyQueue;
				;
			}
			System.out.println("Received " + msgKind + ": " + token
					+ " (" + message.getContentAsString() + ")");
		}
		
	};

}
