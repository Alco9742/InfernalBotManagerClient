package net.nilsghesquiere.infernalclients;

import java.util.List;

import net.nilsghesquiere.entities.LolAccount;

public interface LolAccountInfernalClient {
	public boolean connect();
	public boolean checkPragmas();
	public String getPragmaString();
	public List<LolAccount> getAccounts();
	public void deleteAllAccounts();
	public int insertAccounts(List<LolAccount> lolAccounts, Boolean buffer);
	public String getOldPragmas();
	public String getNewPragmas();
}
