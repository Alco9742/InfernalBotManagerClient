package net.nilsghesquiere.managerclients;



import net.nilsghesquiere.entities.GlobalVariable;
import net.nilsghesquiere.util.wrappers.GlobalVariableSingleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;


public class GlobalVariableManagerRESTClient implements GlobalVariableManagerClient{
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalVariableManagerRESTClient.class);
	private final String URI_GLOBALVARIABLES;
	private final OAuth2RestOperations restTemplate;
	
	public GlobalVariableManagerRESTClient(OAuth2RestOperations restTemplate){
		String uriAccesToken = restTemplate.getResource().getAccessTokenUri();
		String uriServer = uriAccesToken.substring(0,uriAccesToken.indexOf("/oauth/token"));
		
		this.URI_GLOBALVARIABLES = uriServer + "/api/vars";
		this.restTemplate = restTemplate;
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
		} catch (HttpServerErrorException e){
			LOGGER.warn("Failure getting global variable from the server");
			LOGGER.debug(e.getMessage());
			return null;
		}
	}
	
}
