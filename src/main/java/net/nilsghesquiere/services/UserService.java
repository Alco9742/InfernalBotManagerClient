package net.nilsghesquiere.services;

import net.nilsghesquiere.entities.IniSettings;
import net.nilsghesquiere.managerclients.UserManagerClient;
import net.nilsghesquiere.managerclients.UserManagerRESTClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	private final UserManagerClient managerClient;
	
	public UserService(IniSettings iniSettings){
		if(iniSettings.getPort().equals("")){
			this.managerClient = new UserManagerRESTClient(iniSettings.getWebServer(), iniSettings.getUsername(), iniSettings.getPassword(), iniSettings.getDebugHTTP());
		} else {
			this.managerClient = new UserManagerRESTClient(iniSettings.getWebServer() + ":" + iniSettings.getPort(), iniSettings.getUsername(), iniSettings.getPassword(), iniSettings.getDebugHTTP());
		}
	}
	
	public Long getUserId(String username){
		return managerClient.getUserIdByUsername(username);
	}
}