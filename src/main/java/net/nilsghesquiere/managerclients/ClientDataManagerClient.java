package net.nilsghesquiere.managerclients;

import net.nilsghesquiere.util.wrappers.ClientDataMap;

public interface ClientDataManagerClient {
	public boolean sendClientData(Long userid, ClientDataMap map);
	
}
