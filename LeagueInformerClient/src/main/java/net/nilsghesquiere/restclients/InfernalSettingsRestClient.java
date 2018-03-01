package net.nilsghesquiere.restclients;

import net.nilsghesquiere.entities.InfernalSettings;
import net.nilsghesquiere.util.wrappers.InfernalSettingsWrapper;

import org.springframework.web.client.RestTemplate;


public class InfernalSettingsRestClient {
	private static final String URI_INFERNALSETTINGS = "http://localhost:8080/api/infernalsettings";
	private RestTemplate restTemplate = new RestTemplate();
	
	public InfernalSettings getUserInfernalSettings(Long userid){
		InfernalSettingsWrapper jsonResponse = restTemplate.getForObject(URI_INFERNALSETTINGS + "/user/" + userid, InfernalSettingsWrapper.class);
		return jsonResponse.getMap().get("data");
	}
	
	public String getUserInfernalSettingJSON(Long userid){
		String infernalSettings = restTemplate.getForObject(URI_INFERNALSETTINGS + "/user/" + userid, String.class);
		return infernalSettings;
	}
	
}
