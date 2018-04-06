package net.nilsghesquiere.managerclients;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import net.nilsghesquiere.security.SSLBasicAuthenticationRestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class UserManagerRESTClient implements UserManagerClient{
	private static final Logger LOGGER = LoggerFactory.getLogger(UserManagerRESTClient.class);
	private final String URI_USERS;
	private RestTemplate restTemplate;
	
	public UserManagerRESTClient(String uriServer, String username, String password, Boolean debugHTTP) {
		this.URI_USERS = uriServer +"/api/users";
		try {
			this.restTemplate = new SSLBasicAuthenticationRestTemplate(username,password,debugHTTP);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			LOGGER.debug(e.getMessage());
		}	
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
