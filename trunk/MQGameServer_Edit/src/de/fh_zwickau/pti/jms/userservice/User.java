/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.jms.Destination;
import javax.jms.Message;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

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
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "User", discriminatorType = DiscriminatorType.STRING)
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	// private final String username;
	private String username;
	private String pwhash;

	// nur zum testen:
	public String getPwhash() {
		return pwhash;
	}

	public Long getId() {
		return id;
	}

	@Transient
	private static MessageDigest md;
	@Transient
	private Destination replyDestination;
	/**
	 * implementation of ..1 association end across jms by a unique destination
	 */
	@Transient
	private Destination clientDestination;

	public User() {
		// TODO Auto-generated constructor stub
	}

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
