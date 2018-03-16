package net.nilsghesquiere;
import java.awt.BorderLayout;
import java.awt.TextArea;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Reg;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.gui.swing.TextAreaOutputStream;
import net.nilsghesquiere.hooks.GracefulExitHook;
import net.nilsghesquiere.runnables.ExitWaitRunnable;
import net.nilsghesquiere.runnables.InfernalBotManagerRunnable;
import net.nilsghesquiere.util.ProgramConstants;

public class Main{
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static InfernalBotManagerClient client;
	public static Map<Thread, Runnable> threadMap = new HashMap<>();
	public static ExitWaitRunnable exitWaitRunnable;
	public static Thread exitWaitThread;
	public static String iniLocation;
	
	public static void main(String[] args) throws InterruptedException{
		if(ProgramConstants.useSwingGUI){
			JFrame frame = new JFrame();
			frame.add( new JLabel("InfernalBotManager By Alco" ), BorderLayout.NORTH );
	
			JTextArea ta = new JTextArea();
			TextAreaOutputStream taos = new TextAreaOutputStream( ta, 60 );
			PrintStream ps = new PrintStream( taos );
			System.setOut( ps );
			System.setErr( ps );
			
			frame.add( new JScrollPane( ta )  );
	
			frame.pack();
			frame.setVisible( true );
			frame.setSize(600,400);
		}
		LOGGER.info("Starting InfernalBotManager Client");
		Runtime.getRuntime().addShutdownHook(new GracefulExitHook());
		try{
			iniLocation = args[0];
		} catch (ArrayIndexOutOfBoundsException e){
			iniLocation = System.getProperty("user.dir") + "\\" + ProgramConstants.INI_NAME; 
		}
		client = buildClient(iniLocation);
		program();
		//test();
	}
	
	private static void test(){
	}
	
	private static void program(){
		if (client != null){
			boolean upToDate = true;
			boolean connected = false;
			boolean killSwitchOff = true;
			while(!connected){
				try{
					connected = client.checkConnection();
					if (connected){
						if(client.checkKillSwitch()){
							killSwitchOff = false;
						} else {
							if(!client.checkVersion()){
								upToDate = false;
							}
						}
					}
					if(!connected){
						LOGGER.info("Retrying in 1 minute..");
						try {
							TimeUnit.MINUTES.sleep(1);
						} catch (InterruptedException e2) {
							LOGGER.error("Failure during sleep");
							LOGGER.debug(e2.getMessage());
						}
					}
				} catch (NullPointerException ex){
					LOGGER.error("Bad configuration on the server, contact Alco");
					System.exit(0);
				}
			}
			if (killSwitchOff){
				if (upToDate){
					client.scheduleReboot();
					//check for update
					//initial checks
					//Attempt to get accounts, retry if fail
					boolean initDone = client.checkConnection() &&  client.backUpInfernalDatabase() && client.setInfernalSettings() && client.exchangeAccounts();
					while (!initDone){
						try {
							LOGGER.info("Retrying in 1 minute...");
							TimeUnit.MINUTES.sleep(1);
							initDone = (client.checkConnection() && client.backUpInfernalDatabase() && client.setInfernalSettings() && client.exchangeAccounts());
						} catch (InterruptedException e) {
							LOGGER.error("Failure during sleep");
							LOGGER.debug(e.getMessage());
						}
					}
					//send clientData for startup
					client.getClientDataService().sendData("InfernalBotManager Startup");
					//start the ExitWaiter
					exitWaitRunnable = new ExitWaitRunnable();
					exitWaitThread = new Thread(exitWaitRunnable);
					exitWaitThread.setDaemon(false); 
					exitWaitThread.start();
					//start infernalbot checker in a thread
					InfernalBotManagerRunnable infernalRunnable = new InfernalBotManagerRunnable(client);
					Thread infernalThread = new Thread(infernalRunnable);
					threadMap.put(infernalThread, infernalRunnable);
					infernalThread.setDaemon(false); 
					infernalThread.start();
				} else {
					client.updateClient();
					LOGGER.info("Closing InfernalBotManager Client");
					System.exit(0);
				}
			} else {
				LOGGER.info("Closing InfernalBotManager Client");
				System.exit(0);
			}
		} else {
			LOGGER.info("Closing InfernalBotManager Client");
			System.exit(0);
		}
	}
	private static InfernalBotManagerClient buildClient(String iniFile){
		Path iniFilePath = Paths.get(iniFile);
		InfernalBotManagerClient client = null;
		if(Files.exists(iniFilePath)){
			try {
				Wini ini = new Wini(new File(iniFile));
				ClientSettings settings = ClientSettings.buildFromIni(ini);
				if (settings != null){
					client = new InfernalBotManagerClient(settings);
				}
			} catch (InvalidFileFormatException e) {
				LOGGER.error("Error reading settings.ini file");
				LOGGER.debug(e.getMessage());
			} catch (IOException e) {
				LOGGER.error("Error reading settings.ini file");
				LOGGER.debug(e.getMessage());;
			}
		} else {
			LOGGER.error("settings.ini file not found at path: " + iniFilePath);
		}
		disableWindowsErrorReporting();
		return client;
	}
	
	private static void disableWindowsErrorReporting(){
		Reg reg = new Reg();
		Reg.Key key = reg.add("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\Windows Error Reporting");
		key.put("DontShowUI", "1");
		try {
			reg.write();
		} catch (IOException e) {
			LOGGER.info("Failure trying to disable Windows error reporting UI");
			LOGGER.debug(e.getMessage());
		}
		
	}
}
