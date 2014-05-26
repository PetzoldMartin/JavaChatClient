package Test.serverStarts;

import messaging.logic.ChatJmsAdapter;
import messaging.logic.ChatSwingClient;

public class Client_Start {

	public static void main(String[] args) throws Exception {
		// AuthenticationServer.main(args);
		// ChatServer.main(args);
		Runnable x = new Runnable() {

			@Override
			public void run() {

				ChatSwingClient CSC = ChatSwingClient.getInstance();

				ChatJmsAdapter CJA = ChatJmsAdapter.getInstance();

				// CJA.setMessageReceiver(CSC);

				String brokerUri;

				String localConnection = "tcp://localhost:61616";
				brokerUri = localConnection;

				CJA.connectToServer(brokerUri);

//				try {
				// CJA.register("xx", "xx");
//				} catch (JMSException e) {
//					// TODO Auto-generated catch block
				// }
				//
				// try {
				// CJA.login("xx", "xx");
				// } catch (JMSException e1) {
				//
				// }
				// try {
				// Thread.sleep(50);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				//
				// // try {
				// // CJA.askForChats();
				// // } catch (JMSException e) {
				// // // TODO Auto-generated catch block
				// // e.printStackTrace();
				// // }
				// try {
				// Thread.sleep(50);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				// // try {
				// // CJA.askForChatters();
				// // } catch (JMSException e) {
				// // // TODO Auto-generated catch block
				// // e.printStackTrace();
				// // }

			}
		};

		x.run();

	}

}
