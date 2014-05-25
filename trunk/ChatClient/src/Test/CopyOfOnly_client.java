package Test;

import java.awt.EventQueue;
import java.util.Random;

import gui.ChatSwingClient;

import javax.jms.JMSException;

import de.fh_zwickau.pti.jms.userservice.AuthenticationServer;
import de.fh_zwickau.pti.jms.userservice.chat.ChatServer;
import messaging.ChatJmsAdapter;

public class CopyOfOnly_client {

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
		Thread.sleep(50);
		CJA.register( Double.toString(Math.random()), "xy");
		Thread.sleep(50);
		//CJA.startChat();


	}

}
