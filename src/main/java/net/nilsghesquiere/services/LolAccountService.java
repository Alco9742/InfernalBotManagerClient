package net.nilsghesquiere.services;

import java.util.ArrayList;
import java.util.List;

import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.enums.AccountStatus;
import net.nilsghesquiere.infernalclients.LoLAccountInfernalJDBCClient;
import net.nilsghesquiere.infernalclients.LolAccountInfernalClient;
import net.nilsghesquiere.managerclients.LolAccountManagerClient;
import net.nilsghesquiere.managerclients.LolAccountManagerRESTClient;
import net.nilsghesquiere.util.wrappers.LolMixedAccountMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

public class LolAccountService {
	private static final Logger LOGGER = LoggerFactory.getLogger(LolAccountService.class);
	private final ClientSettings clientSettings;
	private final LolAccountInfernalClient infernalClient;
	private final LolAccountManagerClient managerClient;

	
	public LolAccountService(ClientSettings clientSettings){
		this.infernalClient =  new LoLAccountInfernalJDBCClient(clientSettings.getInfernalMap());
		if(clientSettings.getPort().equals("")){
			this.managerClient = new LolAccountManagerRESTClient(clientSettings.getWebServer(), clientSettings.getUsername(), clientSettings.getPassword(), clientSettings.getDebugHTTP());
		} else {
			this.managerClient = new LolAccountManagerRESTClient(clientSettings.getWebServer() + ":" + clientSettings.getPort(), clientSettings.getUsername(), clientSettings.getPassword(), clientSettings.getDebugHTTP());
		}
		this.clientSettings = clientSettings;
	}
	
	public boolean checkPragmas(){
		return infernalClient.checkPragmas();
	}
	
	public boolean exchangeAccounts(){
		//TODO fix bug: if for some reason both clients have same accs in infernalbot database:
		//     Client1 uploads the accs and puts them on READY, after that loads them and puts them on IN USE;
		//     Client2 uploads the accs and does the same!!!! --> solution: check on assigned to
		LolMixedAccountMap sendMap = prepareAccountsToSend();
		//lege map niet senden
		if(sendMap == null || managerClient.sendInfernalAccounts(clientSettings.getUserId(), sendMap)){
			if(sendMap != null){
				infernalClient.deleteAllAccounts();
			}
			List<LolAccount> accountsForInfernal = managerClient.getUsableAccounts(clientSettings.getUserId(), clientSettings.getClientRegion(), clientSettings.getAccountAmount());
			int addedInfernalAccounts = 0;
			if (!accountsForInfernal.isEmpty()){
				addedInfernalAccounts = infernalClient.insertAccounts(accountsForInfernal, false);
			}
			if (addedInfernalAccounts > 0){
				for (LolAccount lolAccount : accountsForInfernal){
					lolAccount.setAccountStatus(AccountStatus.IN_USE);
					lolAccount.setAssignedTo(clientSettings.getClientTag());
				}
				managerClient.updateLolAccounts(clientSettings.getUserId(), accountsForInfernal);
			} else {
				//The accounts are available again
				for (LolAccount lolAccount : accountsForInfernal){
					lolAccount.setAccountStatus(AccountStatus.READY_FOR_USE);
					lolAccount.setAssignedTo("");
				}
				managerClient.updateLolAccounts(clientSettings.getUserId(), accountsForInfernal);
			}
			if (clientSettings.getAccountBuffer() > 0){
				List<LolAccount> accountsForInfernalBuffer = managerClient.getBufferAccounts(clientSettings.getUserId(), clientSettings.getClientRegion(), clientSettings.getAccountBuffer());
				int addedInfernalBufferAccounts = 0;
				if (!accountsForInfernalBuffer.isEmpty()){
					addedInfernalBufferAccounts = infernalClient.insertAccounts(accountsForInfernalBuffer, true);
				}
				if (addedInfernalBufferAccounts > 0){
					for (LolAccount lolAccount : accountsForInfernalBuffer){
						lolAccount.setAccountStatus(AccountStatus.IN_BUFFER);
						lolAccount.setAssignedTo(clientSettings.getClientTag());
					}
					managerClient.updateLolAccounts(clientSettings.getUserId(), accountsForInfernalBuffer);
				} else {
					//The accounts are available again
					for (LolAccount lolAccount : accountsForInfernal){
						lolAccount.setAccountStatus(AccountStatus.READY_FOR_USE);
						lolAccount.setAssignedTo("");
					}
					managerClient.updateLolAccounts(clientSettings.getUserId(), accountsForInfernal);
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
	
	public boolean setAccountsAsReadyForUse(){
		LolMixedAccountMap sendMap = prepareAccountsToSend();
		try{
			if(managerClient.sendInfernalAccounts(clientSettings.getUserId(), sendMap)){
				LOGGER.info("Updated accounts on server");
				infernalClient.deleteAllAccounts();
			} else {
				LOGGER.error("Failure updating accounts on server.");
				return false;
			}
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure updating accounts the server");
			LOGGER.debug(e.getMessage());
			return false;
		}
		return true;
	}
	
	public void updateAccountsOnServer(){
		List<LolAccount> accountsForInfernal = new ArrayList<>();
		List<LolAccount> accountsFromJDBC = infernalClient.getAccounts(false);
		if (!accountsFromJDBC.isEmpty()){
			for (LolAccount accountFromJDBC : accountsFromJDBC){
				LolAccount accountFromREST = managerClient.getByUserIdRegionAndAccount(clientSettings.getUserId(), accountFromJDBC.getRegion(),accountFromJDBC.getAccount());
				//added here to only touch accounts assigned to this client
				if(accountFromREST != null && !accountFromREST.getAssignedTo().equals("")){ //nullpointer if it were to be empty, which shouldn't happen anyway
					if(accountFromREST.getAssignedTo().equals(clientSettings.getClientTag())){
						accountFromJDBC.setId(accountFromREST.getId());
						accountFromJDBC.setMaxLevel(accountFromREST.getMaxLevel());
						accountFromJDBC.setMaxBe(accountFromREST.getMaxBe());
						accountFromJDBC.setRegion(accountFromREST.getRegion());
						accountFromJDBC.setInfo(accountFromREST.getInfo());
						accountFromJDBC.setPriority(accountFromREST.getPriority());
						accountFromJDBC.setActive(accountFromREST.isActive());
						if (accountFromJDBC.getAccountStatus() == AccountStatus.ERROR || accountFromJDBC.getAccountStatus() == AccountStatus.BANNED){
							accountFromJDBC.setActive(false);
						} else {
							accountFromJDBC.setAccountStatus(accountFromREST.getAccountStatus());
						}
						accountFromJDBC.setAssignedTo(accountFromREST.getAssignedTo());
						accountsForInfernal.add(accountFromJDBC);
					}
				}
			}
		}
		if(!accountsForInfernal.isEmpty()){
			managerClient.updateLolAccounts(clientSettings.getUserId(), accountsForInfernal);
		}
	}
	
	public int countActiveAccounts() {
		return infernalClient.countActiveAccounts();
	}
	
	private LolMixedAccountMap prepareAccountsToSend(){
		LolMixedAccountMap lolAccountMap = new LolMixedAccountMap();
		List<LolAccount> newAccounts = new ArrayList<>();
		List<LolAccount> accountsFromJDBC = infernalClient.getAccounts(true);
		if (!accountsFromJDBC.isEmpty()){
			for (LolAccount accountFromJDBC : accountsFromJDBC){
				LolAccount accountFromREST = managerClient.getByUserIdRegionAndAccount(clientSettings.getUserId(), accountFromJDBC.getRegion(),accountFromJDBC.getAccount());
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
						//set active flag 
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

	//TEST METHODS
	public void testPragmas(){
		String oldPragmas = infernalClient.getOldPragmas();
		String newPragmas = infernalClient.getNewPragmas();
		if (newPragmas.equals(oldPragmas)){
			LOGGER.info("Accounts pragmas identitical");
			LOGGER.debug("Old accounts pragmas: " + oldPragmas);
			LOGGER.debug("New accounts pragmas: " + newPragmas);
		} else {
			LOGGER.info("Accounts pragmas changed");
			LOGGER.info("Old accounts pragmas: " + oldPragmas);
			LOGGER.info("New accounts pragmas: " + newPragmas);
		}
	}

} 
