package net.nilsghesquiere.infernalclients;

import java.util.List;

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
	
	public boolean sendSafestopCommand(){
		LOGGER.info("Sending safestop command");
		LOGGER.info("Headers: " + httpHeaders.toString());
		if(checkAuthHeader()){
			try{
				HttpEntity<String> request = new HttpEntity<>(httpHeaders);
				HttpEntity<String> response = restTemplate.exchange(ProgramConstants.INFERNAL_REST_BASE + "/action/v1/safestopAllQueuer", HttpMethod.POST,request, String.class);
				String responseString = response.getBody();
				if(responseString.equals("Safestop complete")){
					return true;
				} else {
					LOGGER.debug("Failed to perform safestop: ");
					LOGGER.debug(responseString);
					return false;
				}
			} catch (Exception e){
				LOGGER.debug("Unhandled exception:", e);
				return false;
			}
		} else {
			LOGGER.debug("No authorization header set yet, not sending safestop command");
			return false;
		}
	} 
	
	public boolean checkAuthHeader(){
		List<String> authorizationHeader = httpHeaders.get("Authorization");
		if(authorizationHeader != null && authorizationHeader.size() > 0){
			return true;
		}
		return false;
	}
}
