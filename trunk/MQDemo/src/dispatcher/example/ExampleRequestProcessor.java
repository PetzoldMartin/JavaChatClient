/**
 * 
 */
package dispatcher.example;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import dispatcher.CommunicationProvider;
import dispatcher.RequestProcessor;

/**
 * @author georg beier
 * 
 */
public class ExampleRequestProcessor extends RequestProcessor {

	private static CommunicationProvider provider = 
		CommunicationProvider.getProvider();
	
	private Double[] numbers;
	private Map<String, Double> results = new HashMap<String, Double>();

	public ExampleRequestProcessor(Message message) throws JMSException {
		super(message);
	}

	@Override
	protected void onDelegating() {
		log.entry("onDelegating");
		if (requestMessage instanceof ObjectMessage) {
			ObjectMessage request = (ObjectMessage) requestMessage;
			try {
				numbers = (Double[]) request.getObject();
				Session session = provider.getSession();
				ObjectMessage message = session.createObjectMessage();
				message.setJMSCorrelationID(request.getJMSCorrelationID());
				message.setObject(numbers);
				message.setJMSReplyTo(serviceReplyQ);
				message.setStringProperty("refId", key);
				provider.getProducer("sum").send(message);
				provider.getProducer("min").send(message);
				provider.getProducer("max").send(message);
			} catch (JMSException e) {
				log.errorException(e);
			}
		} else {
			log.error("unbekannter Message-Typ");
		}
		log.exit("onDelegating");
	}

	@Override
	protected boolean processResultMessage(Message message) {
		if (message instanceof MapMessage) {
			MapMessage result = (MapMessage) message;
			try {
				Enumeration<String> names = result.getMapNames();
				while (names.hasMoreElements()) {
					String key = names.nextElement();
					results.put(key, result.getDouble(key));
					log.debugObject("returned: ", key);
				}
			} catch (JMSException e) {
			}
		}
		boolean result = (results.containsKey("sum")
			&& results.containsKey("min") && results
			.containsKey("max"));
		return result;
	}

	@Override
	protected void onReplying() {
		log.entry("onReplying");
		try {
			Session session = provider.getSession();
			MessageProducer producer = provider.getProducer(null);
			MapMessage message = session.createMapMessage();
			List<Double> lnum = new ArrayList<Double>();
			for (Double dbl : numbers) {
				lnum.add(dbl);
			}
			message.setObject("numbers", lnum);
			double d;
			for (String key : results.keySet()) {
				d = results.get(key);
				message.setDouble(key, d);
			}
			message.setJMSCorrelationID(super.key);
			producer.send(requestMessage.getJMSReplyTo(), message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		log.exit("onReplying");
	}

}
