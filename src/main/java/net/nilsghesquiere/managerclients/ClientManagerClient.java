package net.nilsghesquiere.managerclients;

import net.nilsghesquiere.entities.Client;


public interface ClientManagerClient {
	public Client getClientByUserIdAndTag(Long userid, String tag);
}
