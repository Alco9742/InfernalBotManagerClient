package net.nilsghesquiere.services;

import net.nilsghesquiere.entities.Client;
import net.nilsghesquiere.entities.IniSettings;
import net.nilsghesquiere.managerclients.ClientManagerClient;
import net.nilsghesquiere.managerclients.ClientManagerRESTClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientService.class);
	private final ClientManagerClient managerClient;
	
	public ClientService(IniSettings iniSettings){
		
		if(iniSettings.getPort().equals("")){
			this.managerClient = new ClientManagerRESTClient(iniSettings.getWebServer(), iniSettings.getUsername(), iniSettings.getPassword(), iniSettings.getDebugHTTP());
		} else {
			this.managerClient = new ClientManagerRESTClient(iniSettings.getWebServer() + ":" + iniSettings.getPort(), iniSettings.getUsername(), iniSettings.getPassword(), iniSettings.getDebugHTTP());
		}
	}
	
	public Client getClient(Long userid, String tag){
		return managerClient.getClientByUserIdAndTag(userid, tag);
	}
	
	public Boolean registerHWID(Long userid, Long clientid, String hwid){
		return managerClient.registerHWID(userid, clientid, hwid);
	}
}