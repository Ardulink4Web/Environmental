package polito.environmental;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConfigurationPanel extends JPanel {

	private static final long serialVersionUID = 1;
	private JTextField ev1;
	private JTextField ev2;
	private JTextField ev3;
	private JTextField ev4;
	
	private JTextField stadioII;
	private JTextField stadioIII;
	private JTextField stadioIV;
	private JTextField stadioV;
	private JTextField stadioVI;
		
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;

	public ConfigurationPanel() {
		this.setForeground(Color.WHITE);
		this.setLayout(new GridLayout(0, 2, 5, 5));
		
		ev1 = addRow("EV1:");
		ev2 = addRow("EV2:");
		ev3 = addRow("EV3:");
		ev4 = addRow("EV4:");
		
		stadioII = addRow("Stadio II:");
		stadioIII = addRow("Stadio III:");
		stadioIV = addRow("Stadio IV:");
		stadioV = addRow("Stadio V:");
		stadioVI = addRow("Stadio VI:");
				
		JLabel lblEv_5 = new JLabel("Timeout DLS: ");
		this.add(lblEv_5);
		this.textField_5 = new JTextField();
		this.add(this.textField_5);
		this.textField_5.setColumns(10);

		JLabel lblEv_6 = new JLabel("File DLS: ");
		this.add(lblEv_6);
		this.textField_6 = new JTextField();
		this.add(this.textField_6);
		this.textField_6.setColumns(10);

		JLabel lblEv_7 = new JLabel("EV DLS: ");
		this.add(lblEv_7);
		this.textField_7 = new JTextField();
		this.add(this.textField_7);
		this.textField_7.setColumns(10);

	}

	private JTextField addRow(String label) {
		JLabel lblEv_2 = new JLabel(label);
		this.add(lblEv_2);
		JTextField text = new JTextField();
		this.add(text);
		text.setColumns(10);
		return text; 
	}
	
	public int getDLSTimeoutSeconds() {
		return Integer.parseInt(this.textField_5.getText());
	}
	
	public String getDLSFilePath() {
		return this.textField_6.getText();
	}
	
	public int getDLSPin() {
		return Integer.parseInt(this.textField_7.getText());
	}

	public int[] getPins() {
		return new int[] { 
				Integer.parseInt(this.ev1.getText()), 
				Integer.parseInt(this.ev2.getText()),
				Integer.parseInt(this.ev3.getText()), 
				Integer.parseInt(this.ev4.getText()) };
	}

	public int getStadioII() {
		return Integer.parseInt(this.stadioII.getText());
	}
	
	public int getStadioIII() {
		return Integer.parseInt(this.stadioIII.getText());
	}

	public int getStadioIV() {
		return Integer.parseInt(this.stadioIV.getText());
	}

	public int getStadioV() {
		return Integer.parseInt(this.stadioV.getText());
	}

	public int getStadioVI() {
		return Integer.parseInt(this.stadioVI.getText());
	}
	
}
