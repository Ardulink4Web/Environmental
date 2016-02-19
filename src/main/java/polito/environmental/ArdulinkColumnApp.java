package polito.environmental;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.zu.ardulink.Link;
import org.zu.ardulink.gui.SerialConnectionPanel;

import polito.environmental.business.Connection;
import polito.environmental.business.DefaultConnection;
import polito.environmental.business.Engine;
import polito.environmental.business.FakeConnection;
import polito.environmental.business.Phase;

public class ArdulinkColumnApp implements Controller, GUI {

	private JFrame frame;
	private SerialConnectionPanel connectionPanel;
	private JTextArea statusArea;
	private JButton start;
	private JButton connect;
	private JButton disconnect;
	private JCheckBox debug;
	private JCheckBox invertPin;
	private Thread engineWorker;
	private ConfigurationPanel configurationPanel;
	private JPanel leftPanel_1;

	private Thread fileChecker;
	private Long lastTimeChecked;

	private Thread dlsThread;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ArdulinkColumnApp window = new ArdulinkColumnApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ArdulinkColumnApp() {
		this.initialize();
	}

	private void initialize() {
		this.frame = new JFrame();
		this.frame.setBounds(100, 100, 900, 600);
		this.frame.setDefaultCloseOperation(3);
		this.frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		JSplitPane splitPane = new JSplitPane();
		splitPane.setContinuousLayout(true);
		this.frame.getContentPane().add(splitPane);
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(0);
		splitPane.setRightComponent(splitPane_1);
		this.connectionPanel = new SerialConnectionPanel();
		splitPane_1.setTopComponent((Component) this.connectionPanel);
		this.statusArea = new JTextArea();
		splitPane_1.setBottomComponent(this.statusArea);
		this.leftPanel_1 = new JPanel();
		splitPane.setLeftComponent(this.leftPanel_1);
		
		this.leftPanel_1.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 3;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.VERTICAL;
		
		this.configurationPanel = new ConfigurationPanel();
		this.leftPanel_1.add(this.configurationPanel, c);

		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(0, 3));
		this.setupConnectionButtons(buttons);
		this.setupStartAndConfiguration(buttons);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		this.leftPanel_1.add(buttons, c);

		// Checking of file
		fileChecker = new Thread() {
			@Override
			public void run() {
				while (true) {

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}

					if (configurationPanel.getDLSFilePath() == null) {
						continue;
					}
					if (configurationPanel.getDLSFilePath().trim().equals("")) {
						continue;
					}
					File file = new File(configurationPanel.getDLSFilePath());
					if (!file.exists()) {
						continue;
					}
					if (lastTimeChecked == null) {
						lastTimeChecked = file.lastModified();
					} else {
						if (lastTimeChecked.longValue() != file.lastModified()) {
							dlsFileModified();
							lastTimeChecked = file.lastModified();
						}
					}
				}
			}

		};

		fileChecker.start();

	}

	private void DLSUp() {
		if (!ArdulinkColumnApp.this.debug.isSelected()) {
			Link.getDefaultInstance().sendPowerPinSwitch(configurationPanel.getDLSPin(), 1);
		} else {
			appendStatus("DLS go Up");
		}
	}

	private void DLSDown() {
		if (!ArdulinkColumnApp.this.debug.isSelected()) {
			Link.getDefaultInstance().sendPowerPinSwitch(configurationPanel.getDLSPin(), 0);
		} else {
			appendStatus("DLS go Down");
		}
	}

	/**
	 * DLS file exists and was modified since last view
	 */
	private void dlsFileModified() {

		// Service is not connected
		if (this.connect.isEnabled()) {
			return;
		}
		
		if ( dlsThread != null ) {
			appendStatus("DLS File was modified but DLS was already running: ignore");
			return;
		}

		appendStatus("DLS File was modified: start DLS for " + configurationPanel.getDLSTimeoutSeconds() + " seconds");
		
		dlsThread = new Thread() {

			public void run() {

				try {

					long timeout = configurationPanel.getDLSTimeoutSeconds() * 1000;

					DLSDown();

					Thread.sleep(timeout);

					DLSUp();
					
				} catch (Throwable t) {
					appendStatus("Errore durante l'esecuzione del comando su DLS");
				}

				dlsThread = null;

			}

		};
		
		dlsThread.start();

	}

	private void setupStartAndConfiguration(JPanel leftPanel) {
		this.start = new JButton("Start");
		this.start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArdulinkColumnApp.this.start.setEnabled(false);
				ArdulinkColumnApp.this.startColumnWork();
			}
		});
		this.debug = new JCheckBox("Debug", true);
		this.invertPin = new JCheckBox("Inverti PIN", true);
		leftPanel.add(this.start);
		leftPanel.add(this.debug);
		leftPanel.add(this.invertPin);
	}

	private void setupConnectionButtons(JPanel panel) {
		this.connect = new JButton("Connect");
		this.disconnect = new JButton("Disconnect");
		this.connect.setEnabled(true);
		this.disconnect.setEnabled(false);
		this.connect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (!configurationFinished(connect)) {
					return;
				}

				String comPort = ArdulinkColumnApp.this.connectionPanel.getConnectionPort();
				String baudRateS = ArdulinkColumnApp.this.connectionPanel.getBaudRate();
				try {
					int baudRate = Integer.parseInt(baudRateS);
					if (!ArdulinkColumnApp.this.debug.isSelected()) {
						Link.getDefaultInstance().connect(new Object[] { comPort, baudRate });
					}
					ArdulinkColumnApp.this.connect.setEnabled(false);
					ArdulinkColumnApp.this.disconnect.setEnabled(true);
					ArdulinkColumnApp.this.connected();
				} catch (Exception ex) {
					ex.printStackTrace();
					String message = ex.getMessage();
					if (message == null || message.trim().equals("")) {
						message = "Generic Error on connection";
					}
					JOptionPane.showMessageDialog(ArdulinkColumnApp.this.connect, message, "Error", 0);
				}
			}

		});

		this.disconnect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!ArdulinkColumnApp.this.debug.isSelected()) {
					Link.getDefaultInstance().disconnect();
				}
				ArdulinkColumnApp.this.disconnected();
				ArdulinkColumnApp.this.connect.setEnabled(true);
				ArdulinkColumnApp.this.disconnect.setEnabled(false);
			}

		});

		panel.add(this.connect);
		panel.add(this.disconnect);
	}

	private void startColumnWork() {
		int[] ipins = this.configurationPanel.getPins();
		Connection connection = new FakeConnection();
		if (!this.debug.isSelected()) {
			connection = new DefaultConnection(ipins);
		}
		Engine engine = new Engine((GUI) this, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ArdulinkColumnApp.this.appendStatus("Terminato");
				ArdulinkColumnApp.this.start.setEnabled(true);
			}

		}, connection,
				new Phase[] { new Phase("II\u00b0 Stadio", this.configurationPanel.getStadioII(), new boolean[] { false, false, false, true }),
						new Phase("III\u00b0 Stadio", this.configurationPanel.getStadioIII(), new boolean[] { false, false, false, false }),
						new Phase("IV\u00b0 Stadio", this.configurationPanel.getStadioIV(), new boolean[] { false, false, true, false }),
						new Phase("V\u00b0 Stadio", this.configurationPanel.getStadioV(), new boolean[] { false, true, false, false }),
						new Phase("VI\u00b0 Stadio", this.configurationPanel.getStadioVI(), new boolean[] { true, false, false, false }) });
		engine.setInvertPin(this.invertPin.isSelected());
		this.engineWorker = new Thread((Runnable) engine);
		this.engineWorker.start();

	}

	private boolean configurationFinished(Component component) {

		try {
			this.configurationPanel.getPins();
			this.configurationPanel.getDLSPin();
			this.configurationPanel.getDLSTimeoutSeconds();
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(component, "Qualcuno dei pin non e'impostato correttamente: verifica!",
					"Error", 0);
			return false;
		}

		if (this.configurationPanel.getDLSFilePath() == null
				|| this.configurationPanel.getDLSFilePath().trim().equals("")
				|| !new File(this.configurationPanel.getDLSFilePath().trim()).exists()) {

			JOptionPane.showMessageDialog(component, "Verifica il path del file del DLS", "Error", 0);
			return false;

		}

		return true;

	}

	public void connected() {

		// Clear
		this.clearStatus();

		// Log
		this.appendStatus("Arduino Module Connected");

		// Turn on DLS
		DLSUp();
	}

	public void disconnected() {
		this.appendStatus("Arduino Module Disconnected");
	}

	public void clearStatus() {
		this.statusArea.setText("");
	}

	public void appendStatus(String message) {
		this.statusArea.setText(String.valueOf(this.statusArea.getText()) + message + "\n");
	}

}
