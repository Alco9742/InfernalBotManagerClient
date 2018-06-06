package net.nilsghesquiere.managerclients;

import net.nilsghesquiere.entities.Client;
import net.nilsghesquiere.util.enums.ClientStatus;
import net.nilsghesquiere.util.wrappers.ClientSingleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.client.ResourceAccessException;

public class ClientManagerRESTClient implements ClientManagerClient{
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientManagerRESTClient.class);
	private final String URI_CLIENTS;
	private final OAuth2RestTemplate restTemplate;
	//private final HttpHeaders headers;
	
	public ClientManagerRESTClient(OAuth2RestTemplate restTemplate) {
		String uriAccesToken = restTemplate.getResource().getAccessTokenUri();
		String uriServer = uriAccesToken.substring(0,uriAccesToken.indexOf("/oauth/token"));
		
		this.URI_CLIENTS = uriServer + "/api/clients";
		this.restTemplate = restTemplate;
		//set headers
	//	headers = ProgramUtil.buildHttpHeaders();
	}
	
	public Client getClientByUserIdAndTag(Long userId, String tag){
		try{
			ClientSingleWrapper jsonResponse = restTemplate.getForObject(URI_CLIENTS + "/user/" + userId + "/client/tag/" + tag, ClientSingleWrapper.class);
			Client client = jsonResponse.getMap().get("data");
			if (client != null){
				LOGGER.info("Received settings for client '" + client.getTag() + "'.");
			}
			return client;
		} catch (ResourceAccessException e){
			LOGGER.debug("Handled exception: " + e.getClass().getSimpleName());
			LOGGER.debug("Client isn't connected to the internet or server is down");
			return null;
		} catch (Exception e){
			LOGGER.debug("Unhandled exception:", e);
			return null;
		}
	}
	
	public Boolean registerHWID(Long userId, Long clientId, String hwid){
		try{
			HttpEntity<String> request = new HttpEntity<>(hwid);
			HttpEntity<ClientSingleWrapper> response = restTemplate.exchange(URI_CLIENTS + "/user/" + userId + "/client/" + clientId + "/register/", HttpMethod.PUT,request, ClientSingleWrapper.class);
			ClientSingleWrapper jsonResponse = response.getBody();
			Client client = jsonResponse.getMap().get("data");
			if (client != null && client.getHWID().equals(hwid)){
				return true;
			} else {
				return false;
			}
		} catch (ResourceAccessException e){
			LOGGER.debug("Handled exception: " + e.getClass().getSimpleName());
			LOGGER.debug("Client isn't connected to the internet or server is down");
			return false;
		} catch (Exception e){
			LOGGER.debug("Unhandled exception:", e);
			return false;
		}
	}
	
	public Boolean ping(Long userId, Long clientId, ClientStatus status){
		try{
			HttpEntity<ClientStatus> request = new HttpEntity<>(status);
			HttpEntity<Boolean> response = restTemplate.exchange(URI_CLIENTS + "/user/" + userId + "/client/" + clientId + "/ping/", HttpMethod.PUT,request, Boolean.class);
			Boolean jsonResponse = response.getBody();
			return jsonResponse;
		} catch (ResourceAccessException e){
			LOGGER.debug("Handled exception: " + e.getClass().getSimpleName());
			LOGGER.debug("Client isn't connected to the internet or server is down");
			return false;
		} catch (Exception e){
			LOGGER.debug("Unhandled exception:", e);
			return false;
		}
	}
	
}
