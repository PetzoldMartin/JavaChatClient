package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import messaging.logic.ChatSwingClient;

public class ChannelBrowser extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JList<String> list;

	/**
	 * Create the frame.
	 */
	public ChannelBrowser(final ChatSwingClient chatClient,
			ArrayList<String> channels) {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		list = new JList<String>();
		list.setListData((String[]) channels.toArray());
		contentPane.add(list);

		JButton btnOk = new JButton("Join Chat");
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chatClient.buttonJoinChatPressed(list.getSelectedValue()
						.toString());
			}
		});
		contentPane.add(btnOk, BorderLayout.SOUTH);

		setVisible(true);
	}

}
