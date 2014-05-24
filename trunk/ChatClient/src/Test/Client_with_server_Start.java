package Test;

import java.awt.EventQueue;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import gui.ChatSwingClient;

import javax.jms.JMSException;

import org.apache.activemq.broker.BrokerService;

import States.StatesClasses.WaitForChat;
import States.StatesClasses.Waiting;
import de.fh_zwickau.pti.jms.userservice.AuthenticationServer;
import de.fh_zwickau.pti.jms.userservice.UserFactory;
import de.fh_zwickau.pti.jms.userservice.chat.ChatServer;
import de.fh_zwickau.pti.jms.userservice.chat.ChatterFactory;
import messaging.ChatJmsAdapter;

public class Client_with_server_Start {

	public static void main(String[] args) throws Exception {
//		AuthenticationServer.main(args);
//		ChatServer.main(args);
		ChatSwingClient CSC = ChatSwingClient.getInstance();

		ChatJmsAdapter CJA = ChatJmsAdapter.getInstance();

		CJA.setMessageReceiver(CSC);

		String brokerUri;

		String localConnection = "tcp://localhost:61616";
		brokerUri = localConnection;

		CJA.connectToServer(brokerUri);
		CJA.register("xx", "xx");
		Thread.sleep(500);
		//CJA.logout();
		//CJA.register("xy", "xy");
		//Thread.sleep(500);
		
		
		//CJA.close();
		Thread.sleep(500);
		CJA.startChat();
		Thread.sleep(500);
		//CJA.leave();
		//CJA.close();
		//TODO Later CJA.requestParticipian("xy");
		//CJA.register("xy", "xy");
		
		//CJA.logout();

		//CJA.login("xy", "xy");

		//CJA.logout();

	}
	

}
