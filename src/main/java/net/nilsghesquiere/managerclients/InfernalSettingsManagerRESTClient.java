package net.nilsghesquiere.managerclients;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import net.nilsghesquiere.entities.InfernalSettings;
import net.nilsghesquiere.security.SSLBasicAuthenticationRestTemplate;
import net.nilsghesquiere.util.wrappers.InfernalSettingsWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


public class InfernalSettingsManagerRESTClient implements InfernalSettingsManagerClient{
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalSettingsManagerRESTClient.class);
	private final String URI_INFERNALSETTINGS;
	private RestTemplate restTemplate;
	
	public InfernalSettingsManagerRESTClient(String uriServer, String username, String password, Boolean debugHTTP) {
		this.URI_INFERNALSETTINGS = uriServer +"/api/infernalsettings";
		try {
			this.restTemplate = new SSLBasicAuthenticationRestTemplate(username,password,debugHTTP);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			LOGGER.debug(e.getMessage());
		}	
	}
	public InfernalSettings getUserInfernalSettings(Long userid){
		try{
			InfernalSettingsWrapper jsonResponse = restTemplate.getForObject(URI_INFERNALSETTINGS + "/user/" + userid, InfernalSettingsWrapper.class);
			InfernalSettings infernalSettings = jsonResponse.getMap().get("data");
			if (infernalSettings != null){
				LOGGER.info("Received InfernalBot settings from the InfernalBotManager server.");
			}
			return infernalSettings;
		} catch (ResourceAccessException | HttpServerErrorException e){
			LOGGER.warn("Failure getting infernal settings from the server");
			LOGGER.debug(e.getMessage());
			return null;
		}
	}
}
