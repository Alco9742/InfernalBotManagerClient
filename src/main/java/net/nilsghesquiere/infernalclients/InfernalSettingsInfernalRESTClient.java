package net.nilsghesquiere.infernalclients;

import net.nilsghesquiere.entities.InfernalSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfernalSettingsInfernalRESTClient implements InfernalSettingsInfernalClient{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalSettingsInfernalRESTClient.class);
	
	public InfernalSettingsInfernalRESTClient(){
	}

	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InfernalSettings getDefaultInfernalSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long insertInfernalSettings(InfernalSettings infernalSettings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long updateInfernalSettings(InfernalSettings infernalSettings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkPragmas() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPragmaString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOldPragmas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNewPragmas() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

