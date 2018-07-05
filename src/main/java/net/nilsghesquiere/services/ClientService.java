package net.nilsghesquiere.services;

import net.nilsghesquiere.entities.Client;
import net.nilsghesquiere.managerclients.ClientManagerClient;
import net.nilsghesquiere.managerclients.ClientManagerRESTClient;
import net.nilsghesquiere.util.enums.ClientAction;
import net.nilsghesquiere.util.enums.ClientStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;

public class ClientService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientService.class);
	private final ClientManagerClient managerClient;
	
	public ClientService(OAuth2RestTemplate restTemplate){
		this.managerClient = new ClientManagerRESTClient(restTemplate);
	}
	
	public Client getClient(Long userid, String tag){
		return managerClient.getClientByUserIdAndTag(userid, tag);
	}
	
	public Boolean registerHWID(Long userid, Long clientid, String hwid){
		return managerClient.registerHWID(userid, clientid, hwid);
	}
	
	public ClientAction ping(Long userid, Long clientid, ClientStatus status){
		return managerClient.ping(userid, clientid, status);
	}
	
	public ClientAction action(Long userid, Long clientid, ClientAction action){
		return managerClient.action(userid, clientid, action);
	}
}