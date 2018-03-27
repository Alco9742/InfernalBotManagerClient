package net.nilsghesquiere.infernalclients;

import java.util.List;

import net.nilsghesquiere.entities.LolAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoLAccountInfernalRESTClient implements LolAccountInfernalClient {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(LoLAccountInfernalRESTClient.class);
	
	public LoLAccountInfernalRESTClient(){
	}

	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<LolAccount> getAccounts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAllAccounts() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int insertAccounts(List<LolAccount> lolAccounts, Boolean buffer) {
		// TODO Auto-generated method stub
		return 0;
	}
}
