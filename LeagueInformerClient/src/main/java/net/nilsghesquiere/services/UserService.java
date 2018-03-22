package net.nilsghesquiere.services;

import net.nilsghesquiere.Main;
import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.restclients.GlobalVariableRestClient;
import net.nilsghesquiere.restclients.UserRestClient;
import net.nilsghesquiere.util.ProgramConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	private final UserRestClient restClient;
	
	public UserService(ClientSettings clientSettings){
		this.restClient = new UserRestClient("http://" + clientSettings.getWebServer() + ":" + clientSettings.getPort(), clientSettings.getUsername(), clientSettings.getPassword());
	}
	
	public Long getUserId(String username){
		return restClient.getUserIdByUsername(username);
	}
}