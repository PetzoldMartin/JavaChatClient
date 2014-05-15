/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.jms.Destination;
import javax.jms.Message;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Implementierung der Basisfunktionalität für einen User, der über username
 * identifiziert und über pwhash authentifiziert wird.<br>
 * Objekte sind serialisierbar und können daher als message body einer JMS
 * ObjectMessage übertragen werden.
 * 
 * @author georg beier
 * 
 */
@SuppressWarnings("serial")
public class User implements Serializable {

	private static MessageDigest md;
	private String username;
	private String pwhash;
	private Destination replyDestination;
	/**
	 * implementation of ..1 association end across jms by a unique destination
	 */
	private Destination clientDestination;

	/**
	 * erzeugt ein MessageDigest Objekt
	 */
	private static synchronized MessageDigest getDigest() {
		if (md == null) {
			try {
				md = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				Logger.getRootLogger().log(Level.ERROR,
						"could not create MessageDigest");
			}
		}
		return md;
	}

	/**
	 * berechnet einen möglichst sicheren Passworthash durch wiederholte
	 * Anwendung der digest() Methode
	 * 
	 * @param pwin
	 *            Klartext, der gehasht werden soll
	 * @param loop
	 *            Anzahl der Wiederholungen der Hash-Funktion
	 * @return hash des Eingabestrings
	 */
	public static String hash(String pwin, int loop) {
		byte[] input = pwin.getBytes();
		for (int i = 0; i < loop; i++) {
			input = getDigest().digest(input);
		}
		return makeToken(input);
	}

	/**
	 * byte[] -> String
	 * 
	 * @param input
	 *            arbitrary byte array
	 * @return string of hex numbers
	 */
	private static String makeToken(byte[] input) {
		StringBuilder buf = new StringBuilder();
		for (byte b : input) {
			buf.append(String.format("%02x", b));
		}
		return buf.toString();
	}

	/**
	 * Neues User-Objekt anlegen. Der Passworthash wird aus uname und pword
	 * erzeugt.
	 * 
	 * @param uname
	 *            login Name
	 * @param pword
	 *            Klartext-Passwort
	 */
	public User(String uname, String pword) {
		username = uname;
		if (pword != null && pword.length() != 0)
			pwhash = hash(uname + pword, 100);
		else
			pwhash = "";
	}

	/**
	 * Authentifiziere User
	 * 
	 * @param uname
	 *            login Name
	 * @param pword
	 *            Klartext-Passwort
	 * @return true bei Erfolg
	 */
	public boolean authenticate(String pword) {
		return (hash(username + pword, 100).equals(pwhash));
	}

	/**
	 * whatever has to be done to process a message
	 * 
	 * @param message
	 *            incoming jms message
	 */
	public boolean processMessage(Message message) {
		return false;

	}

	/**
	 * erzeuge einen temporären möglichst nicht vorhersagbaren
	 * Authentifizierungstoken
	 * 
	 * @return der neue Token
	 */
	public String getToken() {
		return hash(username + System.currentTimeMillis(), 1);
	}

	public String getUsername() {
		return username;
	}

	public Destination getReplyDestination() {
		return replyDestination;
	}

	public void setReplyDestination(Destination replyDestination) {
		this.replyDestination = replyDestination;
	}

	protected void setClientDestination(Destination clientDestination) {
		this.clientDestination = clientDestination;
	}

	protected Destination getClientDestination() {
		return clientDestination;
	}

}
