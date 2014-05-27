package Test.serverStarts;

import gui.SwingWindow;

import javax.jms.JMSException;

import messaging.logic.ChatJmsAdapter;

public class ClientOnly {

	public static void main(final String[] args) throws Exception {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				String user, password;
				if (args.length > 1) {
					user = args[0];
					password = args[1];
				} else {
					user = "name";
					password = "password";
				}
				new SwingWindow(user, password);
				try {
					ChatJmsAdapter jmsAdapter = ChatJmsAdapter.getInstance();
					jmsAdapter.register(user, password);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
}
