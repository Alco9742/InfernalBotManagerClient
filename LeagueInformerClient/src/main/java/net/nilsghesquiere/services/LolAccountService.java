package net.nilsghesquiere.services;

import java.util.ArrayList;
import java.util.List;

import net.nilsghesquiere.entities.InfernalBotManagerClientSettings;
import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.enums.AccountStatus;
import net.nilsghesquiere.enums.Region;
import net.nilsghesquiere.jdbcclients.LoLAccountJDBCClient;
import net.nilsghesquiere.restclients.LolAccountRestClient;
import net.nilsghesquiere.util.wrappers.LolMixedAccountMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

public class LolAccountService {
	private static final Logger LOGGER = LoggerFactory.getLogger(LolAccountService.class);
	private final LoLAccountJDBCClient jdbcClient;
	private final LolAccountRestClient restClient;
	private final InfernalBotManagerClientSettings clientSettings;
	
	public LolAccountService(InfernalBotManagerClientSettings clientSettings){
		this.jdbcClient =  new LoLAccountJDBCClient(clientSettings.getInfernalMap());
		this.restClient = new LolAccountRestClient("http://" + clientSettings.getWebServer() + ":" + clientSettings.getPort());
		this.clientSettings = clientSettings;
	}
	
	public boolean exchangeAccounts(Long userid, Region region, String clientTag, Integer amount) throws ResourceAccessException {
		//TODO fix bug: if for some reason both clients have same accs in infernalbot database:
		//     Client1 uploads the accs and puts them on READY, after that loads them and puts them on IN USE;
		//     Client2 uploads the accs and does the same!!!! --> solution: check on assigned to
		LolMixedAccountMap sendMap = prepareAccountsToSend(userid);
		//lege map niet senden
		if(sendMap == null || restClient.sendInfernalAccounts(userid, sendMap)){
			if(sendMap != null){
				jdbcClient.deleteAccounts();
			}
			List<LolAccount> accountsForInfernal = restClient.getUsableAccounts(userid, region, amount);
			int addedInfernalAccounts = 0;
			if (!accountsForInfernal.isEmpty()){
				addedInfernalAccounts = jdbcClient.insertAccounts(accountsForInfernal, false);
			}
			if (addedInfernalAccounts > 0){
				for (LolAccount lolAccount : accountsForInfernal){
					lolAccount.setAccountStatus(AccountStatus.IN_USE);
					lolAccount.setAssignedTo(clientTag);
				}
				restClient.updateLolAccounts(userid, accountsForInfernal);
			}
			if (clientSettings.getAccountBuffer() > 0){
				List<LolAccount> accountsForInfernalBuffer = restClient.getBufferAccounts(userid, region, amount);
				int addedInfernalBufferAccounts = 0;
				if (!accountsForInfernalBuffer.isEmpty()){
					addedInfernalBufferAccounts = jdbcClient.insertAccounts(accountsForInfernalBuffer, true);
				}
				if (addedInfernalBufferAccounts > 0){
					for (LolAccount lolAccount : accountsForInfernalBuffer){
						lolAccount.setAccountStatus(AccountStatus.IN_BUFFER);
						lolAccount.setAssignedTo(clientTag);
					}
					restClient.updateLolAccounts(userid, accountsForInfernalBuffer);
				}
			}	
		if (addedInfernalAccounts < 5) {
			//not enough for one group (TODO check if it needs 1 queuer minimum or amount from groups)
			LOGGER.error("Not enough accounts available on the server");
			return false;
		}
		} else {
			LOGGER.error("Failed to update accounts on server.");
			return false;
		}
		return true;
	}
	
	public void setAccountsAsReadyForUse(Long userid) throws ResourceAccessException {
		LolMixedAccountMap sendMap = prepareAccountsToSend(userid);
		if(restClient.sendInfernalAccounts(userid, sendMap)){
			LOGGER.info("Updated accounts on server");
			jdbcClient.deleteAccounts();
		} else {
			LOGGER.error("Failure updating accounts on server.");
		}
	}
	
	private LolMixedAccountMap prepareAccountsToSend(Long userid){
		LolMixedAccountMap lolAccountMap = new LolMixedAccountMap();
		List<LolAccount> newAccounts = new ArrayList<>();
		List<LolAccount> accountsFromJDBC = jdbcClient.getAccounts();
		if (!accountsFromJDBC.isEmpty()){
			for (LolAccount accountFromJDBC : accountsFromJDBC){
				LolAccount accountFromREST = restClient.getByUserIdRegionAndAccount(userid, accountFromJDBC.getRegion(),accountFromJDBC.getAccount());
				//added here to only touch accounts not assigned to anyone or assigned to this client
				if(accountFromREST != null){
					boolean accountAssignedToOtherClient = true;
					if(accountFromREST.getAssignedTo().equals(clientSettings.getClientTag())){
						accountAssignedToOtherClient = false;
					}
					if(accountFromREST.getAssignedTo().equals("")){
						accountAssignedToOtherClient = false;
					}
					if(!accountAssignedToOtherClient){
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
						//set active flag (TODO figure this out completely)
						accountFromJDBC.setActive(accountFromREST.isActive());
						//Set accountstatus
						if (accountFromJDBC.getAccountStatus() != AccountStatus.ERROR && accountFromJDBC.getAccountStatus() != AccountStatus.BANNED){
							if (accountFromJDBC.getLevel() >= accountFromREST.getMaxLevel()){
								accountFromJDBC.setAccountStatus(AccountStatus.DONE);
							} else {
								accountFromJDBC.setAccountStatus(AccountStatus.READY_FOR_USE);
							}
						} else {
							//temp attempt at a fix
							accountFromJDBC.setActive(false);
						}
						lolAccountMap.add(accountFromJDBC.getId().toString(), accountFromJDBC);
					}
				} else {
					if(clientSettings.getUploadNewAccounts()){
						accountFromJDBC.setAccountStatus(AccountStatus.NEW);
						accountFromJDBC.setAssignedTo("");
						newAccounts.add(accountFromJDBC);
					}
				}
			lolAccountMap.setNewAccs(newAccounts);
			}
		} else {
			lolAccountMap = null;
		}
	return lolAccountMap;
	}
} 
