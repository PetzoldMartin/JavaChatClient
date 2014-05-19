package de.fh_zwickau.pti.jms.userservice.chat;

import de.fh_zwickau.pti.jms.userservice.User;
import de.fh_zwickau.pti.jms.userservice.UserFactory;

/**
 * erzeugt Chatter Objekte statt User-Objekten
 * 
 * @author georg beier
 * 
 */
public class ChatterFactory extends UserFactory {

	@Override
	public User registerUser(String uname, String pword) {
		User c = new Chatter(uname, pword);
		if (register(c))
			return c;
		else
			return null;
	}
}
