package net.nilsghesquiere;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import lombok.Data;
import net.nilsghesquiere.entities.InfernalBotManagerClientSettings;
import net.nilsghesquiere.services.LolAccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

@Data
public class InfernalBotManagerClient {
	private static final Logger LOGGER = LoggerFactory.getLogger("InfernalBotManagerClient");
	private InfernalBotManagerClientSettings clientSettings;
	
	public InfernalBotManagerClient(InfernalBotManagerClientSettings clientSettings) {
		this.clientSettings = clientSettings;
	}
	
	public void scheduleReboot(){
		if (clientSettings.getReboot()){
			try {
				Process p = Runtime.getRuntime().exec("shutdown -r -t " + clientSettings.getRebootTime());
			} catch (IOException e) {
				LOGGER.info("Error scheduling reboot");
				LOGGER.debug(e.getMessage().toString());
			}
			LOGGER.info("Shutdown scheduled in " + clientSettings.getRebootTime() + " seconds");
		}
	}

	public boolean checkConnection(){
		//TODO try /catch
		//Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 8.8.8.8");
		if (clientSettings.getBypassDevChecks() == false){
			try{
				Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 google.be");
				int returnVal = p1.waitFor();
				boolean reachable = (returnVal==0);
				if (reachable){
					LOGGER.info("Successfully connected to network");
					Process p2 = java.lang.Runtime.getRuntime().exec("ping -n 1 " + clientSettings.getWebServer());
					int returnVal2 = p2.waitFor();
					boolean reachable2 = (returnVal2==0);
					if (reachable2){
						LOGGER.info("Successfully connected to the InfernalBotManager server");
					} else {
						LOGGER.info("Error connecting to the InfernalBotManager server");
						return false;
					}
				} else {
					LOGGER.info("Error connecting to network");
					return false;
				}
			}catch (IOException | InterruptedException e ){
				LOGGER.info("Error establishing connection: " + e.getMessage());
				LOGGER.debug(e.getMessage());
				return false;
			}
		}
		return true;
	}
	
	public boolean accountExchange(){
		if (backUpInfernalDatabase()){
			try{
				LolAccountService lolAccountService = new LolAccountService(clientSettings.getInfernalMap(), "http://" + clientSettings.getWebServer() + ":" + clientSettings.getPort());
				lolAccountService.exchangeAccounts(clientSettings.getUserId(), clientSettings.getClientRegion(), clientSettings.getClientTag(), clientSettings.getAccountAmount());
				return true;
			} catch (ResourceAccessException e) {
				LOGGER.info("Error retrieving the requested resource");
				LOGGER.debug(e.getMessage());
				return false;
			} 
		} else {
			return false;
		}
	}

	private boolean backUpInfernalDatabase(){
		if(checkDir()){
			LOGGER.info("Successfully located Infernalbot");
			Path backupDir = Paths.get(clientSettings.getInfernalMap() + "InfernalManager") ;
			Path file = Paths.get(clientSettings.getInfernalMap() + "InfernalDatabase.sqlite") ;
			Path backupFile = Paths.get(clientSettings.getInfernalMap() + "InfernalManager/InfernalDatabase.bak") ;
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
					LOGGER.info("Succesfully backed up Infernalbot database" );
				} catch (IOException e) {
					LOGGER.info("Error backing up Infernal Database: " + e.getMessage());
					LOGGER.debug(e.getMessage());
					return false;
				}
			} else {
				LOGGER.info("Infernalbot database not found, check your path.");
				return false;
			}
		} else {
			LOGGER.info("Error: couldn't locate Infernalbot");
			return false;
		}
		return true;
	}
	
	private boolean checkDir(){
		Path infernalPath = Paths.get(clientSettings.getInfernalMap() + clientSettings.getInfernalProgname());
		return Files.exists(infernalPath);
	}
	
}
