package Test.serverStarts;

import messaging.logic.ChatJmsAdapter;
import messaging.logic.ChatGUIAdapter;
import de.fh_zwickau.pti.jms.userservice.chat.ChatServer;

public class SimpleStart {

	public static void main(String[] args) throws Exception {

		// FIXME autostart AuthenticationServer.main(args);
		ChatServer.main(args);

		// SwingWindow window = new SwingWindow();

		ChatGUIAdapter csc = ChatGUIAdapter.getInstance();

		ChatJmsAdapter CJA = ChatJmsAdapter.getInstance();
		//
		// // CJA.setMessageReceiver(CSC);
		//
		// String brokerUri;
		//
		// String localConnection = "tcp://localhost:61616";
		// brokerUri = localConnection;
		//
		// CJA.connectToServer(brokerUri);
		// // TODO Later CJA.requestParticipian("xy");
		// CJA.register("name", "password");
		//
		// // CJA.logout();
		//
		// // CJA.login("xy", "xy");
		//
		// // CJA.logout();

	}

}
