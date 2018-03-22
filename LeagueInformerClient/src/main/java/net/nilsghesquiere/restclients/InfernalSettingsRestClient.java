package net.nilsghesquiere.restclients;

import net.nilsghesquiere.entities.InfernalSettings;
import net.nilsghesquiere.util.ProgramUtil;
import net.nilsghesquiere.util.wrappers.InfernalSettingsWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


public class InfernalSettingsRestClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalSettingsRestClient.class);
	private final String URI_INFERNALSETTINGS;
	private RestTemplate restTemplate = new RestTemplate();
	
	public InfernalSettingsRestClient(String uriServer, String username, String password) {
		this.URI_INFERNALSETTINGS = uriServer +"api/infernalsettings";
		//set auth
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username,password));
	}
	public InfernalSettings getUserInfernalSettings(Long userid){
		try{
			InfernalSettingsWrapper jsonResponse = restTemplate.getForObject(URI_INFERNALSETTINGS + "/user/" + userid, InfernalSettingsWrapper.class);
			InfernalSettings infernalSettings = jsonResponse.getMap().get("data");
			if (infernalSettings != null){
				LOGGER.info("Received InfernalBot settings from the InfernalBotManager server.");
				LOGGER.debug("SettingsFromServer: " + infernalSettings);
			}
			return infernalSettings;
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure getting infernal settings from the server");
			LOGGER.debug(e.getMessage());
			return null;
		}
	}
	
	public String getUserInfernalSettingJSON(Long userid){
		String infernalSettings = restTemplate.getForObject(URI_INFERNALSETTINGS + "/user/" + userid, String.class);
		return infernalSettings;
	}
	
}
