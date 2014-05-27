package Test;

import Test.serverStarts.ClientOnly;
import de.fh_zwickau.pti.jms.userservice.chat.ChatServer;

public class multipleTest {

	public static void main(String[] args) throws Exception {
		ChatServer.main(args);
		ClientOnly.main(new String[] { "user1", "pw" });
		ClientOnly.main(new String[] { "user2", "pw" });
	}

}
