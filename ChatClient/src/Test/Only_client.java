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
		CJA.askForChats();
		Thread.sleep(500);
		CJA.askForChatters();
		Thread.sleep(500);
		CJA.startChat();
		Thread.sleep(500);
		CJA.chat("Hello");
		Thread.sleep(1500);
		System.out.println("attention");
		CJA.invite("xx");
		CJA.invite("xy");
		//CJA.requestParticipian("c282dbfc-d529-4b50-9cdd-8cec951bd128","xx");
		//CJA.logout();

		//CJA.login("xy", "xy");

		//CJA.logout();

	}

}
