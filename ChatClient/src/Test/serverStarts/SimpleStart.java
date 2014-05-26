package Test.serverStarts;

import messaging.logic.ChatJmsAdapter;
import messaging.logic.ChatSwingClient;

public class SimpleStart {

	public static void main(String[] args) throws Exception {

		// AuthenticationServer.main(args);
		// Thread.sleep(1000);
		// ChatServer.main(args);
		// Thread.sleep(1000);
		ChatSwingClient CSC = ChatSwingClient.getInstance();

		ChatJmsAdapter CJA = ChatJmsAdapter.getInstance();

		// CJA.setMessageReceiver(CSC);

		String brokerUri;

		String localConnection = "tcp://localhost:61616";
		brokerUri = localConnection;

		CJA.connectToServer(brokerUri);
		// TODO Later CJA.requestParticipian("xy");
		CJA.register("Name", "password");

		// CJA.logout();

		// CJA.login("xy", "xy");

		// CJA.logout();

	}

}
