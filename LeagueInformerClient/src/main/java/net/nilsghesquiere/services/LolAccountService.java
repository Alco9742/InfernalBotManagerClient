package net.nilsghesquiere.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.enums.AccountStatus;
import net.nilsghesquiere.enums.Region;
import net.nilsghesquiere.jdbcclients.LoLAccountJDBCClient;
import net.nilsghesquiere.restclients.LolAccountRestClient;
import net.nilsghesquiere.util.wrappers.LolMixedAccountMap;

public class LolAccountService {
	private static final Logger LOGGER = LoggerFactory.getLogger("LolAccountService");
	private final LoLAccountJDBCClient jdbcClient;
	private final LolAccountRestClient restClient;
	
	public LolAccountService(String infernalMap, String webServer){
		this.jdbcClient =  new LoLAccountJDBCClient(infernalMap);
		this.restClient = new LolAccountRestClient(webServer);
	}
	
	public void exchangeAccounts(Long userid, Region region, String clientTag, Integer amount) throws ResourceAccessException {
		LolMixedAccountMap sendMap = prepareAccountsToSend(userid);
		if(restClient.sendInfernalAccounts(userid, sendMap)){
			LOGGER.info("Succesfully updated accounts on server");
			jdbcClient.deleteAccounts();
			List<LolAccount> accountsForInfernal = restClient.getUsableAccounts(userid, region, amount);
			int addedInfernalAccounts = jdbcClient.insertAccounts(accountsForInfernal);
			if (addedInfernalAccounts > 0){
				for (LolAccount lolAccount : accountsForInfernal){
					lolAccount.setAccountStatus(AccountStatus.IN_USE);
					lolAccount.setAssignedTo(clientTag);
				}
				restClient.updateLolAccounts(userid, accountsForInfernal);
			}
			
		} else {
			LOGGER.info("Failed to update accounts on server.");
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
