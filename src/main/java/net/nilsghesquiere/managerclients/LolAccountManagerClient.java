package net.nilsghesquiere.managerclients;

import java.util.List;

import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.util.enums.Region;
import net.nilsghesquiere.util.wrappers.LolMixedAccountMap;

public interface LolAccountManagerClient {
	public List<LolAccount> getUserLolAccounts(Long userid);
	public List<LolAccount> getUsableAccounts(Long userid, Region region, Integer amount);
	public List<LolAccount> getBufferAccounts(Long userid, Region region, Integer amount);
	public LolAccount getByUserIdRegionAndAccount(Long userid, Region region, String account);
	public List<LolAccount> updateLolAccounts(Long userid, List<LolAccount> lolAccounts);
	public boolean sendInfernalAccounts(Long userid, LolMixedAccountMap map);
}
