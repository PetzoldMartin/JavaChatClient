package demo;

import java.util.HashMap;

import javax.jms.BytesMessage;

import de.fh_zwickau.informatik.stompj.Connection;
import de.fh_zwickau.informatik.stompj.ErrorMessage;
import de.fh_zwickau.informatik.stompj.StompMessage;
import de.fh_zwickau.informatik.stompj.MessageHandler;
import de.fh_zwickau.informatik.stompj.StompJException;
import de.fh_zwickau.informatik.stompj.internal.MessageImpl;


public class BytesStompInvoker {

	private Connection stompConnection;
	private static String stompQ = "/queue/binq";
	private static String stompReply = "/temp-queue/xxx";

	private MessageHandler m0 = new MessageHandler() {

		@Override
		public void onMessage(StompMessage message) {
			String[] keys = message.getPropertyNames();
			for (String key : keys) {
				System.out.println(key + ": " + message.getProperty(key));
			}
			System.out.println("Queue: " + message.getDestination());
			System.out.println("ID: " + message.getMessageId() + "\nContent: |"
				+ message.getContentAsBytes().length + "|");
			byte[] load = message.getContentAsBytes();
			System.out.print("bytes received: [");
			for (int i = 0; i < load.length; i++) {
				System.out.print(load[i] + ", ");
			}
			System.out.println("]\n");
		}
	};

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		BytesStompInvoker echoInvoker = new BytesStompInvoker();
		echoInvoker.stomp();
	}

	private void stomp() {
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("reply-to", stompReply);
		stompConnection = new Connection("192.168.111.102", 61613, "sys", "man");
		ErrorMessage emsg;
		try {
			emsg = stompConnection.connect();
			System.out.println("Connect error message: " + emsg);
			stompConnection.addMessageHandler(stompReply, m0);
			stompConnection.subscribe(stompReply, true);

			byte[] load = new byte[409600];
			for (int i = 0; i < load.length; i++) {
				load[i] = (byte) i;
			}

			MessageImpl message;
			while (true) {
				String id = "" + System.currentTimeMillis();
				props.put("correlation-id", id);
				props.put("content-length", "" + load.length);
				message = new MessageImpl();
				message.setContent(load);
				message.setProperties(props);
				stompConnection.send(message, stompQ);
				Thread.sleep(5000);
			}
		} catch (StompJException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
