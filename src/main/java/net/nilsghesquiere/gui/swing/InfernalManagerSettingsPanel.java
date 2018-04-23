package net.nilsghesquiere.gui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.regexlab.j2e.SystemTray;
import com.regexlab.j2e.SystemTrayCallback;
import com.regexlab.j2e.SystemTrayMenu;

public class InfernalManagerSettingsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JCheckBox jCheckBox = null;
 	private SystemTray systemTray = null;
	private SystemTrayMenu systemTrayMenu = null;
	private Boolean drawn = false;
	
	
	public void paintComponent (Graphics g) {
		if (!drawn){
			this.setLayout(new GridLayout(3, 1));
			addSettings();
			//addMinimizeToTaskbarButton();
			this.drawn = true;
		}
	}

	private void addSettings(){
		Dimension labelDimension = new Dimension(80,20);
		Dimension textFieldDimension = new Dimension(200,20);
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
		this.add(loginSettings);
	}
	
	private void addMinimizeToTaskbarButton(){
		this.add(getJCheckBox());
		
		// Safely remove systray icon when System.exit()
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				getSystemTray().Hide();
			}
		});

		// Callback
		SystemTray.setSystemTrayCallback(new SystemTrayCallback() {
			public void OnMouseClick(SystemTray tray, int mouseEvent) {
				if(mouseEvent == SystemTray.RIGHT_CLICK) {
					getSystemTrayMenu().Popup();
				}
			}

			public void OnMenuCommand(int menuid) {
				if(menuid == 1) {
					getJCheckBox().setSelected(false);
					getSystemTray().Hide();
				}
				else if(menuid == 2) {
					System.exit(0);
				}
			}
		});
	}
	
	private JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
			jCheckBox.setText("Show System Tray");
			jCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if(jCheckBox.isSelected()) {
						getSystemTray().Show();
					}else {
						getSystemTray().Hide();
					}
				}
			});
		}
		return jCheckBox;
	}

	private SystemTray getSystemTray() {
		if(systemTray == null) {
			systemTray = new SystemTray(
					1,                     // 1 - the first 'icon image', etc
					"InfernalBotManafer"    // the tips string
			);
		}
		return systemTray;
	}
	private SystemTrayMenu getSystemTrayMenu() {
		if(systemTrayMenu == null) {
			systemTrayMenu = new SystemTrayMenu();
			systemTrayMenu.Append("Hide", 1);
			systemTrayMenu.AppendSeparator();
			systemTrayMenu.Append("Exit", 2);
		}
		return systemTrayMenu;
	}
	
}