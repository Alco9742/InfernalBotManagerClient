package net.nilsghesquiere.services;

import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.infernalclients.ActionsInfernalClient;
import net.nilsghesquiere.infernalclients.ActionsInfernalCustomClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionsService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionsService.class);
	@SuppressWarnings("unused")
	private final ActionsInfernalClient infernalClient;
	
	public ActionsService(ClientSettings clientSettings){
		this.infernalClient = new ActionsInfernalCustomClient();
	}
}