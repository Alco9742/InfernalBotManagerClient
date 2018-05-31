package net.nilsghesquiere;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.entities.Client;
import net.nilsghesquiere.entities.IniSettings;
import net.nilsghesquiere.entities.User;
import net.nilsghesquiere.gui.swing.InfernalBotManagerGUI;
import net.nilsghesquiere.hooks.GracefulExitHook;
import net.nilsghesquiere.monitoring.SystemMonitor;
import net.nilsghesquiere.runnables.ExitWaitRunnable;
import net.nilsghesquiere.runnables.InfernalBotCheckerRunnable;
import net.nilsghesquiere.runnables.ManagerMonitorRunnable;
import net.nilsghesquiere.runnables.ThreadCheckerRunnable;
import net.nilsghesquiere.runnables.UpdateCheckerRunnable;
import net.nilsghesquiere.services.ClientService;
import net.nilsghesquiere.services.UserService;
import net.nilsghesquiere.util.ProgramConstants;
import net.nilsghesquiere.util.ProgramUtil;
import net.nilsghesquiere.util.enums.ClientStatus;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Reg;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

public class Main{
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static final SystemMonitor systemMonitor = new SystemMonitor();
	private static InfernalBotManagerClient infernalBotManagerClient;
	public static Thread gracefullExitHook;
	public static Map<Thread, Runnable> threadMap = new HashMap<>();
	public static ExitWaitRunnable exitWaitRunnable;
	public static Thread exitWaitThread;
	public static ManagerMonitorRunnable managerMonitorRunnable;
	public static String iniLocation;
	public static boolean softStop; //don't update any accounts / settings on close
	public static boolean softStart; //don't update any accounts / settings on start
	public static boolean serverUpToDate = true;
	

	public static void main(String[] args) throws InterruptedException{
		//Hook to ensure safe exits
		addExitHook();
		startExitWaitThread();
		//Lessen the chance that WmiPrvServer keeps hanging (oshi)
		killWmiPrvSE();
		//Disable the windows error reporting
		disableWindowsErrorReporting();
		//Start the GUI
		if(ProgramConstants.useSwingGUI){
			@SuppressWarnings("unused")
			InfernalBotManagerGUI gui = new InfernalBotManagerGUI();
			TimeUnit.SECONDS.sleep(2);
		}
		
		LOGGER.info("Starting InfernalBotManager Client");
		
		//Check the args -> Should only be used when updating
		try{
			for (int i=0; i<args.length; i++){
				LOGGER.debug("arg[" + i + "] = " + args[i]);
			}
			iniLocation = args[0];
			softStart = args[1].equals("soft");
		} catch (ArrayIndexOutOfBoundsException e){
			iniLocation = System.getProperty("user.dir") + "\\" + ProgramConstants.INI_NAME; 
			softStart = false;
		}
		
		//Build the IniSettings
		//TODO catch errors
		Optional<IniSettings> iniSettings = buildIniSettings(iniLocation);
		if(iniSettings.isPresent()){
			//Build the user
			Optional<User> user = buildUser(iniSettings.get());
			if(user.isPresent()){
				//build the client
				Optional<Client> client = buildClient(iniSettings.get(), user.get());
				if(client.isPresent()){
					client.get().setUser(user.get());
					LOGGER.info(client.toString());
					if(checkClientHWID(iniSettings.get(), client.get())){
						infernalBotManagerClient = new InfernalBotManagerClient(iniSettings.get(), client.get());
					}
				} else {
					LOGGER.info("Client '" + iniSettings.get().getClientTag() + "' not found on the server.");
				}
			} // TODO else voor user
		}
		
		if(infernalBotManagerClient != null){
			try{
				if(infernalBotManagerClient.getIniSettings().getTestmode()){
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
			} catch(Exception e){
				//UNHANDLED EXCEPTIONS
				LOGGER.debug("Unhandled Exception:", e);
			}
		} else {
			LOGGER.info("Closing InfernalBotManager Client");
			exitWaitRunnable.exit();
		}

	}
	

	private static void test(){
		LOGGER.info("testmode");
	}
	
	private static void program(){
		//start the ThreadChecker if enabled in ini
		if (infernalBotManagerClient.getIniSettings().getDebugThreads()){
			startThreadCheckerThread();
		}
		boolean upToDate = true;
		boolean connected = false;
		boolean killSwitchOff = true;
		while(!connected){
			try{
				connected = infernalBotManagerClient.checkConnection();
				if (connected){
					if(infernalBotManagerClient.checkKillSwitch()){
						killSwitchOff = false;
					} else {
						if(!infernalBotManagerClient.checkClientVersion()){
							upToDate = false;
						}
						if(!infernalBotManagerClient.checkServerVersion()){
							serverUpToDate = false;
						}
						infernalBotManagerClient.checkUpdateNow(); //Not doing anything with this yet, just placing it here to catch the nullpointer if server isn't set up correctly yet
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
		//connection has been made start the monitor & updatechecker threads
		if(!exitWaitRunnable.getExit()){
			startMonitorThread(infernalBotManagerClient);
			startUpdateCheckerThread(infernalBotManagerClient);
		}
		if (killSwitchOff){
			//check for update
			if (upToDate){
				//backup sqllite file
				if(infernalBotManagerClient.backUpInfernalDatabase()){
					//initial checks
					//Check if infernalbot tables have been changed since last version ((if it updates this launch the pragmas will still change after launch which is why we do another check later on)
					infernalBotManagerClient.checkTables();
					//Attempt to get accounts, retry if fail
					boolean initDone = infernalBotManagerClient.checkConnection() && infernalBotManagerClient.setInfernalSettings() && infernalBotManagerClient.initialExchangeAccounts();
					while (!initDone){
						try {
							LOGGER.info("Retrying in 1 minute...");
							TimeUnit.MINUTES.sleep(1);
							initDone = (infernalBotManagerClient.checkConnection() && infernalBotManagerClient.setInfernalSettings() && infernalBotManagerClient.initialExchangeAccounts());
						} catch (InterruptedException e) {
							LOGGER.debug(e.getMessage());
						}
					}
					//schedule reboot
					infernalBotManagerClient.scheduleReboot();
					//empty queuers (don't do this if softStart)
					infernalBotManagerClient.deleteAllQueuers();
					//send client status
					managerMonitorRunnable.setClientStatus(ClientStatus.CONNECTED);
					//start infernalbot checker in a thread
					startInfernalCheckerThread();
					
				} else {
					LOGGER.info("Closing InfernalBotManager Client");
					exitWaitRunnable.exit();
				}
			} else {
				managerMonitorRunnable.setClientStatus(ClientStatus.UPDATE);
				infernalBotManagerClient.updateClient();
				LOGGER.info("Closing InfernalBotManager Client");
				exitWaitRunnable.exit();
			}
		} else {
			LOGGER.info("Closing InfernalBotManager Client");
			exitWaitRunnable.exit();
		}
	}
	
	private static Optional<IniSettings> buildIniSettings(String iniFile){
		//VARS
		IniSettings iniSettings = null;
		Path iniFilePath = Paths.get(iniFile);
		if(Files.exists(iniFilePath)){
			try {
				Wini ini = new Wini(new File(iniFile));
				iniSettings = IniSettings.buildFromIni(ini);
			} catch (InvalidFileFormatException e) {
				LOGGER.error("Error reading settings.ini file");
				LOGGER.debug(e.getMessage());
			} catch (IOException e) {
				LOGGER.error("Error reading settings.ini file");
				LOGGER.debug(e.getMessage());;
			}
		} else {
			LOGGER.error("settings.ini file not found at path: " + iniFilePath);;
		}
		if (iniSettings != null){
			return Optional.of(iniSettings);
		} else {
			return Optional.empty();
		}
	}

	private static Optional<User> buildUser(IniSettings iniSettings){
		UserService userService = new UserService(iniSettings);
		User user = userService.getUser(iniSettings.getUsername());
		if (user != null){
			return Optional.of(user);
		} else {
			return Optional.empty();
		}
	}
	
	private static Optional<Client> buildClient(IniSettings iniSettings, User user){
		ClientService clientService = new ClientService(iniSettings);
		Client client = clientService.getClient(user.getId(), iniSettings.getClientTag());
		if (client != null){
			return Optional.of(client);
		} else {
			return Optional.empty();
		}
	}
	
	private static Boolean checkClientHWID(IniSettings iniSettings, Client client) {
		String clientHWID = client.getHWID();
		String computerHWID = systemMonitor.getHWID();
		
		if (clientHWID.trim().equals(computerHWID.trim())){
			return true;
		}
		
		if (clientHWID.trim().isEmpty()){
			LOGGER.info("Registering HWID " + computerHWID + " for client '" + client.getTag() +"'.");
			ClientService clientService = new ClientService(iniSettings);
			return clientService.registerHWID(client.getUser().getId(), client.getId(), computerHWID);
		} else {
			LOGGER.info("Incorrect HWID for for client '" + client.getTag() +"'.");
			return false;
		}

	}

	
	private static void addExitHook(){
		gracefullExitHook = new GracefulExitHook();
		Runtime.getRuntime().addShutdownHook(gracefullExitHook);
	}
	
	private static void startExitWaitThread(){
		exitWaitRunnable = new ExitWaitRunnable();
		exitWaitThread = new Thread(exitWaitRunnable);
		exitWaitThread.setName("Exit Wait Thread");
		exitWaitThread.start();
	}
	
	private static void startThreadCheckerThread(){
		ThreadCheckerRunnable threadCheckerRunnable = new ThreadCheckerRunnable();
		Thread threadCheckerThread = new Thread(threadCheckerRunnable);
		threadMap.put(threadCheckerThread, threadCheckerRunnable);
		threadCheckerThread.setDaemon(false); 
		threadCheckerThread.setName("Thread Checker Thread");
		threadCheckerThread.start();
	}

	private static void startMonitorThread(InfernalBotManagerClient client){
		managerMonitorRunnable = new ManagerMonitorRunnable(systemMonitor,client);
		Thread managerMonitorThread = new Thread(managerMonitorRunnable);
		threadMap.put(managerMonitorThread, managerMonitorRunnable);
		managerMonitorThread.setDaemon(false); 
		managerMonitorThread.setName("Manager Monitor Thread");
		managerMonitorThread.start();
	}
	
	private static void startInfernalCheckerThread(){
		InfernalBotCheckerRunnable infernalRunnable = new InfernalBotCheckerRunnable(infernalBotManagerClient);
		Thread infernalThread = new Thread(infernalRunnable);
		threadMap.put(infernalThread, infernalRunnable);
		infernalThread.setDaemon(false); 
		infernalThread.setName("InfernalBot Checker Thread");
		infernalThread.start();
	}
	
	private static void startUpdateCheckerThread(InfernalBotManagerClient client){
		UpdateCheckerRunnable updateCheckerRunnable = new UpdateCheckerRunnable(client);
		Thread updateCheckerThread = new Thread(updateCheckerRunnable);
		threadMap.put(updateCheckerThread, updateCheckerRunnable);
		updateCheckerThread.setDaemon(false); 
		updateCheckerThread.setName("Update Checker Thread");
		updateCheckerThread.start();
	}
	
	private static void killWmiPrvSE(){
		//kill the Wmi prv service at startup to lessen the chance that it keeps hanging when using oshi
		LOGGER.debug("Killing WmiPrvSE.exe");
		ProgramUtil.killProcessIfRunning("WmiPrvSE.exe");
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
	

}
