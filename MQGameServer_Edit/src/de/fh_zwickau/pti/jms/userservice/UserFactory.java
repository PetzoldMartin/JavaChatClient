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
// public class UserFactory {
// /** Ablage fÃ¼r registrierte User Objekte */
// private static DbHibernate db;
// /** Object zum Zugriff auf die Datenbank */
// private DaoHibernate<User> userDao;
//
// @Override
// protected void finalize() throws Throwable {
// DbHibernate.closeDatabase();
// super.finalize();
// }
//
// /**
// * erstellt DB verbindung;
// */
// public UserFactory() {
//
// // die Datenbank erzeugen, bzw. gleich Verbindung öffnen
// db = new DbHibernate();
// // Data Access Object für User
// userDao = new DaoHibernate<User>(User.class, db);
//
// // /**
// // * Einige User automatisch zu Testzwecken anlegen
// // */
// // {
// // users.put("schlapp", new User("schlapp", "hut"));
// // users.put("hut", new User("hut", "schnur"));
// // users.put("muetze", new User("muetze", "cap"));
// // }
//
// }
//
// /**
// * Neues User Objekt erzeugen, in Ablage speichern und zurÃ¼ckgeben.
// *
// * @param uname
// * Name, muss eindeutig sein
// * @param pword
// * Passwort
// * @return User, if not exist before call
// * @return null, if exist before call
// */
// public synchronized User registerUser(String uname, String pword) {
// if (this.createUser(uname, pword) == null) {
//
// userDao = new DaoHibernate<User>(User.class, db);
// User p = new User(uname, pword);
// // was passiert bei Verbindungsabbruch zur Datenbank??? TODO
// userDao.save(p);
// userDao.closeSession();
// return p;
// } else {
// return null;
// }
// }
//
// /**
// * User Objekt aus der Ablage holen, wenn Name und Passwort stimmen
// *
// * @param uname
// * Name
// * @param pword
// * Passwort
// * @return vorhanden User oder null bei fehlerhaften Eingaben
// */
// public synchronized User createUser(String uname, String pword) {
// // der erste User aus der Datenbank, der mit den Parametern
// // übereinstimmt
// userDao = new DaoHibernate<User>(User.class, db);
// List<User> list = userDao.findByExample(new User(uname, pword));
// User user;
// if (!list.isEmpty()) {
// user = list.get(0);
// } else {
// user = null;
// }
// userDao.closeSession();
// return user;
// }
//
// /**
// * User Objekt (oder Subklasse von User) registrieren und persistent in DB
// * ablegen
// *
// * @param p
// * Objekt, das registriert werden soll
// * @return true bei Erfolg, sonst false, da Objekt schon registriert
// */
// // protected synchronized boolean register(User p) {
// // test TODO
// public synchronized boolean register(User p) {
// userDao = new DaoHibernate<User>(User.class, db);
// List<User> list = userDao.findByExample(p);
// if (list != null && userDao.findByExample(p).size() == 0) {
// userDao.save(p);
// return true;
// }
// userDao.closeSession();
// return false;
// }
//
// // test TODO
// public synchronized void deleteAllUser() {
// userDao = new DaoHibernate<User>(User.class, db);
// userDao.deleteAll();
// userDao.closeSession();
// }
//
// }
//
public class UserFactory {

	/** Ablage fÃ¼r registrierte User Objekte */
	private final HashMap<String, User> users = new HashMap<>();
	/** Ablage fÃ¼r registrierte User Objekte */
	private static DbHibernate db;
	/** Object zum Zugriff auf die Datenbank */
	private DaoHibernate<User> userDao;

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		DbHibernate.closeDatabase();

	}

	/**
	 * erstellt DB verbindung;
	 */
	public UserFactory() {

		// die Datenbank erzeugen, bzw. gleich Verbindung öffnen
		db = new DbHibernate();
		// Data Access Object für User
		userDao = new DaoHibernate<User>(User.class, db);
		loadFromDB();
		//
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
	public synchronized User createUser(String uname, String pword) {
		System.err.println("createUser");
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
	public synchronized User registerUser(String uname, String pword) {
		System.err.println("registerUser");
		if (!users.containsKey(uname)) {
			User p = new User(uname, pword);
			users.put(p.getUsername(), p);
			this.saveUser(p);
			return p;
		} else {
			return null;
		}
	}

	protected synchronized void saveUser(User p) {

		userDao = new DaoHibernate<User>(User.class, db);
		try {
			userDao.save(p);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userDao.closeSession();
		}
	}

	private synchronized void loadFromDB() {
		System.out.println("loadFromDB");
		userDao = new DaoHibernate<User>(User.class, db);
		List<User> list = null;
		try {
			list = userDao.fetchAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userDao.closeSession();
		}
		if (list != null) {
			users.clear();
			for (User user : list) {
				users.put(user.getUsername(), user);
				System.out.println("Nutzer " + user.getUsername() + " geladen");
			}
		}
	}

	/**
	 * User Objekt (oder Subklasse von User) registrieren
	 * 
	 * @param p
	 *            Objekt, das registriert werden soll
	 * @return true bei Erfolg
	 */
	protected synchronized boolean register(User p) {
		System.err.println("register");
		// loadFromDB();
		if (!users.containsKey(p.getUsername())) {
			users.put(p.getUsername(), p);
			saveUser(p);
			return true;
		}
		return false;
	}

	/**
	 * TODO dienst nur zum Test per JUnit
	 */
	public synchronized void deleteAllUser() {
		System.out.println(" LÖSCHE ALLES!!!)");
		HashMap<String, User> m = new HashMap<String, User>();
		users.putAll(m);
		userDao = new DaoHibernate<User>(User.class, db);
		try {
			userDao.deleteAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userDao.closeSession();
		}

	}
}
