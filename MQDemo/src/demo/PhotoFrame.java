package demo;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class PhotoFrame extends JFrame {

	private JPanel contentPane;
	private static PhotoFrame instance;
	private Image image;
	private JPanel imgPanel;
	private JScrollPane scrollPane;

	public synchronized static PhotoFrame getInstance() {
		if (instance == null) {
			instance = new PhotoFrame();
			instance.setVisible(true);
		}
		return instance;
	}

	/**
	 * Create the frame.
	 */
	public PhotoFrame() {
	
		initComponents();
	}
	
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 918, 718);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		imgPanel = new JPanel();
		scrollPane.setViewportView(imgPanel);
		imgPanel.setPreferredSize(new Dimension(640, 640));
		imgPanel.setMinimumSize(new Dimension(640, 640));
		imgPanel.setBackground(Color.YELLOW);
		imgPanel.setLayout(new BorderLayout(0, 0));
	}

	public Image showImage(byte[] imgData) {
		Toolkit toolkit = getToolkit();
		image = toolkit.createImage(imgData);
		Graphics graphics = imgPanel.getGraphics();
		graphics.drawImage(image, 0, 0, imgPanel);
		return image;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(image != null && imgPanel != null) {
			imgPanel.getGraphics().drawImage(image, 0, 0, imgPanel);
		}
	}

}
