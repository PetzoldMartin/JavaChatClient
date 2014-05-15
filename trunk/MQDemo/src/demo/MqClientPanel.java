package demo;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;

public class MqClientPanel extends JPanel implements TextMessageListener {

	private Hub mqConnector = new Hub();
	private JScrollPane scrollPane;
	private JTextArea msgTextArea;
	private JTextArea inputTextArea;
	private JLabel lblEingabe;
	private JLabel lblEmpfangen;
	private JButton btnSenden;
	private JLabel lblBrokerUrl;
	private JTextField urlTextField;
	private JButton btnConnect;

	/**
	 * Create the panel.
	 */
	public MqClientPanel() {
		initComponents();
		myInit();
	}

	private void initComponents() {
		setLayout(new MigLayout("", "[][grow]", "[][][][][grow]"));
		
		lblBrokerUrl = new JLabel("Broker URL:");
		add(lblBrokerUrl, "cell 0 0,alignx trailing");
		
		urlTextField = new JTextField();
		urlTextField.setText("tcp://localhost:61616");
		add(urlTextField, "cell 1 0,growx");
		urlTextField.setColumns(30);
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				do_btnConnect_actionPerformed(e);
			}
		});
		add(btnConnect, "cell 1 1");

		lblEingabe = new JLabel("Eingabe:");
		add(lblEingabe, "cell 0 2,alignx right,aligny top");

		inputTextArea = new JTextArea();
		inputTextArea.setEnabled(false);
		inputTextArea.setColumns(40);
		inputTextArea.setRows(4);
		add(inputTextArea, "cell 1 2,growx,aligny top");

		btnSenden = new JButton("Senden");
		btnSenden.setEnabled(false);
		btnSenden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				do_btnSenden_actionPerformed(e);
			}
		});
		add(btnSenden, "cell 1 3,alignx right");

		lblEmpfangen = new JLabel("Empfangen: ");
		add(lblEmpfangen, "cell 0 4,alignx right,aligny top");

		scrollPane = new JScrollPane();
		add(scrollPane, "cell 1 4,grow");

		msgTextArea = new JTextArea();
		msgTextArea.setEditable(false);
		scrollPane.setViewportView(msgTextArea);
	}

	private void myInit() {
		try {
			mqConnector.setMsgDisplay(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMqConnector(Hub mqConnector) {
		this.mqConnector = mqConnector;
		mqConnector.setMsgDisplay(this);
	}
	
	public void setBrokerUrl(String url) {
		urlTextField.setText(url);
	}

	public void shutdown() {
		try {
			mqConnector.shutdown();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {}
	}

	@Override
	public void onMessage(String msg) {
		msg += msgTextArea.getText();
		msgTextArea.setText(msg);
	}

	protected void do_btnSenden_actionPerformed(ActionEvent e) {
		String sendText = inputTextArea.getText();
		if (sendText != null && sendText.length() > 0) {
			try {
				mqConnector.send(sendText);
				inputTextArea.setText("");
			} catch (JMSException e1) {
				String ex = e1.getMessage();
				String out = ex + "\n^^^^^^^^^^^^^\n" + msgTextArea.getText();
				msgTextArea.setText(out);
			}
		}
	}
	protected void do_btnConnect_actionPerformed(ActionEvent e) {
		try {
			mqConnector.setUrl(urlTextField.getText());
			mqConnector.startup();
		} catch (Exception e1) {
			String ex = e1.getMessage();
			String out = ex + "\n^^^^^^^^^^^^^\n";
			msgTextArea.setText(out);
			return;
		}
		btnConnect.setEnabled(false);
		urlTextField.setEnabled(false);
		btnSenden.setEnabled(true);
		inputTextArea.setEnabled(true);
	}
}
