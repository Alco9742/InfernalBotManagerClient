package net.nilsghesquiere.services;

import net.nilsghesquiere.entities.GlobalVariable;
import net.nilsghesquiere.managerclients.GlobalVariableManagerClient;
import net.nilsghesquiere.managerclients.GlobalVariableManagerRESTClient;
import net.nilsghesquiere.util.ProgramConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;

public class GlobalVariableService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalVariableService.class);
	private final GlobalVariableManagerClient managerClient;
	
	public GlobalVariableService(OAuth2RestTemplate restTemplate){
		this.managerClient = new GlobalVariableManagerRESTClient(restTemplate);
	}
	
	public boolean checkClientVersion(){
		String currentVersion = getClientVersion();
		if(ProgramConstants.CLIENT_VERSION.equals(currentVersion)){
			LOGGER.info("Client up to date (v" + currentVersion +")");
			return true;
		} else {
			LOGGER.error("Client is outdated (v" + ProgramConstants.CLIENT_VERSION +")");
			LOGGER.info("Updating to new version (v" +currentVersion +")");
			return false;
		}
	}
	
	public boolean checkClientVersion(boolean log){
		String currentVersion = getClientVersion();
		if(ProgramConstants.CLIENT_VERSION.equals(currentVersion)){
			if(log){
				LOGGER.info("Client up to date (v" + currentVersion +")");
			}
			return true;
		} else {
			if (log){
				LOGGER.error("Client is outdated (v" + ProgramConstants.CLIENT_VERSION +")");
				LOGGER.info("Updating to new version (v" +currentVersion +")");
			}
			return false;
		}
	}
	
	public boolean checkServerVersion(){
		String currentVersion = getServerVersion();
		if (currentVersion == null){
			return true;
		}
		if(ProgramConstants.SERVER_VERSION.equals(currentVersion)){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkKillSwitch(){
		String killSwitch = getKillSwitch();
		if (killSwitch == null){
			return false;
		}
		if(killSwitch.equals("on")){
			LOGGER.error(getKillSwitchMessage());
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkConnection(){
		String connection = getConnection();
		if (connection == null){
			return false;
		}
		if(connection.equals("Connected")){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkUpdateNow(){
		String update = getUpdate();
		if (update == null){
			return false;
		}
		if(update.equals("now")){
			return true;
		} else {
			return false;
		}
	}
	
	private String getClientVersion(){
		return getVar("clientVersion");
	}
	
	private String getServerVersion(){
		return getVar("serverVersion");
	}
	
	private String getKillSwitch(){
		return getVar("killSwitch");
	}
	
	private String getKillSwitchMessage(){
		return getVar("killSwitchMessage");
	}
	
	private String getConnection(){
		return getVar("connection");
	}
	
	private String getUpdate(){
		return getVar("update");
	}
	
	private String getVar(String varName){
		GlobalVariable var = managerClient.getGlobalVariableByName(varName);
		if (var != null){
			return var.getValue();
		}
		return null;
	}
}