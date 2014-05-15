package demo;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Hub implements MessageListener {

	private Connection connection;
	private Session session;
	private MessageProducer publisher;
	private Topic topic;
	protected String topicName = "topictest.messages";
	//wird derzeit nicht benötigt
//	private Destination control;
	
	protected TextMessageListener msgDisplay;

	/**
	 * url des StompMessage Brokers, default lokal
	 */
	protected String url = "";

	public void startup() throws Exception {
		// Factory holen
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
		// connection zum StompMessage Broker aufbauen
		connection = factory.createConnection();
		// Session öffnen
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// Destination anlegen - hier ein Topic für broadcasts
		topic = session.createTopic(topicName);
//		control = session.createQueue("topictest.control");
		
		// Publisher auf dem Topic anlegen
		publisher = session.createProducer(topic);
		publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		// Consumer auf dem dem Topic anlegen und
		// MessageListener hinzufügen
		session.createConsumer(topic).setMessageListener(this);
		// connection starten
		connection.start();
	}
	
	/**
	 * Sende Text-StompMessage auf das Topic
	 * @param msgText StompMessage Inhalt
	 * @throws JMSException
	 */
	public void send(String msgText) throws JMSException {
		publisher.send(session.createTextMessage(msgText));
	}
	
	/**
	 * schließt die connection
	 * @throws JMSException
	 */
	public void shutdown() throws JMSException {
		connection.stop();
		connection.close();
	}

	
	@Override
	/**
	 * MessageListener Methode, die aufgerufen wird, wenn
	 * eine neue StompMessage auf dem Topic eingeht. 
	 * Leitet die StompMessage an die GUI weiter. 
	 */
	public void onMessage(Message arg0) {
		if(msgDisplay != null) msgDisplay.onMessage(processMessage(arg0));
//		System.out.println("received message");
	}
	
	private String processMessage(Message msg) {
		String out;
		if (msg instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) msg;
			try {
				out = textMessage.getText() + "\n==========\n";
			} catch (JMSException e) {
				out = e.getMessage() + "\n^^^^^^^^^^\n";
			}
		} else {
			out = msg.toString() + "\n??????????\n";
		}
		return out;
	}

	public TextMessageListener getMsgDisplay() {
		return msgDisplay;
	}

	public void setMsgDisplay(TextMessageListener msgDisplay) {
		this.msgDisplay = msgDisplay;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
