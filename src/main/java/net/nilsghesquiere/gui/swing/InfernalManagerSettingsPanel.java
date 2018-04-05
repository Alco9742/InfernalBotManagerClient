package net.nilsghesquiere.gui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;

import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

import com.regexlab.j2e.SystemTray;
import com.regexlab.j2e.SystemTrayCallback;
import com.regexlab.j2e.SystemTrayMenu;

public class InfernalManagerSettingsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JCheckBox jCheckBox = null;
 	private SystemTray systemTray = null;
	private SystemTrayMenu systemTrayMenu = null;
	public void paintComponent (Graphics g) {
		this.setLayout(new GridLayout(1, 1));
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
					"Hello System Tray"    // the tips string
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