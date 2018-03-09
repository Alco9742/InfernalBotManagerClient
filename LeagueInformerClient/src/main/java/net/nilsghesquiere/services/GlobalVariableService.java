package net.nilsghesquiere.services;

import net.nilsghesquiere.Main;
import net.nilsghesquiere.entities.InfernalBotManagerClientSettings;
import net.nilsghesquiere.restclients.GlobalVariableRestClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalVariableService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalVariableService.class);
	private final GlobalVariableRestClient restClient;
	
	public GlobalVariableService(InfernalBotManagerClientSettings clientSettings){
		this.restClient = new GlobalVariableRestClient("http://" + clientSettings.getWebServer() + ":" + clientSettings.getPort());
		
	}
	
	public boolean checkVersion(){
		String currentVersion = getClientVersion();
		if(Main.CLIENT_VERSION.equals(currentVersion)){
			LOGGER.info("Client up to date (v" + currentVersion +")");
			return true;
		} else {
			LOGGER.error("Client is outdated (v" + Main.CLIENT_VERSION +")");
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
		return restClient.getGlobalVariableByName("clientVersion").getValue();
	}
	
	private String getKillSwitch(){
		return restClient.getGlobalVariableByName("killSwitch").getValue().toLowerCase();
	}
	
	private String getKillSwitchMessage(){
		return restClient.getGlobalVariableByName("killSwitchMessage").getValue();
	}
	
	private String getConnection(){
		return restClient.getGlobalVariableByName("connection").getValue();
	}
}