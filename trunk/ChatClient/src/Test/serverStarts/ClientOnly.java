package Test.serverStarts;

import gui.SwingWindow;

import javax.swing.SwingUtilities;

public class ClientOnly {

	public static void main(final String[] args) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
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
				SwingWindow window = new SwingWindow(user, password);
				// try {
				// ChatJmsAdapter jmsAdapter = ChatJmsAdapter.getInstance();
				// jmsAdapter.register(user, password);
				// } catch (JMSException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}
		});
	}
}
