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
		
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				ChatJmsAdapter CJA=ChatJmsAdapter.getInstance();
				CJA.setMessageReceiver(ChatSwingClient.getInstance());

				String brokerUri;
				
					String localConnection = "tcp://localhost:61616";
					brokerUri = localConnection;
				
					;
				
				CJA.connectToServer(brokerUri);
			
				try {
					CJA.register("xy", "xy");
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					CJA.login("xy", "xy");
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					CJA.logout();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		

	}

}
