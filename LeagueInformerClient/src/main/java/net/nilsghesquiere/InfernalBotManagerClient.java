package net.nilsghesquiere;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

import lombok.Data;
import net.nilsghesquiere.entities.ClientData;
import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.services.ClientDataService;
import net.nilsghesquiere.services.GlobalVariableService;
import net.nilsghesquiere.services.InfernalSettingsService;
import net.nilsghesquiere.services.LolAccountService;
import net.nilsghesquiere.services.UserService;
import net.nilsghesquiere.util.ProgramConstants;
import net.nilsghesquiere.util.ProgramUtil;

@Data
public class InfernalBotManagerClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalBotManagerClient.class);
	
	private boolean infernalSettingsPragmasOK;
	private boolean lolAccountPragmasOK;
	
	private ClientSettings clientSettings;
	private ClientData clientData;
	
	private UserService userService;
	private GlobalVariableService globalVariableService;
	private LolAccountService accountService;
	private InfernalSettingsService infernalSettingsService;
	private ClientDataService clientDataService;

	public InfernalBotManagerClient(ClientSettings clientSettings) {
		this.clientSettings = clientSettings;
		this.clientData = new ClientData(clientSettings.getClientTag());
		this.userService = new UserService(clientSettings);
		this.globalVariableService = new GlobalVariableService(clientSettings);
		this.accountService = new LolAccountService(clientSettings);
		this.infernalSettingsService = new InfernalSettingsService(clientSettings);
		this.clientDataService = new ClientDataService(clientSettings, clientData);
	}
	
	//Schedule Reboot
	public void scheduleReboot(){
		if (clientSettings.getReboot()){
			try {
				Runtime.getRuntime().exec("shutdown -r -t " + clientSettings.getRebootTime());
			} catch (IOException e) {
				LOGGER.error("Error scheduling reboot");
				LOGGER.debug(e.getMessage().toString());
			}
			LOGGER.info("Shutdown scheduled in " + clientSettings.getRebootTime() + " seconds");
		}
	}
	
	//Check if tables have been edited since last version (don't do any updates to settings or accounts if so)
	public void checkTables(){
		infernalSettingsPragmasOK = infernalSettingsService.checkPragmas();
		lolAccountPragmasOK = accountService.checkPragmas();
	}
	
	
	//User methods
	public boolean setUserId() {
		try{
			Long id = userService.getUserId(clientSettings.getUsername());
			if (id == null){
				LOGGER.error("User '" +clientSettings.getUsername() +  "' not found on the server.");
				return false;
			} else {
				clientSettings.setUserId(id);
				return true;
			}
		} catch (ResourceAccessException e) {
			LOGGER.error("Failure connecting to the InfernalBotManager server.");
			LOGGER.debug(e.getMessage());
				return false;
		}
	}
	public Long getUserId(){
		return userService.getUserId(clientSettings.getUsername());
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
	
	public boolean checkVersion(){
		return globalVariableService.checkVersion();
	}
	
	public boolean checkKillSwitch(){
		return globalVariableService.checkKillSwitch();
	}
	
	//InfernalSettings methods
	public boolean setInfernalSettings(){
		if (clientSettings.getFetchSettings()){
			if(infernalSettingsPragmasOK){
				return infernalSettingsService.updateInfernalSettings(clientSettings.getUserId());
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
	public boolean exchangeAccounts(){
		if (lolAccountPragmasOK){
			return accountService.exchangeAccounts();
		} else {
			LOGGER.info("InfernalBot accountlist table has changed since latest InfernalBotManager version");
			LOGGER.info("Falling back to InfernalBots own accounts until updated.");
			return true;
		}
	}
	
	public boolean setAccountsAsReadyForUse(){
		if (lolAccountPragmasOK){
			return accountService.setAccountsAsReadyForUse();
		} else {
			//No need to repeat logger info
			return true;
		}
	}
	
	//Queuer methods
	public void deleteAllQueuers(){
		clientDataService.deleteAllQueuers();
	}
	
	//Backup database methods
	public boolean backUpInfernalDatabase(){
		if(checkDir()){
			LOGGER.info("Located Infernalbot");
			Path backupDir = Paths.get(clientSettings.getInfernalMap() + "InfernalBotManager") ;
			Path file = Paths.get(clientSettings.getInfernalMap() + "InfernalDatabase.sqlite") ;
			Path backupFile = Paths.get(clientSettings.getInfernalMap() + "InfernalBotManager/InfernalDatabase.bak") ;
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
		Path infernalPath = Paths.get(clientSettings.getInfernalMap() + clientSettings.getInfernalProgramName());
		return Files.exists(infernalPath);
	}

	//update client methods
	public void updateClient() {
		if(ProgramUtil.downloadFileFromUrl(clientSettings, ProgramConstants.UPDATER_NAME)){
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
				if(clientSettings.getPort().equals("")){
					arg1 = "http://" + clientSettings.getWebServer() + "/downloads/"; 
				} else {
					arg1 = "http://" + clientSettings.getWebServer() + ":" + clientSettings.getPort() + "/downloads/"; 
				}
				String command = "\"" + updaterPath.toString() + "\" \"" + arg0 + "\" \"" + arg1 + "\"";
	
				//Start the updater
				LOGGER.info("Starting updater");
				LOGGER.info(command);
				ProcessBuilder pb = new ProcessBuilder(command);
				pb.directory(new File(managerMap));
				Process p = pb.start();
				} catch (IOException e) {
					LOGGER.error("Failed to start the updater");
					LOGGER.debug(e.getMessage());
				}
		} else{
			LOGGER.error("Failed to download the updater");
		}
	}


}

