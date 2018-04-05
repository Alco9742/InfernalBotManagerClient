package net.nilsghesquiere.gui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class InfernalManagerConsolePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public void paintComponent (Graphics g) {
		this.setLayout(new GridLayout(1, 1));
		JTextArea ta = new JTextArea();
		//Set background black
		ta.setBackground(Color.BLACK); 
		//Set Foreground(text) white
		ta.setForeground(Color.WHITE);
		DefaultCaret caret = (DefaultCaret)ta.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		TextAreaOutputStream taos = new TextAreaOutputStream( ta, 60 );
		PrintStream ps = new PrintStream( taos );
		System.setOut( ps );
		System.setErr( ps );
		this.add( new JScrollPane( ta )  );
	}
}