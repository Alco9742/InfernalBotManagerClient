package net.nilsghesquiere.restclients;

import net.nilsghesquiere.entities.GlobalVariable;
import net.nilsghesquiere.util.wrappers.GlobalVariableSingleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


public class GlobalVariableRestClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalVariableRestClient.class);
	private final String URI_GLOBALVARIABLES;
	private RestTemplate restTemplate = new RestTemplate();
	
	public GlobalVariableRestClient(String uriServer, String username, String password) {
		this.URI_GLOBALVARIABLES = uriServer +"api/vars";
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
	}
	public GlobalVariable getGlobalVariableByName(String name){
		try{
			GlobalVariableSingleWrapper jsonResponse = restTemplate.getForObject(URI_GLOBALVARIABLES + "/" + name, GlobalVariableSingleWrapper.class);
			GlobalVariable globalVariable = jsonResponse.getMap().get("data");
			return globalVariable;
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure getting global variable from the server");
			LOGGER.debug(e.getMessage());
			return null;
		}
	}
	
}
