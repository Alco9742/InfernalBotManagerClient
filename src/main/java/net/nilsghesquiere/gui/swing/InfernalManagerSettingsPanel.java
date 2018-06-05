package net.nilsghesquiere.gui.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InfernalManagerSettingsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Boolean drawn = false;
	private Dimension labelDimension = new Dimension(150,20);
	private Dimension textFieldDimension = new Dimension(200,20);
	
	
	public void paintComponent (Graphics g) {
		if (!drawn){
			this.setLayout(new GridLayout(5, 1));
			this.add(getLoginSettingsPanel());
			this.drawn = true;
		}
	}

	private JPanel getLoginSettingsPanel(){
		JPanel loginSettings = new JPanel();
		loginSettings.setLayout(new GridLayout(2, 1));
		
		JPanel emailPanel = new JPanel();
		emailPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lEmail = new JLabel("Email");
		JTextField tEmail = new JTextField();
		lEmail.setPreferredSize(labelDimension);
		tEmail.setPreferredSize(textFieldDimension);
		emailPanel.add(lEmail);
		emailPanel.add(tEmail);
		
		JPanel passwordPanel = new JPanel();
		passwordPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lPassword = new JLabel("Password");
		JTextField tPassword = new JTextField();
		lPassword.setPreferredSize(labelDimension);
		tPassword.setPreferredSize(textFieldDimension);
		passwordPanel.add(lPassword);
		passwordPanel.add(tPassword);
		
		loginSettings.add(emailPanel);
		loginSettings.add(passwordPanel);
		return loginSettings;
	}
}