package net.nilsghesquiere.gui.swing;

import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.nilsghesquiere.Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfernalBotManagerGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalBotManagerGUI.class);
	
	public InfernalBotManagerGUI(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			LOGGER.info("Failed to change set the GUI look and feel");
			LOGGER.debug(e.getMessage());
		} catch (InstantiationException e) {
			LOGGER.info("Failed to change set the GUI look and feel");
			LOGGER.debug(e.getMessage());
		} catch (IllegalAccessException e) {
			LOGGER.info("Failed to change set the GUI look and feel");
			LOGGER.debug(e.getMessage());
		} catch (UnsupportedLookAndFeelException e) {
			LOGGER.info("Failed to change set the GUI look and feel");
			LOGGER.debug(e.getMessage());
		}
		
		setIconConnected();
		this.setTitle("IBMC");
		
		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel consolePanel = new InfernalManagerConsolePanel();
		//JPanel settingsPanel = new InfernalManagerSettingsPanel();
		
		tabbedPane.addTab("Console", consolePanel);
		//tabbedPane.addTab("Settings", settingsPanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		this.add(tabbedPane);
		this.pack();
		this.setVisible( true );
		this.setSize(800,300);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (!Main.exitWaitRunnable.getExit()){
					LOGGER.info("Commencing InfernalBotManager close");
					Main.exitWaitRunnable.exit();
				}
			}
		});
	}
	
	protected ImageIcon createImageIcon(String path,String description) {
			java.net.URL imgURL = getClass().getResource(path);
			if (imgURL != null) {
				return new ImageIcon(imgURL, description);
			} else {
				System.err.println("Couldn't find file: " + path);
				return null;
			}
	}
	
	public void changeTitle(String newTitle){
		LOGGER.debug("Changing windows title to: '" + newTitle + "'");
		this.setTitle(newTitle);
	}
	
	public void setIconConnected(){
		changeIcon("/i.png","InfernalBotManagerClient");
	}
	
	public void setIconDisconnected(){
		changeIcon("/i-err.png","InfernalBotManagerClient");
	}
	
	private void changeIcon(String path,String description){
		ImageIcon newIcon = createImageIcon(path, description);
		this.setIconImage(newIcon.getImage());
	}
	
}
