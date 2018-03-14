package net.nilsghesquiere.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import net.nilsghesquiere.entities.ClientData;
import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.entities.Queuer;
import net.nilsghesquiere.jdbcclients.QueuerJDBCClient;
import net.nilsghesquiere.restclients.ClientDataRestClient;
import net.nilsghesquiere.util.wrappers.ClientDataMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDataService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDataService.class);
	private final ClientSettings clientSettings;
	private final ClientData clientData;
	private final ClientDataRestClient restClient;
	private final QueuerJDBCClient jdbcClient;
	
	
	public ClientDataService(ClientSettings clientSettings,ClientData clientData){
		this.jdbcClient =  new QueuerJDBCClient(clientSettings.getInfernalMap());
		this.restClient = new ClientDataRestClient("http://" + clientSettings.getWebServer() + ":" + clientSettings.getPort());
		this.clientSettings = clientSettings;
		this.clientData = clientData;
	}
	
	public void sendData(String status){
		ClientDataMap sendmap = prepareData(status);
		restClient.sendClientData(clientSettings.getUserId(), sendmap);
	}
	
	public boolean hasActiveQueuer(){
		return activeQueuerAmount() > 0;
	}
	
	public Integer activeQueuerAmount(){
		return jdbcClient.countQueuers();
	}
	
	private ClientDataMap prepareData(String status){
		ClientDataMap clientDataMap = new ClientDataMap();
		List<Queuer> queuers = jdbcClient.getQueuers();
		for (Queuer queuer : queuers){
			queuer.setQueuerLolAccounts(jdbcClient.getQueuerAccounts(queuer));
		}
		clientData.setDate(LocalDateTime.now());
		clientData.setQueuers(queuers);
		clientData.setStatus(status);
		clientDataMap.add("0", clientData);
		return clientDataMap;
	}
} 
