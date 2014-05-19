/**
 * 
 */
package de.fh_zwickau.pti.jms.tracing;

/**
 * send traces in some way
 * 
 * @author georg beier
 * 
 */
public interface TraceSender {

	/**
	 * send a trace record
	 * 
	 * @param record
	 *            a single trace record
	 */
	void sendTrace(TraceRecord record);

}
