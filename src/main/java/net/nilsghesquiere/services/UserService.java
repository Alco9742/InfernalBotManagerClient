package net.nilsghesquiere.services;

import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.managerclients.UserManagerClient;
import net.nilsghesquiere.managerclients.UserManagerRESTClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	private final UserManagerClient managerClient;
	
	public UserService(ClientSettings clientSettings){
		if(clientSettings.getPort().equals("")){
			this.managerClient = new UserManagerRESTClient(clientSettings.getWebServer(), clientSettings.getUsername(), clientSettings.getPassword(), clientSettings.getDebugHTTP());
		} else {
			this.managerClient = new UserManagerRESTClient(clientSettings.getWebServer() + ":" + clientSettings.getPort(), clientSettings.getUsername(), clientSettings.getPassword(), clientSettings.getDebugHTTP());
		}
	}
	
	public Long getUserId(String username){
		return managerClient.getUserIdByUsername(username);
	}
}