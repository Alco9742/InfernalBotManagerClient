package net.nilsghesquiere.infernalclients;

import net.nilsghesquiere.util.ProgramConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class ActionsInfernalRESTClient implements ActionsInfernalClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionsInfernalRESTClient.class);
	private RestTemplate restTemplate;
	private HttpHeaders httpHeaders;
	
	public ActionsInfernalRESTClient(RestTemplate infernalRestTemplate,HttpHeaders infernalRestHeaders) {
		this.restTemplate = infernalRestTemplate;
		this.httpHeaders = infernalRestHeaders;
	}
	
	public String sendSafestopCommand(){
		LOGGER.info("Sending safestop command");
		LOGGER.info("Headers: " + httpHeaders.toString());
		try{
			HttpEntity<String> request = new HttpEntity<>(httpHeaders);
			HttpEntity<String> response = restTemplate.exchange(ProgramConstants.INFERNAL_REST_BASE + "/action/v1/safestopAllQueuer", HttpMethod.POST,request, String.class);
			String responseString = response.getBody();
			return responseString;
		} catch (Exception e){
			LOGGER.debug("Unhandled exception:", e);
			return null;
		}
	} 
}
