package net.nilsghesquiere.managerclients;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import net.nilsghesquiere.entities.Client;
import net.nilsghesquiere.security.SSLBasicAuthenticationRestTemplate;
import net.nilsghesquiere.util.ProgramUtil;
import net.nilsghesquiere.util.wrappers.ClientSingleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class ClientManagerRESTClient implements ClientManagerClient{
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientManagerRESTClient.class);
	private final String URI_CLIENTS;
	private RestTemplate restTemplate;
	private HttpHeaders headers;
	
	public ClientManagerRESTClient(String uriServer, String username, String password, Boolean debugHTTP) {
		this.URI_CLIENTS = uriServer +"/api/clients";
		try {
			this.restTemplate = new SSLBasicAuthenticationRestTemplate(username,password,debugHTTP);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			LOGGER.debug(e.getMessage());
		}	
		//set headers
		headers = ProgramUtil.buildHttpHeaders();
	}
	
	public Client getClientByUserIdAndTag(Long userId, String tag){
		try{
			ClientSingleWrapper jsonResponse = restTemplate.getForObject(URI_CLIENTS + "/user/" + userId + "/tag/" + tag, ClientSingleWrapper.class);
			Client client = jsonResponse.getMap().get("data");
			if (client != null){
				LOGGER.info("Received settings for client '" + client.getTag() + "'.");
			}
			return client;
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure getting client from the server");
			LOGGER.debug(e.getMessage());
			return null;
		} catch (HttpServerErrorException e2){
			LOGGER.warn("Failure getting client from the server");
			LOGGER.debug(e2.getMessage());
			return null;
		}
	}
	
	public Boolean registerHWID(Long userId, Long clientId, String hwid){
		try{
			HttpEntity<String> request = new HttpEntity<>(hwid, headers);
			HttpEntity<ClientSingleWrapper> response = restTemplate.exchange(URI_CLIENTS + "/user/" + userId + "/client/" + clientId + "/register/", HttpMethod.PUT,request, ClientSingleWrapper.class);
			ClientSingleWrapper jsonResponse = response.getBody();
			Client client = jsonResponse.getMap().get("data");
			if (client != null && client.getHWID().equals(hwid)){
				return true;
			} else {
				return false;
			}
		} catch (ResourceAccessException e){
			LOGGER.warn("Failed to register HWID for this client");
			LOGGER.debug(e.getMessage());
			return null;
		} catch (HttpServerErrorException e2){
			LOGGER.warn("Failure to register HWID for this client");
			LOGGER.debug(e2.getMessage());
			return null;
		}
	}
	
}
