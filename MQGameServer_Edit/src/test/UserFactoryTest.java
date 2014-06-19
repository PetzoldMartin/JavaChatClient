package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.fh_zwickau.pti.jms.userservice.User;
import de.fh_zwickau.pti.jms.userservice.UserFactory;

public class UserFactoryTest {
	UserFactory userFactory = new UserFactory();
	String userName = "userName";
	String userPassword = "password";

	@Test
	public void testUserFactory() {


		User testUser = new User(userName, userPassword);

		User testUser2 = userFactory.createUser(userName, userPassword);
		assertEquals(testUser.getPwhash(), testUser2.getPwhash());
	}

	@Test
	public void testRegisterUser() {
		User testUser = userFactory.registerUser(userName, userPassword);
		assertTrue(userName.equals(testUser.getUsername()));
	}

}