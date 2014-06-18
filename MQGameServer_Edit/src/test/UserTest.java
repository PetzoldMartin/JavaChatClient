package test;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import architecture.hibernate.DaoHibernate;
import architecture.hibernate.DbHibernate;
import de.fh_zwickau.pti.jms.userservice.User;

public class UserTest {
	private static DbHibernate db;
	private DaoHibernate<User> userDao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = new DbHibernate();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DbHibernate.closeDatabase();
	}

	@Before
	public void setUp() throws Exception {
		userDao = new DaoHibernate<User>(User.class, db);
	}

	@After
	public void tearDown() throws Exception {
		userDao.closeSession();
	}

	@Test
	public void test() {

		// vorgeplänkel
		User userA = new User("userA", "pwUserA");
		// >>>> folgende Zeile geht nur wenn in datenbank
		// schon einmal userA geschrieben wurde
		userA = userDao.findByExample(userA).get(0);
		// <<<
		assertTrue("User A Test: ", userA.getUsername().equals("userA"));
		assertTrue("passworthash stimmt nicht", userA.authenticate("pwUserA"));

		userDao.save(userA);

		System.out.println("erster gespeicherter Nutzer: "
				+ userDao.findByExample(userA).get(0));
		User userNew = new User("userA", "pwUserA");
		User userB = userDao.findByExample(userNew).get(0);
		System.out.println("userAName : " + userA.getUsername()
				+ " userBName : "
				+ userB.getUsername());
		System.out.println("userAID : " + userA.getId() + " userBID : "
				+ userB.getId());
		System.out.println("userAPW : " + userA.getPwhash() + " userB : "
				+ userB.getPwhash());

	}

}
