package net.nilsghesquiere.services;

import java.util.ArrayList;
import java.util.List;

import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.enums.AccountStatus;
import net.nilsghesquiere.enums.Region;
import net.nilsghesquiere.jdbcclients.LoLAccountJDBCClient;
import net.nilsghesquiere.restclients.LolAccountRestClient;
import net.nilsghesquiere.util.wrappers.LolMixedAccountMap;

public class LolAccountService {
	private final LoLAccountJDBCClient jdbcClient;
	private final LolAccountRestClient restClient;
	
	public LolAccountService(String infernalMap){
		this.jdbcClient =  new LoLAccountJDBCClient(infernalMap);
		this.restClient = new LolAccountRestClient();
	}
	
	public void exchangeAccounts(Long userid, Region region, String clientTag, Integer amount){
		LolMixedAccountMap sendMap = prepareAccountsToSend(userid);
		if(restClient.sendInfernalAccounts(userid, sendMap)){
			System.out.println("Succesfully updated accounts on server, deleting from local database");
			jdbcClient.deleteAccounts();
			System.out.println("Delete complete, grabbing " + amount + " accounts from the server");
			List<LolAccount> accountsForInfernal = restClient.getUsableAccounts(userid, region, amount);
			int addedInfernalAccounts = jdbcClient.insertAccounts(accountsForInfernal);
			System.out.println(addedInfernalAccounts + " accounts added to the Infernalbot Database" ); 
		} else {
			System.out.println("Failed to update accounts on server");
		}
	}
	private LolMixedAccountMap prepareAccountsToSend(Long userid){
		LolMixedAccountMap lolAccountMap = new LolMixedAccountMap();
		List<LolAccount> newAccounts = new ArrayList<>();
		List<LolAccount> accountsFromJDBC = jdbcClient.getAccounts();
		for (LolAccount accountFromJDBC : accountsFromJDBC){
			LolAccount accountFromREST = restClient.getByUserIdAndAccount(userid, accountFromJDBC.getAccount());
			if(accountFromREST != null){
				//Account already exists in the database: copy the editable settings from serverside
				//set the id
				accountFromJDBC.setId(accountFromREST.getId());
				//set the max level & max BE (this will always be filled in if it exists on the server)
				accountFromJDBC.setMaxLevel(accountFromREST.getMaxLevel());
				accountFromJDBC.setMaxBe(accountFromREST.getMaxBe());
				//set the region
				accountFromJDBC.setRegion(accountFromREST.getRegion());
				//set the Info
				accountFromJDBC.setInfo(accountFromREST.getInfo());
				//set the priority
				accountFromJDBC.setPriority(accountFromREST.getPriority());
				//set assignedto Empty
				accountFromJDBC.setAssignedTo("");
				//Set accountstatus
				if (accountFromJDBC.getAccountStatus() != AccountStatus.ERROR){
					if (accountFromJDBC.getLevel() >= accountFromJDBC.getMaxLevel()){
						accountFromJDBC.setAccountStatus(AccountStatus.DONE);
					} else {
						accountFromJDBC.setAccountStatus(AccountStatus.READY_FOR_USE);
					}
				}
				lolAccountMap.add(accountFromJDBC.getId().toString(), accountFromJDBC);
			} else {
				accountFromJDBC.setAccountStatus(AccountStatus.NEW);
				accountFromJDBC.setAssignedTo("");
				newAccounts.add(accountFromJDBC);
			}
		lolAccountMap.setNewAccs(newAccounts);
		}
	return lolAccountMap;
	}
} 
