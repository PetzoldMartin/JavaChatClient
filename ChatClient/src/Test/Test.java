package Test;
import gui.SwingWindow;
import States.StatesClasses.NotLoggedIn;


public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Create GUI and run it on a new Thread
		SwingWindow window = new SwingWindow();
		Thread threadWindow = new Thread(window);
		threadWindow.start();
	}

}
