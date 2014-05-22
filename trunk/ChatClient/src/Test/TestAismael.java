package Test;

import java.awt.EventQueue;

import gui.ChatSwingClient;

import javax.jms.JMSException;

import de.fh_zwickau.pti.jms.userservice.AuthenticationServer;
import de.fh_zwickau.pti.jms.userservice.chat.ChatServer;
import messaging.ChatJmsAdapter;

public class TestAismael {

	public static void main(String[] args) throws Exception {
		
		AuthenticationServer.main(args);
		ChatServer.main(args);
		ChatSwingClient CSC=ChatSwingClient.getInstance();

		System.out.println("xu");

		
				ChatJmsAdapter CJA=ChatJmsAdapter.getInstance();
				System.out.println("xx");
				System.out.println("xy");
				CJA.setMessageReceiver(CSC);
				System.out.println("xx");

				String brokerUri;
				
					String localConnection = "tcp://localhost:61616";
					brokerUri = localConnection;
				
					;
					System.out.println("xx");

					CJA.connectToServer(brokerUri);
			
				
					//CJA.register("xy", "xy");
				
					// TODO Auto-generated catch block
					System.out.println("xx");
				
					//CJA.login("xy", "xy");
				
				
					//CJA.logout();
				
		
	
		

	}

}
