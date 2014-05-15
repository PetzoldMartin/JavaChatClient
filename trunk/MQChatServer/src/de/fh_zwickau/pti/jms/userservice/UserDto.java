/**
 * 
 */
package de.fh_zwickau.pti.jms.userservice;

import java.io.Serializable;

/**
 * data transfer objekt for User and Chatter objects
 * @author georg beier
 *
 */
public class UserDto implements Serializable {

	private String username;
	private boolean chatter = false;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isChatter() {
		return chatter;
	}

	public void setChatter(boolean chatter) {
		this.chatter = chatter;
	}
}
