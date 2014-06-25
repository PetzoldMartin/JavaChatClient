package test_fromGroup13;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fh_zwickau.pti.jms.userservice.UserFactory;
import de.fh_zwickau.pti.jms.userservice.chat.Chatter;
import de.fh_zwickau.pti.jms.userservice.chat.ChatterFactory;

public class ChatterFactoryTest {
	static UserFactory chatterFactory;
	Chatter chatterTestRegister;
	String userPassword = "userPassword";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		chatterFactory = new ChatterFactory();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		chatterTestRegister = new Chatter("usertestRegister()", userPassword);
	}

	@After
	public void tearDown() throws Exception {
		chatterFactory.deleteAllUser();
	}

	@Test
	public void testRegisterUser() {
		System.out.println("\ntestRegister() : START>>>>>>");
		// schreibe in DB über factory methode
		Chatter chatter = (Chatter) chatterFactory.registerUser(
				chatterTestRegister.getUsername(), userPassword);
		System.err.println(chatter);
		System.err.println(chatterTestRegister);
		assertTrue(chatterTestRegister.equals(chatter));

		chatter = (Chatter) chatterFactory.registerUser(
				chatterTestRegister.getUsername(), userPassword);
		System.err.println(chatter);
		System.err.println(chatterTestRegister);
		assertTrue(chatter == null);
		System.out.println("\ntestRegister() : ENDE <<<<<<<<");
	}

}
