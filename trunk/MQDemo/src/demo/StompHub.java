package demo;

import javax.jms.JMSException;

import de.fh_zwickau.informatik.stompj.Connection;
import de.fh_zwickau.informatik.stompj.ErrorMessage;
import de.fh_zwickau.informatik.stompj.StompMessage;
import de.fh_zwickau.informatik.stompj.MessageHandler;


public class StompHub extends Hub implements MessageHandler {

	private Connection stompConnection;
	protected boolean isConnected;

	@Override
	public void startup() throws Exception {
		String[] urlParts = url.split("[:/,?]+\\s*");
		if (urlParts.length < 3) {
			String u = "";
			for (String part : urlParts) {
				u += part + ", ";
			}
			throw new Exception(u);
		}
		stompConnection = new Connection(urlParts[1],
			Integer.parseInt(urlParts[2]), "sys", "man");
		ErrorMessage emsg = stompConnection.connect();
		System.out.println(emsg);
		String stopic = "/topic/" + topicName;
		stompConnection.addMessageHandler(stopic, this);
		stompConnection.subscribe(stopic, true);
	}

	@Override
	public void shutdown() throws JMSException {
		String stopic = "/topic/" + topicName;
		stompConnection.unsubscribe(stopic);
		stompConnection.removeMessageHandlers(stopic);
		stompConnection.disconnect();
	}

	@Override
	public void send(String msgText) throws JMSException {
		stompConnection.send(msgText, "/topic/" + topicName);
	}

	@Override
	public void onMessage(StompMessage message) {
		if (msgDisplay != null)
			msgDisplay.onMessage(message.getContentAsString());
	}
}
