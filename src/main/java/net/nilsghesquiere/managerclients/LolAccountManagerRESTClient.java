package net.nilsghesquiere.managerclients;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map.Entry;

import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.enums.Region;
import net.nilsghesquiere.security.SSLBasicAuthenticationRestTemplate;
import net.nilsghesquiere.util.ProgramUtil;
import net.nilsghesquiere.util.wrappers.LolAccountMap;
import net.nilsghesquiere.util.wrappers.LolAccountWrapper;
import net.nilsghesquiere.util.wrappers.LolMixedAccountMap;
import net.nilsghesquiere.util.wrappers.StringResponseMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;



public class LolAccountManagerRESTClient implements LolAccountManagerClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(LolAccountManagerRESTClient.class);
	private final String URI_ACCOUNTS;
	private RestTemplate restTemplate;
	private HttpHeaders headers;
	
	public LolAccountManagerRESTClient(String uriServer, String username, String password, Boolean debugHTTP) {
		this.URI_ACCOUNTS = uriServer +"/api/accounts";
		
		try {
			this.restTemplate = new SSLBasicAuthenticationRestTemplate(username,password,debugHTTP);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			LOGGER.debug(e.getMessage());
		}	
		
		//set headers
		headers = ProgramUtil.buildHttpHeaders();
	}

	public List<LolAccount> getUserLolAccounts(Long userid){
		LolAccountWrapper jsonResponse = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid, LolAccountWrapper.class);
		return jsonResponse.getMap().get("data");
	}
	
	public List<LolAccount> getUsableAccounts(Long userid, Region region, Integer amount){
		try{
			LolAccountWrapper jsonResponse = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid + "/region/" + region + "/limit/" + amount, LolAccountWrapper.class);
			List<LolAccount> returnAccounts = jsonResponse.getMap().get("data");
			if (returnAccounts.size() == amount){
				LOGGER.info("Received " + returnAccounts.size() + " accounts from the InfernalBotManager server.");
			} else {
				if(returnAccounts.size() > 0){
					LOGGER.warn("Only found " + returnAccounts.size() + " eligible accounts on the the InfernalBotManager server.");
				} else {
					LOGGER.error("No eligible accounts on the the InfernalBotManager server.");
				}
			}
			return returnAccounts;
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure getting accounts from the server");
			LOGGER.debug(e.getMessage());
			return null;
		} catch (HttpServerErrorException e){
			LOGGER.warn("Failure getting accounts from the server");
			LOGGER.debug(e.getMessage());
			return null;
		}
	}
	
	public List<LolAccount> getBufferAccounts(Long userid, Region region, Integer amount){
		try{
			LolAccountWrapper jsonResponse = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid + "/region/" + region + "/limit/" + amount + "/buffer/", LolAccountWrapper.class);
			List<LolAccount> returnAccounts = jsonResponse.getMap().get("data");
			if (returnAccounts.size() == amount){
				LOGGER.info("Received " + returnAccounts.size() + " bufferaccounts from the InfernalBotManager server.");
			} else {
				if(returnAccounts.size() > 0){
					LOGGER.warn("Only found " + returnAccounts.size() + " eligible bufferaccounts on the the InfernalBotManager server.");
				} else {
					LOGGER.warn("No eligible bufferaccounts on the the InfernalBotManager server.");
				}
			}
			return returnAccounts;
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure getting bufferaccounts from the server");
			LOGGER.debug(e.getMessage());
			return null;
		} catch (HttpServerErrorException e){
			LOGGER.warn("Failure getting bufferaccounts from the server");
			LOGGER.debug(e.getMessage());
			return null;
		}
	}
	
	public LolAccount getByUserIdRegionAndAccount(Long userid, Region region, String account){
		try{
			LolAccount lolAccount = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid + "/region/" + region + "/account/" + account, LolAccount.class);
			return lolAccount;
		} catch (ResourceAccessException e){
			LOGGER.debug(e.getMessage());
			return null;
		} catch (HttpServerErrorException e){
			LOGGER.debug(e.getMessage());
			return null;
		}
	}

	public List<LolAccount> updateLolAccounts(Long userid, List<LolAccount> lolAccounts) {
		LolAccountMap lolAccountMap = new LolAccountMap();
		for(LolAccount lolAccount : lolAccounts){
			lolAccountMap.add(lolAccount.getId().toString(), lolAccount);
			LOGGER.debug("updateLolAccounts");
			LOGGER.debug(lolAccount.toString());
			LOGGER.debug(lolAccountMap.getMap().get(lolAccount.getId().toString()).toString());
		}
		try{
			HttpEntity<LolAccountMap> request = new HttpEntity<>(lolAccountMap, headers);
			HttpEntity<LolAccountWrapper> response = restTemplate.exchange(URI_ACCOUNTS + "/user/" + userid, HttpMethod.PUT,request, LolAccountWrapper.class);
			LolAccountWrapper lolAccountWrapperResponse = response.getBody();
			List<LolAccount> returnAccounts = lolAccountWrapperResponse.getMap().get("data");
			return returnAccounts;
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure updating accounts on the server");
			LOGGER.debug(e.getMessage());
			return null;
		} catch (HttpServerErrorException e){
			LOGGER.warn("updating getting accounts from the server");
			LOGGER.debug(e.getMessage());
			return null;
		}
	} 
	
	public boolean sendInfernalAccounts(Long userid, LolMixedAccountMap map){
		try{
			boolean result = true;
			HttpEntity<LolMixedAccountMap> request = new HttpEntity<>(map, headers);
			HttpEntity<StringResponseMap> response = restTemplate.exchange(URI_ACCOUNTS + "/user/" + userid + "/infernalImport", HttpMethod.PUT,request, StringResponseMap.class);
			StringResponseMap stringResponseMap = response.getBody();
			for(Entry<String,String> entry : stringResponseMap.getMap().entrySet()){
				if(!entry.getValue().equals("OK")){
					LOGGER.warn("Error(" + entry.getKey() + ": " + entry.getValue() + ")");
					result = false;
				}
			}
			if (result = true){
				LOGGER.info("Updated accounts on the InfernalBotManager server");
			}
			return result;
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure updating accounts on the server");
			LOGGER.debug(e.getMessage());
			return false;
		} catch (HttpServerErrorException e){
			LOGGER.warn("Failure updating accounts on the server");
			LOGGER.debug(e.getMessage());
			return false;
		}
	}
}
