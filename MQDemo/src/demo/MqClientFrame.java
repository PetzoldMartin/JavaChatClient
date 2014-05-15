package demo;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MqClientFrame extends JFrame {

	private JPanel contentPane;
	private MqClientPanel clientPanel;
	private static boolean isStompish;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		if(args.length > 0 && args[0].contains("stomp")) isStompish = true;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MqClientFrame frame = new MqClientFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MqClientFrame() {
		initComponents();
	}
	private void initComponents() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				do_this_windowClosing(e);
			}
		});
		setBounds(100, 100, 481, 471);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		clientPanel = new MqClientPanel();
		if(isStompish) {
			clientPanel.setMqConnector(new StompHub());
			clientPanel.setBrokerUrl("stomp://localhost:61613");
		}
		contentPane.add(clientPanel, BorderLayout.CENTER);
	}

	protected void do_this_windowClosing(WindowEvent e) {
		clientPanel.shutdown();
//		System.exit(1);
	}
}
