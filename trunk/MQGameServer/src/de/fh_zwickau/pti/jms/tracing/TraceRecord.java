/**
 * 
 */
package de.fh_zwickau.pti.jms.tracing;

import java.io.Serializable;
import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * Capture a single point for tracing in a human readable form
 * 
 * @author georg beier
 * 
 */
public class TraceRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	private long timestamp;
	private String clazz;
	private String objId;
	private String fromState;
	private String toState;
	private String event;
	private HashMap<String, String> properties = new HashMap<>(4);

	/**
	 * initialize record
	 * 
	 * @param cl
	 *            traced class (->fromState chart)
	 * @param obj
	 *            traced object
	 * @param fromSt
	 *            actual fromState before event handling
	 * @param toSt
	 *            resulting fromState after event handling
	 * @param evt
	 *            event kind
	 * @param msg
	 *            full event message (optional)
	 */
	public TraceRecord(String cl, String obj, String fromSt, String toSt,
			String evt, Message msg) {
		timestamp = System.nanoTime();
		clazz = cl;
		objId = obj;
		fromState = fromSt;
		toState = toSt;
		event = evt;
		try {
			properties.put(MessageHeader.MsgKind.toString(),
					msg.getStringProperty(MessageHeader.MsgKind.toString()));
			properties.put(MessageHeader.AuthToken.toString(),
					msg.getStringProperty(MessageHeader.AuthToken.toString()));
			if (msg instanceof TextMessage)
				properties.put("body", ((TextMessage) msg).getText());
		} catch (JMSException e) {
			Logger.getLogger(getClass()).log(Level.ERROR, "tracing error", e);
		}
	}

	@Override
	public String toString() {
		return "[" + clazz + ", (" + objId + "), " + fromState + " --<" + event
				+ ">-> " + toState + " at " + timestamp + "ns]";
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getClazz() {
		return clazz;
	}

	public String getObjId() {
		return objId;
	}

	public String getToState() {
		return toState;
	}

	public String getEvent() {
		return event;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public String getFromState() {
		return fromState;
	}
}
