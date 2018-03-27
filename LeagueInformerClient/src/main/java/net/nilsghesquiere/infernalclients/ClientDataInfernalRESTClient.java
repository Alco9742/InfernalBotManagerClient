package net.nilsghesquiere.infernalclients;

import java.util.List;

import net.nilsghesquiere.entities.Queuer;
import net.nilsghesquiere.entities.QueuerLolAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO Transactions
public class ClientDataInfernalRESTClient implements ClientDataInfernalClient{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDataInfernalRESTClient.class);

	public ClientDataInfernalRESTClient(){
	}

	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Queuer> getQueuers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QueuerLolAccount> getQueuerAccounts(Queuer queuer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer countQueuers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteQueuerExtent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteQueuer(Queuer queuer) {
		// TODO Auto-generated method stub
		
	}
}
