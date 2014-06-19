/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice;

import java.util.HashMap;
import java.util.List;

import architecture.hibernate.DaoHibernate;
import architecture.hibernate.DbHibernate;

/**
 * Klasse zur Erzeugung und Verwaltung von User Objekten im System
 * 
 * @author georg beier
 * 
 */
public class UserFactory {
	/** Ablage fÃ¼r registrierte User Objekte */
	private final HashMap<String, User> users;
	private static DbHibernate db;
	private DaoHibernate<User> userDao;

	@Override
	protected void finalize() throws Throwable {
		DbHibernate.closeDatabase();
		super.finalize();
	}
	/**
	 * lädt vorhandene User aus Datenbank TODO
	 */
	public UserFactory() {
		// die LaufzeitUserListe:
		users = new HashMap<String, User>();
		// die Datenbank erzeugen, bzw. gleich Verbindung öffnen
		db = new DbHibernate();
		// Data Access Object für User
		userDao = new DaoHibernate<User>(User.class, db);
		// Die User aus der DB in die Hashmap laden
		List<User> list = userDao.fetchAll();
		System.out.println("users ist mit " + list.size() + " belegt ");
		System.out.println("diese sind:" + list.toString());
		for (User user : list) {
			users.put(user.getUsername(), user);
			System.out.println("Lese User ein, bei " + user.getUsername());
		}

		// /**
		// * Einige User automatisch zu Testzwecken anlegen
		// */
		// {
		// users.put("schlapp", new User("schlapp", "hut"));
		// users.put("hut", new User("hut", "schnur"));
		// users.put("muetze", new User("muetze", "cap"));
		// }

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
		if (users.containsKey(uname) && users.get(uname).authenticate(pword)) {
			return users.get(uname);
		} else {
			return null;
		}
	}

	/**
	 * Neues User Objekt erzeugen, in Ablage speichern und zurÃ¼ckgeben.
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
			userDao = new DaoHibernate<User>(User.class, db);
			userDao.save(p);
			userDao.closeSession();
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
