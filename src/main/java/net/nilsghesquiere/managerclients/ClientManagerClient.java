package net.nilsghesquiere.managerclients;

import net.nilsghesquiere.entities.Client;


public interface ClientManagerClient {
	public Client getClientByUserIdAndTag(Long userid, String tag);
	public Boolean registerHWID(Long userid, Long clientid, String hwid);
	public Boolean ping(Long userid, Long clientid);
}
