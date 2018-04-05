package net.nilsghesquiere.infernalclients;

import java.util.List;

import net.nilsghesquiere.entities.Queuer;
import net.nilsghesquiere.entities.QueuerLolAccount;

public interface ClientDataInfernalClient{
	public boolean connect();
	public List<Queuer> getQueuers();
	public List<QueuerLolAccount> getQueuerAccounts(Queuer queuer);
	public Integer countQueuers();
	public void deleteQueuerExtent();
	public void deleteQueuer(Queuer queuer);
}
