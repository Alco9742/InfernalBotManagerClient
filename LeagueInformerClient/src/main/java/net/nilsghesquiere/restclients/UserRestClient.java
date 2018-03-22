package net.nilsghesquiere.restclients;

import java.io.IOException;

import net.nilsghesquiere.entities.GlobalVariable;
import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.util.wrappers.GlobalVariableSingleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class UserRestClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserRestClient.class);
	private final String URI_USERS;
	private RestTemplate restTemplate = new RestTemplate();
	
	public UserRestClient(String uriServer, String username, String password) {
		this.URI_USERS = uriServer +"/api/users";
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
	}
	
	public Long getUserIdByUsername(String username){
		try{
			Long jsonResponse = restTemplate.getForObject(URI_USERS + "/username/" + username, Long.class);
			return jsonResponse;
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure getting user ID from the server");
			LOGGER.debug(e.getMessage());
			return null;
		}
	}
}
