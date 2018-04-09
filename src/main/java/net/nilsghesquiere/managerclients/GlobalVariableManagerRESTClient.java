package net.nilsghesquiere.managerclients;



import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import net.nilsghesquiere.entities.GlobalVariable;
import net.nilsghesquiere.security.SSLBasicAuthenticationRestTemplate;
import net.nilsghesquiere.util.wrappers.GlobalVariableSingleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;



public class GlobalVariableManagerRESTClient implements GlobalVariableManagerClient{
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalVariableManagerRESTClient.class);
	private final String URI_GLOBALVARIABLES;
	private RestTemplate restTemplate;
	
	public GlobalVariableManagerRESTClient(String uriServer, String username, String password, Boolean debugHTTP){
		this.URI_GLOBALVARIABLES = uriServer +"/api/vars";
		try {
			this.restTemplate = new SSLBasicAuthenticationRestTemplate(username,password,debugHTTP);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			LOGGER.debug(e.getMessage());
		}		
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
