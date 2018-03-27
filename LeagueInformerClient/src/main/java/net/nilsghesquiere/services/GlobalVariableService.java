package net.nilsghesquiere.services;

import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.managerclients.GlobalVariableManagerClient;
import net.nilsghesquiere.managerclients.GlobalVariableManagerRESTClient;
import net.nilsghesquiere.util.ProgramConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalVariableService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalVariableService.class);
	private final GlobalVariableManagerClient managerClient;
	
	public GlobalVariableService(ClientSettings clientSettings){
		this.managerClient = new GlobalVariableManagerRESTClient("http://" + clientSettings.getWebServer() + ":" + clientSettings.getPort(), clientSettings.getUsername(), clientSettings.getPassword());
		
	}
	
	public boolean checkVersion(){
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
	
	public boolean checkKillSwitch(){
		String killSwitch = getKillSwitch();
		if(killSwitch.equals("on")){
			LOGGER.error(getKillSwitchMessage());
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkConnection(){
		String connection = getConnection();
		if(connection.equals("Connected")){
			return true;
		} else {
			return false;
		}
	}
	
	private String getClientVersion(){
		return managerClient.getGlobalVariableByName("clientVersion").getValue();
	}
	
	private String getKillSwitch(){
		return managerClient.getGlobalVariableByName("killSwitch").getValue().toLowerCase();
	}
	
	private String getKillSwitchMessage(){
		return managerClient.getGlobalVariableByName("killSwitchMessage").getValue();
	}
	
	private String getConnection(){
		return managerClient.getGlobalVariableByName("connection").getValue();
	}
}