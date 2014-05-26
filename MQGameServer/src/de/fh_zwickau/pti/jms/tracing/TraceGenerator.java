/**
 * 
 */
package de.fh_zwickau.pti.jms.tracing;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.fh_zwickau.pti.mqgamecommon.MessageHeader;
import de.fh_zwickau.pti.mqgamecommon.MessageKind;

/**
 * Generate event tracing records to verify asynchronous behaviour
 * 
 * @author georg beier
 *
 */
public class TraceGenerator {
	
	public static final String TRACEQ = "traceq";

	private TraceSender sender;
	private String clazz;
	private String objId;
	
	public TraceGenerator(TraceSender traceSender, String forClass) {
		this(traceSender, forClass, "");
	}
	
	public TraceGenerator(TraceSender traceSender, String forClass, String oid) {
		sender = traceSender;
		clazz = forClass;
		objId = oid;
	}
	
	public void trace(String fromState, String toState, Message msg) {
		String event;
		try {
			event = msg.getStringProperty(MessageHeader.MsgKind.toString());
			sender.sendTrace(new TraceRecord(clazz, objId, fromState, toState, event, msg));
		} catch (JMSException e) {
			Logger.getLogger(getClass()).log(Level.ERROR, "tracing error", e);
		}
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

}
