package Test;

import java.awt.EventQueue;

import gui.ChatSwingClient;

import javax.jms.JMSException;

import de.fh_zwickau.pti.jms.userservice.AuthenticationServer;
import de.fh_zwickau.pti.jms.userservice.chat.ChatServer;
import messaging.ChatJmsAdapter;

public class Only_client {

	public static void main(String[] args) throws Exception {

		//AuthenticationServer.main(args);
		//ChatServer.main(args);
		ChatSwingClient CSC = ChatSwingClient.getInstance();

		ChatJmsAdapter CJA = ChatJmsAdapter.getInstance();

		CJA.setMessageReceiver(CSC);

		String brokerUri;

		String localConnection = "tcp://localhost:61616";
		brokerUri = localConnection;

		CJA.connectToServer(brokerUri);

		CJA.register("xy", "xy");
		//Thread.sleep(500);
		//CJA.startChat();
		Thread.sleep(500);
		CJA.requestParticipian("xx");
		//CJA.logout();

		//CJA.login("xy", "xy");

		//CJA.logout();

	}

}
