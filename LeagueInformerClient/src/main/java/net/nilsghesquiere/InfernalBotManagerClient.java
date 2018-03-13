package net.nilsghesquiere;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import lombok.Data;
import net.nilsghesquiere.entities.ClientData;
import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.services.GlobalVariableService;
import net.nilsghesquiere.services.InfernalSettingsService;
import net.nilsghesquiere.services.LolAccountService;
import net.nilsghesquiere.services.ClientDataService;
import net.nilsghesquiere.util.ProgramUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

@Data
public class InfernalBotManagerClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalBotManagerClient.class);
	private static final String UPDATER_NAME = "updater.bat";
	private static final String PROGRAM_NAME = "InfernalBotManagerClient.jar";
	
	private ClientSettings clientSettings;
	private ClientData clientData;
	
	private GlobalVariableService globalVariableService;
	private LolAccountService accountService;
	private InfernalSettingsService infernalSettingsService;
	private ClientDataService clientDataService;

	public InfernalBotManagerClient(ClientSettings clientSettings) {
		this.clientSettings = clientSettings;
		this.clientData = new ClientData(clientSettings.getClientTag());
		this.globalVariableService = new GlobalVariableService(clientSettings);
		this.accountService = new LolAccountService(clientSettings);
		this.infernalSettingsService = new InfernalSettingsService(clientSettings);
		this.clientDataService = new ClientDataService(clientSettings, clientData);
	}
	
	//Schedule Reboot
	public void scheduleReboot(){
		if (clientSettings.getReboot()){
			try {
				//Process p = Runtime.getRuntime().exec("shutdown -r -t " + clientSettings.getRebootTime());
				Runtime.getRuntime().exec("shutdown -r -t " + clientSettings.getRebootTime());
			} catch (IOException e) {
				LOGGER.error("Error scheduling reboot");
				LOGGER.debug(e.getMessage().toString());
			}
			LOGGER.info("Shutdown scheduled in " + clientSettings.getRebootTime() + " seconds");
		}
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
		try{
			return globalVariableService.checkVersion();
		} catch (ResourceAccessException e) {
			LOGGER.error("Failure retrieving the requested resource.");
			LOGGER.debug(e.getMessage());
				return false;
		} 
	}
	
	public boolean checkKillSwitch(){
		try{
			return globalVariableService.checkKillSwitch();
		} catch (ResourceAccessException e) {
			LOGGER.error("Failure retrieving the requested resource.");
			LOGGER.debug(e.getMessage());
				return false;
		} 
	}
	
	//InfernalSettings methods
	public boolean setInfernalSettings(){
		if (clientSettings.getFetchSettings()){
			try{
				infernalSettingsService.updateInfernalSettings(clientSettings.getUserId());
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
	
	//LolAccount methods
	public boolean exchangeAccounts(){
		try{
			return accountService.exchangeAccounts();
		} catch (ResourceAccessException e) {
			LOGGER.error("Failure retrieving the requested resource");
			LOGGER.debug(e.getMessage());
				return false;
		} 
	}
	
	public boolean setAccountsAsReadyForUse(){
		try{
			accountService.setAccountsAsReadyForUse();
			return true;
		} catch (ResourceAccessException e) {
			LOGGER.error("Failure retrieving the requested resource");
			LOGGER.debug(e.getMessage());
			return false;
		} 
	}
	
	//Queuer methods
	public void queuertest(){
		clientDataService.sendData();
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
