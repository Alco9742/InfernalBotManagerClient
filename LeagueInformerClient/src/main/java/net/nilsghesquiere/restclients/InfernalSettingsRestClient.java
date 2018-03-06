package net.nilsghesquiere.restclients;

import net.nilsghesquiere.entities.InfernalSettings;
import net.nilsghesquiere.util.wrappers.InfernalSettingsWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;


public class InfernalSettingsRestClient {
	private static final Logger LOGGER = LoggerFactory.getLogger("InfernalBotManagerAccountDatabaseClient");
	private final String URI_INFERNALSETTINGS;
	private RestTemplate restTemplate = new RestTemplate();
	
	public InfernalSettingsRestClient(String uriServer) {
		this.URI_INFERNALSETTINGS = uriServer +"api/infernalsettings";
	}
	public InfernalSettings getUserInfernalSettings(Long userid){
		InfernalSettingsWrapper jsonResponse = restTemplate.getForObject(URI_INFERNALSETTINGS + "/user/" + userid, InfernalSettingsWrapper.class);
		return jsonResponse.getMap().get("data");
	}
	
	public String getUserInfernalSettingJSON(Long userid){
		String infernalSettings = restTemplate.getForObject(URI_INFERNALSETTINGS + "/user/" + userid, String.class);
		return infernalSettings;
	}
	
}
