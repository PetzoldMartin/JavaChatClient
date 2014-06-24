package test_fromGroup13;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fh_zwickau.pti.jms.userservice.User;
import de.fh_zwickau.pti.jms.userservice.UserFactory;

/**
 * für testfälle müssen zugriffe in USerFactory geändert werden !! TODO
 * 
 * @author Peter
 * 
 */
public class UserFactoryTest {
	static UserFactory userFactory;
	User userTestRegisterUser;
	User userTestCreateUser;
	User userTestRegister;
	String userPassword = "userPassword";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		userFactory = new UserFactory();

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// TODO not used
	}

	@Before
	public void setUp() throws Exception {
		userFactory.deleteAllUser();
		userTestRegisterUser = new User("userTestRegisterUser()", userPassword);
		userTestCreateUser = new User("usertestCreateUser()", userPassword);
		userTestRegister = new User("usertestRegister()", userPassword);
	}

	@After
	public void tearDown() throws Exception {
		// TODO not used
	}

	@Test
	public void testUserFactory() {
		// TODO not used
	}

	@Test
	public void testRegisterUser() {
		System.out.println("testRegisterUser() : START>>>>>>");
		// neuen nutzer in DB schreiben
		User testUser1 = userFactory.registerUser(
				userTestRegisterUser.getUsername(), userPassword);
		System.out.println("!!!!!" + testUser1);
		assertTrue(userTestRegisterUser.getUsername().equals(
				testUser1.getUsername()));
		assertTrue(userTestRegisterUser.getPwhash()
				.equals(testUser1.getPwhash()));
		System.out.println("testRegisterUser() : ENDE <<<<<<<<");
	}

	@Test
	public void testCreateUser() {
		System.out.println("testCreateUser() : START>>>>>>");
		// test ob der einzuloggende nutzer wirklich in DB steht, rükgabe dieses
		// neuen nutzer in db schreiben

		userFactory.register(userTestCreateUser);
		User testUser2 = userFactory.createUser(
				userTestCreateUser.getUsername(), userPassword);
		System.out.println("\ntestUser1 " + testUser2.getUsername());
		System.out.println("\ntestUser2 " + userTestCreateUser.getUsername());
		assertTrue(testUser2.getUsername().equals(
				userTestCreateUser.getUsername()));
		assertTrue(testUser2.getPwhash().equals(userTestCreateUser.getPwhash()));
		System.out.println("testCreateUser() : ENDE <<<<<<<<");
	}

	@Test
	public void testRegister() {
		System.out.println("\ntestRegister() : START>>>>>>");
		// schreibe in DB über factory methode
		assertTrue(userFactory.register(userTestRegister));
		System.out.println("\ntestRegister() : ENDE <<<<<<<<");
	}

}