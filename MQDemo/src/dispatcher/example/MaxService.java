/**
 * 
 */
package dispatcher.example;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * @author georg beier
 * 
 */
public class MaxService extends BasicService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dispatcher.example.BasicService#serviceCalculation(java.lang.Double[],
	 * javax.jms.MapMessage)
	 */
	@Override
	protected void serviceCalculation(Double[] numbers, MapMessage serviceReply) {
		double max = 0.;
		for (Double num : numbers) {
			max = Math.max(max, num);
		}
		log.debug("Max berechnet: " + max);
		try {
			serviceReply.setDouble("max", max);
		} catch (JMSException e) {
			log.error(e.toString());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MaxService().startService("max");
	}

}
