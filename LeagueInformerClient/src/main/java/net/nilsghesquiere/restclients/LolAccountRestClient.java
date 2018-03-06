package net.nilsghesquiere.restclients;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.enums.Region;
import net.nilsghesquiere.util.wrappers.LolAccountMap;
import net.nilsghesquiere.util.wrappers.LolAccountWrapper;
import net.nilsghesquiere.util.wrappers.LolMixedAccountMap;
import net.nilsghesquiere.util.wrappers.StringResponseMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;


public class LolAccountRestClient {
	private static final Logger LOGGER = LoggerFactory.getLogger("InfernalBotManagerAccountDatabaseClient");
	private final String URI_ACCOUNTS;
	private RestTemplate restTemplate = new RestTemplate();
	
	public LolAccountRestClient(String uriServer) {
		this.URI_ACCOUNTS = uriServer +"api/accounts";
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
		LolAccountWrapper jsonResponse = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid + "/region/" + region + "/limit/" + amount, LolAccountWrapper.class);
		List<LolAccount> returnAccounts = jsonResponse.getMap().get("data");
		if (returnAccounts.size() == amount){
			LOGGER.info("Succesfully received " + returnAccounts.size() + " accounts from the InfernalBotManager database.");
		} else {
			LOGGER.info("Warning: only found " + returnAccounts.size() + " eligible accounts in the the InfernalBotManager database.");
		}
		return returnAccounts;
	}
	
	public String getUsableAccountsJSON(Long userid, Region region, Integer amount){
		String lolAccounts = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid + "/region/" + region + "/limit/" + amount, String.class);
		return lolAccounts;
	}
	
	public LolAccount getByUserIdAndAccount(Long userid, String account){
		LolAccount lolAccount = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid + "/account/" + account, LolAccount.class);
		LOGGER.debug("getByUserIdAndAccount:" + lolAccount);
		return lolAccount;
	}

	public List<LolAccount> updateLolAccounts(Long userid, List<LolAccount> lolAccounts) {
		LolAccountMap lolAccountMap = new LolAccountMap();
		for(LolAccount lolAccount : lolAccounts){
			lolAccountMap.add(lolAccount.getId().toString(), lolAccount);
		}
		LOGGER.debug("updateLolAccounts (before):" + lolAccountMap.getMap().values());
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LolAccountMap> request = new HttpEntity<>(lolAccountMap, headers);
		HttpEntity<LolAccountWrapper> response = restTemplate.exchange(URI_ACCOUNTS + "/user/" + userid, HttpMethod.PUT,request, LolAccountWrapper.class);
		LolAccountWrapper lolAccountWrapperResponse = response.getBody();
		List<LolAccount> returnAccounts = lolAccountWrapperResponse.getMap().get("data");
		LOGGER.debug("updateLolAccounts (after):" + returnAccounts);
		return returnAccounts;
	} 
	
	public boolean sendInfernalAccounts(Long userid, LolMixedAccountMap map){
		boolean result = true;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LolMixedAccountMap> request = new HttpEntity<>(map, headers);
		HttpEntity<StringResponseMap> response = restTemplate.exchange(URI_ACCOUNTS + "/user/" + userid + "/infernalImport", HttpMethod.PUT,request, StringResponseMap.class);
		StringResponseMap stringResponseMap = response.getBody();
		LOGGER.debug("sendInfernalAccounts - existing:" + map.getMap().values());
		LOGGER.debug("sendInfernalAccounts - new:" + map.getNewAccs());
		for(Entry<String,String> entry : stringResponseMap.getMap().entrySet()){
			if(!entry.getValue().equals("OK")){
				LOGGER.info("Error(" + entry.getKey() + ": " + entry.getValue() + ")");
				result = false;
			}
		}
		return result;
	}
	
	public void test(){
		HttpHeaders httpHeaders = restTemplate.headForHeaders("http://localhost:8080/api/accounts/user/3");
		System.out.println(httpHeaders.toString());
	}
}
