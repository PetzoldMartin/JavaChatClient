/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice;

import java.util.HashMap;

/**
 * Klasse zur Erzeugung und Verwaltung von User Objekten im System
 * 
 * @author georg beier
 * 
 */
public class UserFactory {

	/** Ablage für registrierte User Objekte */
	private HashMap<String, User> users = new HashMap<>();

	/**
	 * Einige User automatisch zu Testzwecken anlegen
	 */
	{
		users.put("schlapp", new User("schlapp", "hut"));
		users.put("hut", new User("hut", "schnur"));
		users.put("muetze", new User("muetze", "cap"));
	}

	/**
	 * User Objekt aus der Ablage holen, wenn Name und Passwort stimmen
	 * 
	 * @param uname
	 *            Name
	 * @param pword
	 *            Passwort
	 * @return vorhanden User oder null bei fehlerhaften Eingaben
	 */
	public User createUser(String uname, String pword) {
		if (users.containsKey(uname)
				&& users.get(uname).authenticate(pword)) {
			return users.get(uname);
		} else {
			return null;
		}
	}

	/**
	 * Neues User Objekt erzeugen, in Ablage speichern und zurückgeben.
	 * 
	 * @param uname
	 *            Name, muss eindeutig sein
	 * @param pword
	 *            Passwort
	 * @return User Objekt oder null
	 */
	public User registerUser(String uname, String pword) {
		if (!users.containsKey(uname)) {
			User p = new User(uname, pword);
			users.put(uname, p);
			return p;
		} else {
			return null;
		}
	}

	/**
	 * User Objekt (oder Subklasse von User) registrieren
	 * 
	 * @param p
	 *            Objekt, das registriert werden soll
	 * @return true bei Erfolg
	 */
	protected boolean register(User p) {
		if (!users.containsKey(p.getUsername())) {
			users.put(p.getUsername(), p);
			return true;
		}
		return false;
	}
}
