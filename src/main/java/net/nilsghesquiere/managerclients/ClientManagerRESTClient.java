package net.nilsghesquiere.managerclients;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import net.nilsghesquiere.entities.Client;
import net.nilsghesquiere.security.SSLBasicAuthenticationRestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class ClientManagerRESTClient implements ClientManagerClient{
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientManagerRESTClient.class);
	private final String URI_CLIENTS;
	private RestTemplate restTemplate;
	
	public ClientManagerRESTClient(String uriServer, String username, String password, Boolean debugHTTP) {
		this.URI_CLIENTS = uriServer +"/api/clients";
		try {
			this.restTemplate = new SSLBasicAuthenticationRestTemplate(username,password,debugHTTP);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			LOGGER.debug(e.getMessage());
		}	
	}
	
	public Client getClientByUserIdAndTag(Long userId, String tag){
		try{
			Client jsonResponse = restTemplate.getForObject(URI_CLIENTS + "/user/" + userId + "/tag/" + tag, Client.class);
			return jsonResponse;
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
}
