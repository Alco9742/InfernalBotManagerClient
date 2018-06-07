package net.nilsghesquiere.managerclients;

import net.nilsghesquiere.entities.User;
import net.nilsghesquiere.util.wrappers.UserSingleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.client.ResourceAccessException;

public class UserManagerRESTClient implements UserManagerClient{
	private static final Logger LOGGER = LoggerFactory.getLogger(UserManagerRESTClient.class);
	private final String URI_USERS;
	private OAuth2RestTemplate restTemplate;
	
	public UserManagerRESTClient(OAuth2RestTemplate restTemplate) {
		String uriAccesToken = restTemplate.getResource().getAccessTokenUri();
		String uriServer = uriAccesToken.substring(0,uriAccesToken.indexOf("/oauth/token"));
		
		this.URI_USERS = uriServer +"/api/users";
		this.restTemplate = restTemplate;
	}
	
	public Long getUserIdByUsername(String username){
		try{
			Long jsonResponse = restTemplate.getForObject(URI_USERS + "/username/" + username +"/id", Long.class);
			return jsonResponse;
		} catch (ResourceAccessException e){
			LOGGER.debug("Handled exception: " + e.getClass().getSimpleName());
			LOGGER.debug("Client isn't connected to the internet or server is down");
			return null;
		} catch (Exception e){
			LOGGER.debug("Unhandled exception:", e);
			return null;
		}
	}
	
	public User getUserByUsername(String username){
		try{
			UserSingleWrapper jsonResponse = restTemplate.getForObject(URI_USERS + "/username/" + username , UserSingleWrapper.class);
			User user = jsonResponse.getMap().get("data");
			if (user != null){
				LOGGER.info("Received settings for user '" + username + "'");
			}
			return user;
		} catch (ResourceAccessException e){
			LOGGER.debug("Handled exception: " + e.getClass().getSimpleName());
			LOGGER.debug("Client isn't connected to the internet or server is down");
			return null;
		} catch (Exception e){
			LOGGER.debug("Unhandled exception:", e);
			return null;
		}
	}
}
