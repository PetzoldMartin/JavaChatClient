/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice.chat;

import de.fh_zwickau.pti.jms.userservice.JmsReference;

/**
 * extends the general jms reference with chatter specific information
 * 
 * @author georg beier
 * 
 */
public class ChatterReference extends JmsReference {
	public String nickname;
}
