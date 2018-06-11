package net.nilsghesquiere;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.entities.Client;
import net.nilsghesquiere.entities.IniSettings;
import net.nilsghesquiere.entities.User;
import net.nilsghesquiere.exceptionhandlers.RestErrorHandler;
import net.nilsghesquiere.gui.swing.InfernalBotManagerGUI;
import net.nilsghesquiere.hooks.GracefulExitHook;
import net.nilsghesquiere.monitoring.SystemMonitor;
import net.nilsghesquiere.runnables.ClientActionCheckerRunnable;
import net.nilsghesquiere.runnables.ExitWaitRunnable;
import net.nilsghesquiere.runnables.InfernalBotCheckerRunnable;
import net.nilsghesquiere.runnables.ClientDataRunnable;
import net.nilsghesquiere.runnables.ThreadCheckerRunnable;
import net.nilsghesquiere.runnables.UpdateCheckerRunnable;
import net.nilsghesquiere.services.ClientService;
import net.nilsghesquiere.services.GlobalVariableService;
import net.nilsghesquiere.services.UserService;
import net.nilsghesquiere.util.InternetAvailabilityChecker;
import net.nilsghesquiere.util.ProgramConstants;
import net.nilsghesquiere.util.ProgramUtil;
import net.nilsghesquiere.util.ProgramVariables;
import net.nilsghesquiere.util.enums.ClientDataStatus;
import net.nilsghesquiere.util.error.ServerInternalErrorException;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Reg;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

public class Main{
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static final SystemMonitor systemMonitor = new SystemMonitor();
	private static InfernalBotManagerClient infernalBotManagerClient;
	public static Thread gracefullExitHook;
	public static Map<Thread, Runnable> threadMap = new HashMap<>();
	public static ExitWaitRunnable exitWaitRunnable;
	public static Thread exitWaitThread;
	public static ClientDataRunnable managerMonitorRunnable;
	public static ClientActionCheckerRunnable clientActionCheckerRunnable;

	public static void main(String[] args) throws InterruptedException{
		//Hook to ensure safe exits
		addExitHook();
		startExitWaitThread();
		//Lessen the chance that WmiPrvServer keeps hanging (oshi)
		killWmiPrvSE();
		//Disable the windows error reporting
		disableWindowsErrorReporting();
		//Start the GUI
		InfernalBotManagerGUI gui = new InfernalBotManagerGUI();
		TimeUnit.SECONDS.sleep(2);
		
		LOGGER.info("Starting InfernalBotManager Client");
		
		//Check the args -> Should only be used when updating
		try{
			for (int i=0; i<args.length; i++){
				LOGGER.debug("arg[" + i + "] = " + args[i]);
			}
			ProgramVariables.iniLocation = args[0];
			ProgramVariables.softStart = args[1].equals("soft");
		} catch (ArrayIndexOutOfBoundsException e){
			ProgramVariables.iniLocation = System.getProperty("user.dir") + "\\" + ProgramConstants.INI_NAME; 
			ProgramVariables.softStart = false;
		}
		
		//FROM HERE ON WE BUILD THE REST TEMPLATE, USE IT TO CHECK AUTH AND CHECK OR UPDATES BEFORE CONTINUEING
		//Initializing optionals
		Optional<IniSettings> iniSettings = Optional.empty();
		Optional<OAuth2RestTemplate> restTemplate= Optional.empty();
		Optional<User> user= Optional.empty();
		Optional<Client> client = Optional.empty();
		//Conditions we should check before launching the actual program
		boolean outdated = false;
		boolean killswitch = false;
		boolean badconfig = false;
		boolean runprogram = true;
		boolean launchError =false;
		
		//Build the IniSettings
		iniSettings = buildIniSettings(ProgramVariables.iniLocation);
		if(iniSettings.isPresent()){
			//Build the RestTemplate
			restTemplate = buildRestTemplate(gui,iniSettings.get());
		}
		
		//If the template is present the user is already authenticated
		if(restTemplate.isPresent()){
			//We got connection with the server, check for updates before actually launching the core program
			GlobalVariableService globalVariableService = new GlobalVariableService(restTemplate.get());
			try{
				killswitch = globalVariableService.checkKillSwitch();
				ProgramVariables.serverUpToDate = globalVariableService.checkServerVersion();
				outdated = !globalVariableService.checkClientVersion();
			} catch (NullPointerException ex){
				LOGGER.debug("Handled exception:", ex);
				badconfig = true;
			}	
		
			if(badconfig || killswitch || outdated ){
				runprogram = false;
				launchError = true;
			}
			
			if (runprogram){
				user = buildUser(iniSettings.get(), restTemplate.get());
				if(user.isPresent()){
					//Build the client optional
					client = buildClient(iniSettings.get(),restTemplate.get(), user.get());
					if(client.isPresent()){
						//Set user on the client
						client.get().setUser(user.get());
						//Check or register the client HWID
						if(checkClientHWID(iniSettings.get(),restTemplate.get(), client.get())){
							infernalBotManagerClient = new InfernalBotManagerClient(gui, iniSettings.get(), client.get(),restTemplate.get());
							if(infernalBotManagerClient.getIniSettings().getTestmode()){
								try{
									test();
								} catch (Exception e){
									LOGGER.debug("Unhandled exception in testmode:", e);
								}
							} else {
								try{
									program();
								} catch(ServerInternalErrorException e){
									LOGGER.error(e.getMessage());
									LOGGER.debug("Unhandled internal server exception:", e);
								} catch (Exception e){
									LOGGER.debug("Unhandled exception:", e);
								}
							}
						} else {
							launchError = true;
						}
					} else {
						LOGGER.error("Client '" + iniSettings.get().getClientTag() + "' not found on the server");
						launchError = true;
					}
				} else {
					//Should never happen since the user is already authenticated by the resttemplate here
					LOGGER.error("User not found on the server");
					launchError = true;
				}
			} else {
				if(badconfig){
					LOGGER.error("Bad configuration on the server, contact Alco");
				} else {
					if(outdated){
						updateClient(iniSettings.get());
						LOGGER.info("Closing InfernalBotManager Client");
						exitWaitRunnable.exit();
					}
				}
			}
		} else {
			launchError = true;
		}
		if(launchError){
			if(killswitch){
				LOGGER.info("Infernalbotmanager is currently disabled, try again later");
			} else {
				if(!outdated && !badconfig){
					LOGGER.info("Client failed to launch, fix your set-up and relaunch");
				}
			}
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
		//connection has been made start the monitor & update & client action checker threads
		if(!exitWaitRunnable.getExit()){
			startMonitorThread(infernalBotManagerClient);
			startUpdateCheckerThread(infernalBotManagerClient);
			startClientActionCheckerThread(infernalBotManagerClient);
		}

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
			managerMonitorRunnable.setClientDataStatus(ClientDataStatus.INIT);
			//start infernalbot checker in a thread
			startInfernalCheckerThread();
			
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

	private static Optional<OAuth2RestTemplate> buildRestTemplate(InfernalBotManagerGUI gui, IniSettings iniSettings){
		String uriServer = "";
		
		if(iniSettings.getPort().equals("")){
			uriServer = iniSettings.getWebServer();
		} else {
			uriServer = iniSettings.getWebServer() + ":" + iniSettings.getPort();
		}
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
		
		List<String> scopes = new ArrayList<>(2);
		scopes.add("write");
		scopes.add("read");
		resource.setAccessTokenUri(uriServer + "/oauth/token");
		resource.setClientId("infernalbotmanager");
		resource.setClientSecret("secret");
		resource.setGrantType("password");
		resource.setScope(scopes);
		resource.setUsername(iniSettings.getUsername());
		resource.setPassword(iniSettings.getPassword());
		OAuth2RestTemplate restTemplate =  new OAuth2RestTemplate(resource, new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest()));
		restTemplate.setErrorHandler(new RestErrorHandler());
		
		do{
			try{
				restTemplate.getAccessToken();
				gui.changeTitle("IBMC - Connected");
				gui.setIconConnected();
				return Optional.of(restTemplate);
			} catch (OAuth2AccessDeniedException e){
				if(e.getMessage().equals("Access token denied.")){
					LOGGER.info("Access to the server denied, check your login details.");
					return Optional.empty();
				}
				if(e.getMessage().equals("Error requesting access token.")){
					if(e.getHttpErrorCode() == 403){
						LOGGER.info("IP blocked due to too many failed attempts, contact Alco");
					} else {
						if(InternetAvailabilityChecker.isInternetAvailable()){
							LOGGER.info("Server may be having issues, contact Alco");
							gui.changeTitle("IBMC - Disconnected - Server Down");
							gui.setIconDisconnected();
						} else {
							LOGGER.info("No available connection found, check you internet settings");
							gui.changeTitle("IBMC - Disconnected - Internet Down");
							gui.setIconDisconnected();
						}
					}
				}
				LOGGER.debug("Handled exception: " + e.getClass().getSimpleName());
			} catch (Exception e){
				LOGGER.debug("Unhandled exception:", e);
			}
			try {
				LOGGER.info("Retrying in 1 minute...");
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e) {
				LOGGER.debug(e.getMessage());
			}
		} while (true);
	}
	
	private static Optional<User> buildUser(IniSettings iniSettings, OAuth2RestTemplate restTemplate){
		UserService userService = new UserService(restTemplate);
		User user = userService.getUser(iniSettings.getUsername());
		if (user != null){
			return Optional.of(user);
		} else {
			return Optional.empty();
		}
	}
	
	private static Optional<Client> buildClient(IniSettings iniSettings, OAuth2RestTemplate restTemplate, User user){
		ClientService clientService = new ClientService(restTemplate);
		Client client = clientService.getClient(user.getId(), iniSettings.getClientTag());
		if (client != null){
			return Optional.of(client);
		} else {
			return Optional.empty();
		}
	}
	
	private static Boolean checkClientHWID(IniSettings iniSettings,OAuth2RestTemplate restTemplate, Client client) {
		String clientHWID = client.getHWID();
		String computerHWID = systemMonitor.getHWID();
		
		if (clientHWID.trim().equals(computerHWID.trim())){
			return true;
		}
		
		if (clientHWID.trim().isEmpty()){
			LOGGER.info("Registering HWID " + computerHWID + " for client '" + client.getTag() +"'.");
			ClientService clientService = new ClientService(restTemplate);
			return clientService.registerHWID(client.getUser().getId(), client.getId(), computerHWID);
		} else {
			LOGGER.info("Incorrect HWID for for client '" + client.getTag() +"'.");
			return false;
		}

	}

	//update client methods
	public static void updateClient(IniSettings iniSettings) {
		if(ProgramUtil.downloadFileFromUrl(iniSettings, ProgramConstants.UPDATER_NAME)){
			//vars
			String managerMap = System.getProperty("user.dir");
			Path updaterDownloadPath = Paths.get(managerMap + "\\downloads\\" + ProgramConstants.UPDATER_NAME);
			Path updaterPath = Paths.get(managerMap + "\\" + ProgramConstants.UPDATER_NAME);
			
			//move the updater
			try {
				Files.copy(updaterDownloadPath,updaterPath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				LOGGER.error("Failure moving the updater");
				LOGGER.debug(e.getMessage());
			}
			try{
				//build the args
				String arg0 = managerMap;
				String arg1 = "";
				String arg2 = "";
				if(iniSettings.getPort().equals("")){
					arg1 = iniSettings.getWebServer() + "/downloads/"; 
				} else {
					arg1 = iniSettings.getWebServer() + ":" + iniSettings.getPort() + "/downloads/"; 
				}
				if(ProgramVariables.softStop){
					//softStop = stopStart aswell, pass the parameter to the updater so it can pass it back to the program
					arg2 = "soft";
				} else {
					arg2 = "hard";
				}
				String command = updaterPath.toString();
				//Start the updater
				LOGGER.info("Starting updater");
				ProcessBuilder pb = new ProcessBuilder(command,arg0,arg1,arg2);
				pb.directory(new File(managerMap));
				@SuppressWarnings("unused")
				Process p = pb.start();
				} catch (IOException e) {
					LOGGER.error("Failed to start the updater");
					LOGGER.debug(e.getMessage());
				}
		} else{
			LOGGER.error("Failed to download the updater");
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
		managerMonitorRunnable = new ClientDataRunnable(systemMonitor,client);
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
	
	private static void startClientActionCheckerThread(InfernalBotManagerClient client){
		clientActionCheckerRunnable = new ClientActionCheckerRunnable(client);
		Thread clientActionCheckerThread = new Thread(clientActionCheckerRunnable);
		threadMap.put(clientActionCheckerThread, clientActionCheckerRunnable);
		clientActionCheckerThread.setDaemon(false); 
		clientActionCheckerThread.setName("Client Action Checker Thread");
		clientActionCheckerThread.start();
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
			LOGGER.debug("Handled exception: " + e.getClass().getSimpleName());
			LOGGER.debug("Failure trying to disable Windows error reporting UI");
		}
		
	}
	

}
