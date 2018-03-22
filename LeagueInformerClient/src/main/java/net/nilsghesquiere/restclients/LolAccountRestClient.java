package net.nilsghesquiere.restclients;

import java.util.List;
import java.util.Map.Entry;

import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.enums.Region;
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
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


public class LolAccountRestClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(LolAccountRestClient.class);
	private final String URI_ACCOUNTS;
	private RestTemplate restTemplate = new RestTemplate();
	private HttpHeaders headers;
	
	public LolAccountRestClient(String uriServer, String username, String password) {
		this.URI_ACCOUNTS = uriServer +"api/accounts";
		//set auth
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username,password));
		//set headers
		headers = ProgramUtil.buildHttpHeaders();
	}

	public List<LolAccount> getUserLolAccounts(Long userid){
		LolAccountWrapper jsonResponse = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid, LolAccountWrapper.class);
		return jsonResponse.getMap().get("data");
	}
	
	public String getUserLolAccountsJSON(Long userid){
		String lolAccounts = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid, String.class);
		return lolAccounts;
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
		}
	}
	
	public LolAccount getByUserIdRegionAndAccount(Long userid, Region region, String account){
		try{
			LolAccount lolAccount = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid + "/region/" + region + "/account/" + account, LolAccount.class);
			LOGGER.debug("getByUserIdRegionAndAccount:" + lolAccount);
			return lolAccount;
		} catch (ResourceAccessException e){
			LOGGER.debug(e.getMessage());
			return null;
		}
	}

	public List<LolAccount> updateLolAccounts(Long userid, List<LolAccount> lolAccounts) {
		LolAccountMap lolAccountMap = new LolAccountMap();
		for(LolAccount lolAccount : lolAccounts){
			lolAccountMap.add(lolAccount.getId().toString(), lolAccount);
		}
		try{
			LOGGER.debug("updateLolAccounts (before):" + lolAccountMap.getMap().values());
			HttpEntity<LolAccountMap> request = new HttpEntity<>(lolAccountMap, headers);
			HttpEntity<LolAccountWrapper> response = restTemplate.exchange(URI_ACCOUNTS + "/user/" + userid, HttpMethod.PUT,request, LolAccountWrapper.class);
			LolAccountWrapper lolAccountWrapperResponse = response.getBody();
			List<LolAccount> returnAccounts = lolAccountWrapperResponse.getMap().get("data");
			LOGGER.debug("updateLolAccounts (after):" + returnAccounts);
			return returnAccounts;
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure updating accounts on the server");
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
			LOGGER.debug("sendInfernalAccounts - existing:" + map.getMap().values());
			LOGGER.debug("sendInfernalAccounts - new:" + map.getNewAccs());
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
		}
	}
	
	public void test(){
		HttpHeaders httpHeaders = restTemplate.headForHeaders("http://localhost:8080/api/accounts/user/3");
	}
}
