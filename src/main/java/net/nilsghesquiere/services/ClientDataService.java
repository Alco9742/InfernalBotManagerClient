package net.nilsghesquiere.services;

import java.time.LocalDateTime;
import java.util.List;

import net.nilsghesquiere.entities.Client;
import net.nilsghesquiere.entities.ClientData;
import net.nilsghesquiere.entities.IniSettings;
import net.nilsghesquiere.entities.Queuer;
import net.nilsghesquiere.entities.QueuerLolAccount;
import net.nilsghesquiere.infernalclients.ClientDataInfernalClient;
import net.nilsghesquiere.infernalclients.ClientDataInfernalJDBCClient;
import net.nilsghesquiere.managerclients.ClientDataManagerClient;
import net.nilsghesquiere.managerclients.ClientDataManagerRESTClient;
import net.nilsghesquiere.util.wrappers.ClientDataMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDataService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDataService.class);
	private final Client client;
	private final ClientDataManagerClient managerClient;
	private final ClientDataInfernalClient infernalClient;
	
	
	public ClientDataService(Client client,IniSettings iniSettings){
		this.client = client;
		this.infernalClient =  new ClientDataInfernalJDBCClient(client.getClientSettings().getInfernalMap());
		if(iniSettings.getPort().equals("")){
			this.managerClient = new ClientDataManagerRESTClient(iniSettings.getWebServer(), iniSettings.getUsername(), iniSettings.getPassword(), iniSettings.getDebugHTTP());
		} else {
			this.managerClient = new ClientDataManagerRESTClient(iniSettings.getWebServer() + ":" + iniSettings.getPort(), iniSettings.getUsername(), iniSettings.getPassword(), iniSettings.getDebugHTTP());
		}
	}
	
	public void sendData(String status, String ramInfo, String cpuInfo){
		ClientDataMap sendmap = prepareData(status, ramInfo, cpuInfo);
		managerClient.sendClientData(client.getUser().getId(), sendmap);
	}
	
	public boolean hasActiveQueuer(){
		return activeQueuerAmount() > 0;
	}
	
	public Integer activeQueuerAmount(){
		return infernalClient.countQueuers();
	}
	
	public void deleteAllQueuers(){
		List<Queuer> queuers = infernalClient.getQueuers();
		for (Queuer queuer : queuers){
			infernalClient.deleteQueuer(queuer);
		}
		infernalClient.deleteQueuerExtent();
	}
	
	public List<Queuer> getAllQueuers(){
		return infernalClient.getQueuers();
	}
	
	private ClientDataMap prepareData(String status, String ramInfo, String cpuInfo){
		ClientDataMap clientDataMap = new ClientDataMap();
		List<Queuer> queuers = infernalClient.getQueuers();
		for (Queuer queuer : queuers){
			queuer.setQueuerLolAccounts(infernalClient.getQueuerAccounts(queuer));
			for (QueuerLolAccount accInQueuer: queuer.getQueuerLolAccounts()){
				if (accInQueuer.getLpq()){
					queuer.setLpq(true);
				}
			}
		}
		ClientData clientData = new ClientData();
		clientData.setDate(LocalDateTime.now());
		clientData.setQueuers(queuers);
		clientData.setStatus(status);
		clientData.setRamInfo(ramInfo);
		clientData.setCpuInfo(cpuInfo);
		clientDataMap.add("0", clientData);
		return clientDataMap;
	}

	public void deleteQueuer(Queuer queuer) {
		infernalClient.deleteQueuer(queuer);
	}

} 
