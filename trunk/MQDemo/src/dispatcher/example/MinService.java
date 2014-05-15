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
public class MinService extends BasicService {

	/* (non-Javadoc)
	 * @see dispatcher.example.BasicService#serviceCalculation(java.lang.Double[], javax.jms.MapMessage)
	 */
	@Override
	protected void serviceCalculation(Double[] numbers, MapMessage serviceReply) {
		double min = 0.;
		for (Double num : numbers) {
			min = Math.min(min, num);
		}
		try {
			Thread.sleep(0);
			log.debug("Min berechnet: " + min);
		} catch (InterruptedException e1) {
		}
		try {
			serviceReply.setDouble("min", min);
		} catch (JMSException e) {
			log.error(e.toString());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MinService().startService("min");
	}

}
