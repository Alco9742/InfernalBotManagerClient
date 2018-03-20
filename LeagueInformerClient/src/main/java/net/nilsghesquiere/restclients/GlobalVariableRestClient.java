package net.nilsghesquiere.restclients;

import net.nilsghesquiere.entities.GlobalVariable;
import net.nilsghesquiere.util.wrappers.GlobalVariableSingleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


public class GlobalVariableRestClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalVariableRestClient.class);
	private final String URI_GLOBALVARIABLES;
	private RestTemplate restTemplate = new RestTemplate();
	
	public GlobalVariableRestClient(String uriServer) {
		this.URI_GLOBALVARIABLES = uriServer +"api/admin/globalvars";
	}
	public GlobalVariable getGlobalVariableByName(String name){
		try{
			GlobalVariableSingleWrapper jsonResponse = restTemplate.getForObject(URI_GLOBALVARIABLES + "/name/" + name, GlobalVariableSingleWrapper.class);
			GlobalVariable globalVariable = jsonResponse.getMap().get("data");
			return globalVariable;
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure getting global variable from the server");
			LOGGER.debug(e.getMessage());
			return null;
		}
	}
	
}
