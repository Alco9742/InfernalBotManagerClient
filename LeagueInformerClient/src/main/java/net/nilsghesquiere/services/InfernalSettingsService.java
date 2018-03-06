package net.nilsghesquiere.services;

import net.nilsghesquiere.entities.InfernalSettings;
import net.nilsghesquiere.jdbcclients.InfernalSettingsJDBCClient;
import net.nilsghesquiere.restclients.InfernalSettingsRestClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

public class InfernalSettingsService {
	private static final Logger LOGGER = LoggerFactory.getLogger("InfernalSettingsService");
	private final InfernalSettingsJDBCClient jdbcClient;
	private final InfernalSettingsRestClient restClient;
	
	public InfernalSettingsService(String infernalMap, String webServer){
		//this.jdbcClient =  new LoLAccountJDBCClient(infernalMap);
		this.jdbcClient =  new InfernalSettingsJDBCClient(infernalMap);
		this.restClient = new InfernalSettingsRestClient(webServer);
	}
	
	public void setInfernalSettings(Long userid) throws ResourceAccessException {
		InfernalSettings infernalSettings = restClient.getUserInfernalSettings(userid);
		System.out.println("server: " + infernalSettings);
		InfernalSettings infernalSettings2 = jdbcClient.getDefaultInfernalSettings();
		System.out.println("client: " + infernalSettings2);
	}
}
