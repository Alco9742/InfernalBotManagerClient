package net.nilsghesquiere;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import lombok.Data;
import net.nilsghesquiere.entities.Client;
import net.nilsghesquiere.entities.IniSettings;
import net.nilsghesquiere.entities.Queuer;
import net.nilsghesquiere.services.ClientDataService;
import net.nilsghesquiere.services.GlobalVariableService;
import net.nilsghesquiere.services.InfernalSettingsService;
import net.nilsghesquiere.services.LolAccountService;
import net.nilsghesquiere.services.UserService;
import net.nilsghesquiere.util.ProgramConstants;
import net.nilsghesquiere.util.ProgramUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

@Data
public class InfernalBotManagerClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalBotManagerClient.class);
	
	private boolean infernalSettingsPragmasOK;
	private boolean lolAccountPragmasOK;
	private boolean serverUpToDate = true;
	
	private IniSettings iniSettings;
	
	private Client client;
	
	private UserService userService;
	private GlobalVariableService globalVariableService;
	private LolAccountService accountService;
	private InfernalSettingsService infernalSettingsService;
	private ClientDataService clientDataService;

	public InfernalBotManagerClient(IniSettings iniSettings, Client client){
		this.iniSettings = iniSettings;
		this.client = client;
		this.userService = new UserService(iniSettings);
		this.globalVariableService = new GlobalVariableService(iniSettings);
		this.accountService = new LolAccountService(client, iniSettings);
		this.infernalSettingsService = new InfernalSettingsService(client, iniSettings);
		this.clientDataService = new ClientDataService(client, iniSettings);
	}
	
	//Schedule Reboot
	public void scheduleReboot(){
		if (client.getClientSettings().getReboot()){
			if (ProgramUtil.scheduleReboot(client.getClientSettings().getRebootTime())){
				LOGGER.info("Shutdown scheduled in " + client.getClientSettings().getRebootTime() + " seconds");
			}
		}
	}
	
	//Check if tables have been edited since last version (don't do any updates to settings or accounts if so)
	public void checkTables(){
		infernalSettingsPragmasOK = infernalSettingsService.checkPragmas();
		lolAccountPragmasOK = accountService.checkPragmas();
	}
	
	//Check connection to riot server, infernalbot server & infernalbotmanager server
	public boolean checkConnectionComplete(){
		//TODO implements checks for riot & infernal connection
		boolean connectedToRiot = true;
		boolean connectedToInfernal = true;
		boolean connectedToManager = checkConnection();
		if(connectedToRiot && connectedToInfernal && connectedToManager){
			return true;
		} else {
			return false;
		}
	}
	
	//check if lol is up to date TODO implement
	public boolean lolUpToDate(){
		return true;
	}
	
	//check if infernal is up to date TODO implement
	public boolean infernalUpToDate(){
		return true;
	}

	//GlobalVariables methods
	public boolean checkConnection(){
		try{
			boolean connected =  globalVariableService.checkConnection();
			if(!connected){
				LOGGER.error("Failure connecting to the InfernalBotManager server.");
			}
			return globalVariableService.checkConnection();
		} catch (ResourceAccessException e) {
			LOGGER.error("Failure connecting to the InfernalBotManager server.");
			LOGGER.debug(e.getMessage());
				return false;
		}
	}
	
	public boolean checkClientVersion(){
		return globalVariableService.checkClientVersion();
	}
	
	public boolean checkClientVersion(boolean log) {
		return globalVariableService.checkClientVersion(log);
	}
	
	
	public boolean checkServerVersion(){
		return globalVariableService.checkServerVersion();
	}
	
	public boolean checkKillSwitch(){
		return globalVariableService.checkKillSwitch();
	}
	
	public boolean checkUpdateNow() {
		return globalVariableService.checkUpdateNow();
	}
	

	//InfernalSettings methods
	public boolean setInfernalSettings(){
		if(Main.softStart){
			LOGGER.debug("Not performing settings exchange (softstart)");
			return true;
		}
		if (client.getClientSettings().getFetchInfernalSettings()){
			if(!Main.serverUpToDate){
				LOGGER.info("InfernalBotManager server is outdated.");
				LOGGER.info("Falling back to InfernalBots own settings until updated.");
				LOGGER.debug("Not performing settings exchange (server outdated)");
				return true;
			}
			if(infernalSettingsPragmasOK){
				return infernalSettingsService.updateInfernalSettings(client.getUser().getId());
			} else {
				LOGGER.info("InfernalBot settings table has changed since latest InfernalBotManager version");
				LOGGER.info("Falling back to InfernalBots own settings until updated.");
				return true;
			}
		} else {
			LOGGER.info("Not requesting settings from the InfernalBotManager Server, using InfernalBots own settings.");
			return true;
		}
	}
	
	//LolAccount methods
	public boolean initialExchangeAccounts(){
		if(Main.softStart){
			LOGGER.debug("Not performing account exchange (softstart)");
			return true;
		}
		if(!Main.serverUpToDate){
			LOGGER.info("InfernalBotManager server is outdated.");
			LOGGER.info("Falling back to InfernalBots own accounts until updated.");
			LOGGER.debug("Not performing account exchange (server outdated)");
			return true;
		}
		if (lolAccountPragmasOK){
			return accountService.exchangeAccounts();
		} else {
			LOGGER.info("InfernalBot accountlist table has changed since latest InfernalBotManager version");
			LOGGER.info("Falling back to InfernalBots own accounts until updated.");
			return true;
		}
	}
	
	public boolean exchangeAccounts(){
		if(!Main.serverUpToDate){
			LOGGER.info("InfernalBotManager server is outdated.");
			LOGGER.info("Falling back to InfernalBots own accounts until updated.");
			LOGGER.debug("Not performing account exchange (server outdated)");
			return true;
		}
		if (lolAccountPragmasOK){
			return accountService.exchangeAccounts();
		} else {
			LOGGER.info("InfernalBot accountlist table has changed since latest InfernalBotManager version");
			LOGGER.info("Falling back to InfernalBots own accounts until updated.");
			return true;
		}
	}
	
	public boolean setAccountsAsReadyForUse(){
		if (!Main.serverUpToDate){
			LOGGER.debug("Not setting accounts as ready for use (server outdated)");
			return true; // niet doen als server outdated is
		}
		if (Main.softStop){
			LOGGER.debug("Not setting accounts as ready for use (softstop)");
			return true; // niet doen als server outdated is
		}
		if (lolAccountPragmasOK){
			return accountService.setAccountsAsReadyForUse();
		} else {
			return true; //niet doen als pragmas outdated zijn
		}
	}
	
	
	public void updateAccountsOnServer(){
		if (Main.serverUpToDate){ 
			if (!Main.softStop){
				accountService.updateAccountsOnServer();
			} else {
				LOGGER.debug("Not updating accounts on server (softstop)");
			}
		} else {
			LOGGER.debug("Not updating accounts on server (server outdated)");
		}
	}
	
	//ClientData methods
	public void deleteAllQueuers(){
		if(!Main.softStart){
			clientDataService.deleteAllQueuers();
		} else {
			LOGGER.debug("Not deleting queuers (softstart)");
		}

	}
	
	public void sendData(String status, String ramInfo, String cpuInfo){
		if (Main.serverUpToDate){
			clientDataService.sendData(status, ramInfo, cpuInfo);
		} //don't log this, too many times
	}
	
	public boolean queuersHaveEnoughAccounts(){
		//After that do a double check by looking if there are any queuers with less than 5 accounts
		//This scenario happens when there are not enough accounts in the account list (bans etc etc)
		//The client will make a queuer with not enough accounts and gets a popup with "need 5 accounts)
		int activeAccounts = accountService.countActiveAccounts();
		int neededAccounts = client.getClientSettings().getAccountAmount();
		//Check if there are enough active accounts in the list
		if (activeAccounts < neededAccounts){
			//check if there are any queuers running with < 5 accounts
			for(Queuer queuer : clientDataService.getAllQueuers()){
				if(queuer.getQueuerLolAccounts().size() < 5){
					return false;
				}
			}
		}
		return true;
	}
	
	//Backup database methods
	public boolean backUpInfernalDatabase(){
		if(checkDir()){
			LOGGER.info("Located Infernalbot");
			
			Path backupDir = client.getClientSettings().getInfernalMap().resolve("InfernalBotManager");
			Path file = client.getClientSettings().getInfernalMap().resolve("InfernalDatabase.sqlite") ;
			Path backupFile = client.getClientSettings().getInfernalMap().resolve("InfernalBotManager/InfernalDatabase.bak") ;
			if(!Files.exists(backupDir)){
				try {
					Files.createDirectories(backupDir);
				} catch (IOException e1) {
					//Path exists, do nothing
				}
			}
			if (Files.exists(file)){
				try {
					Files.copy(file,backupFile, StandardCopyOption.REPLACE_EXISTING);
					LOGGER.info("Backed up Infernalbot database" );
				} catch (IOException e) {
					LOGGER.error("Failure backing up Infernal Database: " + e.getMessage());
					LOGGER.debug(e.getMessage());
					return false;
				}
			} else {
				LOGGER.error("Infernalbot database not found, check your path.");
				return false;
			}
		} else {
			LOGGER.error("Failure locating Infernalbot");
			return false;
		}
		return true;
	}
	
	private boolean checkDir(){
		Path infernalPath = client.getClientSettings().getInfernalMap().resolve(client.getClientSettings().getInfernalProgramName());
		if(Files.exists(infernalPath)){
			return  true;
		} else {
			LOGGER.debug("'" + client.getClientSettings().getInfernalProgramName() + "' not found at path: " + infernalPath);
			return false;
		}
		
	}

	//update client methods
	public void updateClient() {
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
				if(Main.softStop){
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

	//TEST methods
	
	public void testPragmas(){
		infernalSettingsService.testPragmas();
		accountService.testPragmas();
	}
}

