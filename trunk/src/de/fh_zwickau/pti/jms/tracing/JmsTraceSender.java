/**
 * 
 */
package de.fh_zwickau.pti.jms.tracing;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * send trace via jms destination
 * @author georg beier
 *
 */
public class JmsTraceSender implements TraceSender {

	private MessageProducer producer;
	private Session session;

	public JmsTraceSender(MessageProducer p, Session s) {
		producer = p;
		session = s;
	}
	
	/* (non-Javadoc)
	 * @see de.fh_zwickau.pti.jms.tracing.TraceSender#sendTrace(de.fh_zwickau.pti.jms.tracing.TraceRecord)
	 */
	@Override
	public void sendTrace(TraceRecord record) {
		try {
			ObjectMessage objMsg = session.createObjectMessage(record);
			producer.send(objMsg);
		} catch (JMSException e) {
			Logger.getLogger(getClass()).log(Level.ERROR, "tracing error", e);
		}
	}

}
