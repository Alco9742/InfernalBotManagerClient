package net.nilsghesquiere.services;

import net.nilsghesquiere.infernalclients.ActionsInfernalClient;
import net.nilsghesquiere.infernalclients.ActionsInfernalRESTClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class ActionsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionsService.class);
	private final ActionsInfernalClient infernalClient;
	
	public ActionsService(RestTemplate infernalRestTemplate,HttpHeaders infernalRestHeaders) {
		this.infernalClient = new ActionsInfernalRESTClient(infernalRestTemplate,infernalRestHeaders);
	}
	
	public void sendSafestopCommand(){
		String response = infernalClient.sendSafestopCommand();
		LOGGER.info(response);
	}
}