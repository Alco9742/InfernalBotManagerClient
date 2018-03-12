package net.nilsghesquiere.restclients;

import java.util.Collections;

import net.nilsghesquiere.util.wrappers.ClientDataMap;
import net.nilsghesquiere.util.wrappers.ClientDataWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;


public class ClientDataRestClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDataRestClient.class);
	private final String URI_CLIENTS;
	private RestTemplate restTemplate = new RestTemplate();
	
	public ClientDataRestClient(String uriServer) {
		this.URI_CLIENTS = uriServer +"/api/clients";
	}
	
	public boolean sendClientData(Long userid, ClientDataMap map){
		boolean result = true;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ClientDataMap> request = new HttpEntity<>(map, headers);
		HttpEntity<ClientDataWrapper> response = restTemplate.exchange(URI_CLIENTS + "/user/" + userid,  HttpMethod.POST,request, ClientDataWrapper.class);
		ClientDataWrapper clientDataWrapper = response.getBody();
		if (!clientDataWrapper.getError().equals((""))){
			result = false;
			LOGGER.error("Failure updating Client data on the server: " + clientDataWrapper.getError());
		}
		if (result = true){
			LOGGER.info("Updated Client data on the server");
		}
		return result;
	}
}
