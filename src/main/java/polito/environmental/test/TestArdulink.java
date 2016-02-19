package polito.environmental.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.zu.ardulink.Link;
import org.zu.ardulink.gui.SerialConnectionPanel;
import org.zu.ardulink.gui.customcomponents.SignalButton;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

import sun.awt.HorizBagLayout;
import sun.awt.VerticalBagLayout;

public class TestArdulink extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(NimbusLookAndFeel.class.getCanonicalName());
					TestArdulink frame = new TestArdulink();
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
	public TestArdulink() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		final SerialConnectionPanel serialConnectionPanel = new SerialConnectionPanel();

		final JButton button = new JButton("Connect");

		contentPane.add(serialConnectionPanel, BorderLayout.NORTH);

		JPanel connectPanel = new JPanel(new HorizBagLayout());

		connectPanel.add(button);

		JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Link.getDefaultInstance().disconnect();
			}
		});
		connectPanel.add(btnDisconnect);

		contentPane.add(connectPanel, BorderLayout.CENTER);

		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Link.getDefaultInstance().disconnect();
			}
		});

		button.addActionListener(
				new ActionListener() {

					public void actionPerformed(ActionEvent e) {

						String comPort = serialConnectionPanel.getConnectionPort();
						String baudRateS = serialConnectionPanel.getBaudRate();

						try {
							int baudRate = Integer.parseInt(baudRateS);

							Link.getDefaultInstance().connect(comPort, baudRate);
						}
						catch(Exception ex) {
							ex.printStackTrace();
							String message = ex.getMessage();
							if(message == null || message.trim().equals("")) {
								message = "Generic Error on connection";
							}
							JOptionPane.showMessageDialog(button, message, "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});

		JPanel panel = new JPanel(new VerticalBagLayout());

		panel.add(new SignalButton());

		contentPane.add(panel, BorderLayout.SOUTH);

	}

}
