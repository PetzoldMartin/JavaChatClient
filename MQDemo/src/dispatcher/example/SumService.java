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
public class SumService extends BasicService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dispatcher.example.BasicService#serviceCalculation(java.lang.Double[],
	 * javax.jms.MapMessage)
	 */
	@Override
	protected void serviceCalculation(Double[] numbers, MapMessage serviceReply) {
		double sum = 0.;
		for (Double num : numbers) {
			sum += num;
		}
		log.debug("Sum berechnet: " + sum);
		try {
			serviceReply.setDouble("sum", sum);
		} catch (JMSException e) {
			log.error(e.toString());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SumService().startService("sum");
	}

}
