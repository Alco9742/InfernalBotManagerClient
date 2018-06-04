package net.nilsghesquiere.managerclients;

import net.nilsghesquiere.util.wrappers.ClientDataMap;

public interface ClientDataManagerClient {
	public boolean sendClientData(Long userid,Long clientid, ClientDataMap map);
	
}
