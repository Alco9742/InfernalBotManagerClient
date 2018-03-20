package net.nilsghesquiere.gui.swing;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
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
		
		BufferedImage icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
		this.setTitle("InfernalBotManager");
		this.setIconImage(icon);
		
		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel consolePanel = new JPanel(false);
		
		consolePanel.setLayout(new GridLayout(1, 1));
		JTextArea ta = new JTextArea();
		//Set background black
		ta.setBackground(Color.BLACK); 
		//Set Foreground(text) white
		ta.setForeground(Color.WHITE);
		TextAreaOutputStream taos = new TextAreaOutputStream( ta, 60 );
		PrintStream ps = new PrintStream( taos );
		System.setOut( ps );
		System.setErr( ps );
		consolePanel.add( new JScrollPane( ta )  );
		
		tabbedPane.addTab("Console", consolePanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		this.add(tabbedPane);
		this.pack();
		this.setVisible( true );
		this.setSize(800,300);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				Main.exitWaitRunnable.exit();
			}
		});
		
	}
}
