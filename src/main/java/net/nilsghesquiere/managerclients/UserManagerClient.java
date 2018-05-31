package net.nilsghesquiere.managerclients;

import net.nilsghesquiere.entities.User;


public interface UserManagerClient {
	public Long getUserIdByUsername(String username);
	public User getUserByUsername(String username);
}
