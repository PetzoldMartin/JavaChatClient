package Test;
import gui.ChatSwingClient;
import gui.SwingWindow;
import States.StatesClasses.NotLoggedIn;


public class Test {

	public static void main(final String[] args) throws Exception {
		Runnable x= new Runnable() {
			public void run() {
				try {
					Client_with_server_Start.main(args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		Runnable y= new Runnable() {
			public void run() {
				try {
					Only_client.main(args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		x.run();y.run();
	}

}
