package Test;

import gui.ChatSwingClient;

import javax.jms.JMSException;

import de.fh_zwickau.pti.jms.userservice.AuthenticationServer;
import de.fh_zwickau.pti.jms.userservice.chat.ChatServer;
import messaging.ChatJmsAdapter;

public class TestAismael {

	public static void main(String[] args) throws Exception {
		
		AuthenticationServer.main(args);
		ChatServer.main(args);
		
		ChatJmsAdapter CJA=ChatJmsAdapter.getInstance();
		CJA.setMessageReceiver(ChatSwingClient.getInstance());

		String brokerUri;
		if (args.length == 0) {
			String localConnection = "tcp://localhost:61616";
			brokerUri = localConnection;
		} else {
			brokerUri = args[0];
		}
		CJA.connectToServer(brokerUri);
		CJA.register("xy", "xy");
		CJA.register("xy", "xy");
		CJA.logout();
		

	}

}
