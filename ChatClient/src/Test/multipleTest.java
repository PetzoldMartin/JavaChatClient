package Test;

import Test.serverStarts.ClientOnly;

public class multipleTest {

	public static void main(String[] args) throws Exception {
		Test.main(args);

		ClientOnly.main(new String[] { "user2", "pw" });

	}
}
