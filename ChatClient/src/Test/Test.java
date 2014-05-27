package Test;

import Test.serverStarts.ClientOnly;
import de.fh_zwickau.pti.jms.userservice.chat.ChatServer;

public class Test {

	public static void main(String[] args) throws Exception {

		ChatServer.main(args);
		ClientOnly.main(new String[] { "user1", "pw" });
	}

}
