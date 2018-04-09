package net.nilsghesquiere;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.gui.swing.InfernalBotManagerGUI;
import net.nilsghesquiere.hooks.GracefulExitHook;
import net.nilsghesquiere.monitor.SystemMonitor;
import net.nilsghesquiere.runnables.ExitWaitRunnable;
import net.nilsghesquiere.runnables.InfernalBotManagerRunnable;
import net.nilsghesquiere.runnables.ThreadCheckerRunnable;
import net.nilsghesquiere.util.ProgramConstants;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Reg;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

public class Main{
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static InfernalBotManagerClient client;
	public static Map<Thread, Runnable> threadMap = new HashMap<>();
	public static ExitWaitRunnable exitWaitRunnable;
	public static Thread exitWaitThread;
	public static Thread gracefullExitHook;
	public static String iniLocation;
	
	public static void main(String[] args) throws InterruptedException{
		gracefullExitHook = new GracefulExitHook();
		Runtime.getRuntime().addShutdownHook(gracefullExitHook);
		//start the ExitWaiter
		exitWaitRunnable = new ExitWaitRunnable();
		exitWaitThread = new Thread(exitWaitRunnable);
		exitWaitThread.start();
		if(ProgramConstants.useSwingGUI){
			@SuppressWarnings("unused")
			InfernalBotManagerGUI gui = new InfernalBotManagerGUI();
			TimeUnit.SECONDS.sleep(2);
		}
		LOGGER.info("Starting InfernalBotManager Client");
		
		try{
			iniLocation = args[0];
		} catch (ArrayIndexOutOfBoundsException e){
			iniLocation = System.getProperty("user.dir") + "\\" + ProgramConstants.INI_NAME; 
		}
		client = buildClient(iniLocation);
		if(client != null){
			try{
				if(client.getClientSettings().getTestMode()){
					test();
				} else {
					program();
				}
				//test();
			} catch(HttpClientErrorException e){
				//AuthenticationException
				LOGGER.debug("Received the following response from the server: " + e.getMessage());
				if (e.getMessage().toLowerCase().contains("unauthorized")){
					LOGGER.error("Failure authenticating to the server, check your credentials");
				}
				if (e.getMessage().toLowerCase().contains("not found")){
					LOGGER.error("Something went wrong, contact Alco");
				}
				LOGGER.info("Closing InfernalBotManager Client");
				exitWaitRunnable.exit();
			} 
		} else {
			LOGGER.info("Closing InfernalBotManager Client");
			exitWaitRunnable.exit();
		}

	}
	
	private static void test(){
		SystemMonitor monitor = new SystemMonitor();
		System.out.println(monitor.getRamUsage());
		System.out.println(monitor.getCpuUsage());
	}
	
	private static void program(){
		if (client.getClientSettings().getEnableDevMode()){
			//start the ThreadChecker
			ThreadCheckerRunnable threadCheckerRunnable = new ThreadCheckerRunnable();
			Thread threadCheckerThread = new Thread(threadCheckerRunnable);
			threadMap.put(threadCheckerThread, threadCheckerRunnable);
			threadCheckerThread.setDaemon(false); 
			threadCheckerThread.start();
		}
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
						LOGGER.debug(e2.getMessage());
					}
				}
			} catch (NullPointerException ex){
				LOGGER.error("Bad configuration on the server, contact Alco");
				exitWaitRunnable.exit();
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					LOGGER.debug(e.getMessage());
				}
			}
		}
		if (killSwitchOff){
			//check for update
			if (upToDate){
				//backup sqllite file
				if(client.backUpInfernalDatabase()){
					//initial checks
					//Check if infernalbot tables have been changed since last version
					client.checkTables();
					//Attempt to get accounts, retry if fail
					boolean initDone = client.checkConnection() && client.setUserId() && client.setInfernalSettings() && client.exchangeAccounts();
					while (!initDone){
						try {
							LOGGER.info("Retrying in 1 minute...");
							TimeUnit.MINUTES.sleep(1);
							initDone = (client.checkConnection() && client.setInfernalSettings() && client.exchangeAccounts());
						} catch (InterruptedException e) {
							LOGGER.debug(e.getMessage());
						}
					}
					//schedule reboot
					client.scheduleReboot();
					//empty queuers
					client.deleteAllQueuers();
					//send clientData for startup
					client.getClientDataService().sendData("InfernalBotManager Startup", "","");
					//start infernalbot checker in a thread
					InfernalBotManagerRunnable infernalRunnable = new InfernalBotManagerRunnable(client);
					Thread infernalThread = new Thread(infernalRunnable);
					threadMap.put(infernalThread, infernalRunnable);
					infernalThread.setDaemon(false); 
					infernalThread.start();
				} else {
					LOGGER.info("Closing InfernalBotManager Client");
					exitWaitRunnable.exit();
				}
			} else {
				client.updateClient();
				LOGGER.info("Closing InfernalBotManager Client");
				exitWaitRunnable.exit();
			}
		} else {
			LOGGER.info("Closing InfernalBotManager Client");
			exitWaitRunnable.exit();
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
			LOGGER.info("Generating default settings.ini file");
			generateDefaultSettings(iniFile);
		}
		disableWindowsErrorReporting();
		return client;
	}
	
	private static void disableWindowsErrorReporting(){
		Reg reg = new Reg();
		Reg.Key key = reg.add("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\Windows Error Reporting");
		key.put("DontShowUI", 1);
		key.putType("DontShowUI", Reg.Type.REG_DWORD);
		try {
			reg.write();
		} catch (IOException e) {
			LOGGER.debug("Failure trying to disable Windows error reporting UI");
			LOGGER.debug(e.getMessage());
		}
		
	}
	
	private static void generateDefaultSettings(String iniFile) {
		try {
			File file = new File(iniFile);
			file.createNewFile();
			Wini ini = new Wini(new File(iniFile));
			
			//login
			ini.put("login", "username", "managerusername");
			ini.put("login", "password", "managerpassword");
			
			//clientinfo
			ini.put("clientinfo", "clienttag", "clienttag");
			ini.put("clientinfo", "region", "EUW");
		
			//clientsettings
			ini.put("clientsettings", "infernalpath", "C:/PATH/TO/INFERNAL/MAP");
			ini.put("clientsettings", "accounts", "5");
			ini.put("clientsettings", "accountbuffer", "2");
			ini.put("clientsettings","uploadnewaccounts", "false");
			ini.put("clientsettings", "reboot", "false");
			ini.put("clientsettings", "reboottime", "10800");
			ini.put("clientsettings","fetchsettings", "true");
			ini.put("clientsettings","overwritesettings", "false");
			ini.put("clientsettings", "rebootfrommanager", "false");

			//botsettings
			ini.put("botsettings", "groups", "2");
			ini.put("botsettings", "clientpath", "C:/PATH/TO/LOL/MAP");
			
			//extra
			ini.put("extra", "readme", "readthereadme!!!!");
			
			//write
			ini.store();
		} catch (InvalidFileFormatException e) {
			LOGGER.error("Error reading settings.ini file");
			LOGGER.debug(e.getMessage());
		} catch (IOException e) {
			LOGGER.error("Failure creating file");
			LOGGER.debug(e.getMessage());
		}
	}
}
