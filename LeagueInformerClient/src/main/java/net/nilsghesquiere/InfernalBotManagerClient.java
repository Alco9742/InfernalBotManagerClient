package net.nilsghesquiere;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import lombok.Data;
import net.nilsghesquiere.entities.InfernalBotManagerClientSettings;
import net.nilsghesquiere.services.GlobalVariableService;
import net.nilsghesquiere.services.InfernalSettingsService;
import net.nilsghesquiere.services.LolAccountService;
import net.nilsghesquiere.util.ProgramUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

@Data
public class InfernalBotManagerClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalBotManagerClient.class);
	private static final String UPDATER_NAME = "updater.bat";
	private static final String PROGRAM_NAME = "InfernalBotManagerClient.jar";
	
	private InfernalBotManagerClientSettings clientSettings;
	
	public InfernalBotManagerClient(InfernalBotManagerClientSettings clientSettings) {
		this.clientSettings = clientSettings;
	}
	
	//Schedule Reboot
	public void scheduleReboot(){
		if (clientSettings.getReboot()){
			try {
				Process p = Runtime.getRuntime().exec("shutdown -r -t " + clientSettings.getRebootTime());
			} catch (IOException e) {
				LOGGER.error("Error scheduling reboot");
				LOGGER.debug(e.getMessage().toString());
			}
			LOGGER.info("Shutdown scheduled in " + clientSettings.getRebootTime() + " seconds");
		}
	}

	//Connection Check
	public boolean checkConnection(){
		try{
			GlobalVariableService globalVariableService = new GlobalVariableService(clientSettings);
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
		try{
			GlobalVariableService globalVariableService = new GlobalVariableService(clientSettings);
			return globalVariableService.checkVersion();
		} catch (ResourceAccessException e) {
			LOGGER.error("Failure retrieving the requested resource.");
			LOGGER.debug(e.getMessage());
				return false;
		} 
	}
	
	public boolean checkKillSwitch(){
		try{
			GlobalVariableService globalVariableService = new GlobalVariableService(clientSettings);
			return globalVariableService.checkKillSwitch();
		} catch (ResourceAccessException e) {
			LOGGER.error("Failure retrieving the requested resource.");
			LOGGER.debug(e.getMessage());
				return false;
		} 
	}
	//LolAccount methods
	public boolean accountExchange(){
		try{
			LolAccountService lolAccountService = new LolAccountService(clientSettings);
			return lolAccountService.exchangeAccounts(clientSettings.getUserId(), clientSettings.getClientRegion(), clientSettings.getClientTag(), clientSettings.getAccountAmount());
		} catch (ResourceAccessException e) {
			LOGGER.error("Failure retrieving the requested resource");
			LOGGER.debug(e.getMessage());
				return false;
		} 
	}
	
	public boolean setAccountsAsReadyForUse(){
		try{
			LolAccountService lolAccountService = new LolAccountService(clientSettings);
			lolAccountService.setAccountsAsReadyForUse(clientSettings.getUserId());
			return true;
		} catch (ResourceAccessException e) {
			LOGGER.error("Failure retrieving the requested resource");
			LOGGER.debug(e.getMessage());
			return false;
		} 
	}

	//InfernalSettings methods
	public boolean setInfernalSettings(){
		if (clientSettings.getFetchSettings()){
			try{
				InfernalSettingsService infernalSettingsService = new InfernalSettingsService(clientSettings);
				infernalSettingsService.updateInfernalSettings(clientSettings.getUserId());
				//get the settings here and overwrite them if in ini has the values, this should probably be done in the service
				//--> boolean for overwrite and values in a map --> pass to the method
				return true;
			} catch (ResourceAccessException e) {
				LOGGER.error("Failure retrieving the requested resource");
				LOGGER.debug(e.getMessage());
				return false;
			}
		} else {
			LOGGER.info("Not requesting settings from the InfernalBotManager Server, using InfernalBots own settings.");
			return true;
		}
	}
	
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

	public void updateClient() {
		if(ProgramUtil.downloadFileFromUrl(clientSettings, UPDATER_NAME)){
			if(ProgramUtil.downloadFileFromUrl(clientSettings, PROGRAM_NAME)){
				//batch args:
				//1: current program path 
				//2: backup program path
				//3: new program path
				String managerMap = System.getProperty("user.dir");
				String batlocation = managerMap + "\\backup\\updater.bat";
				String param1 = managerMap + "\\" + PROGRAM_NAME;
				String param2 = managerMap + "\\backup\\" + PROGRAM_NAME + ".bak";
				String param3 = managerMap + "\\backup\\" + PROGRAM_NAME;
				String commandString = "\"" + batlocation + "\" \"" +param1 + "\" \"" + param2 + "\" \"" + param3 + "\"";
				try {
					Process proc = Runtime.getRuntime().exec(new String[] {"cmd.exe","/c start",commandString});
				//	 int exitVal = proc.exitValue();
					 try {
						 System.out.println(proc.waitFor());
					 } catch (InterruptedException e){
						 LOGGER.error("Failure, updater got interrupted");
					 }
				} catch (IOException e) {
					LOGGER.error("Failed to start the updater");
					LOGGER.debug(e.getMessage());
				}
			} else{
				LOGGER.error("Failed to download the update");
			}
		} else {
			LOGGER.error("Failed to download the updater");
		}
	}
	
}
