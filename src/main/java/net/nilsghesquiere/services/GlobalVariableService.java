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
		if(clientSettings.getPort().equals("")){
			this.managerClient = new GlobalVariableManagerRESTClient(clientSettings.getWebServer(), clientSettings.getUsername(), clientSettings.getPassword(), clientSettings.getDebugHTTP());
		} else {
			this.managerClient = new GlobalVariableManagerRESTClient(clientSettings.getWebServer() + ":" + clientSettings.getPort(), clientSettings.getUsername(), clientSettings.getPassword(), clientSettings.getDebugHTTP());
		}
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
			return false;
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
		return managerClient.getGlobalVariableByName("clientVersion").getValue();
	}
	
	private String getServerVersion(){
		return managerClient.getGlobalVariableByName("serverVersion").getValue();
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
	
	private String getUpdate(){
		return managerClient.getGlobalVariableByName("update").getValue().toLowerCase();
	}
}