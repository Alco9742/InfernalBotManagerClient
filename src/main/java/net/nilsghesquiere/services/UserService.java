package net.nilsghesquiere.services;

import net.nilsghesquiere.entities.User;
import net.nilsghesquiere.managerclients.UserManagerClient;
import net.nilsghesquiere.managerclients.UserManagerRESTClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;

public class UserService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	private final UserManagerClient managerClient;
	
	public UserService(OAuth2RestTemplate restTemplate){
		this.managerClient = new UserManagerRESTClient(restTemplate);
	}
	
	public Long getUserId(String username){
		return managerClient.getUserIdByUsername(username);
	}
	
	public User getUser(String username){
		return managerClient.getUserByUsername(username);
	}
}