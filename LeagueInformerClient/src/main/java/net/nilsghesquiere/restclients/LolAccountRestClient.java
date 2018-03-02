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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;


public class LolAccountRestClient {
	private static final String URI_ACCOUNTS = "http://localhost:8080/api/accounts";
	private RestTemplate restTemplate = new RestTemplate();
	
	public List<LolAccount> getUserLolAccounts(Long userid){
		LolAccountWrapper jsonResponse = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid, LolAccountWrapper.class);
		return jsonResponse.getMap().get("data");
	}
	
	public String getUserLolAccountsJSON(Long userid){
		String lolAccounts = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid, String.class);
		return lolAccounts;
	}
	
	public List<LolAccount> getUsableAccounts(Long userid, Region region, Integer amount){
		//TODO debug this, something is going wrong
		LolAccountWrapper jsonResponse = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid + "/region/" + region + "/limit/" + amount, LolAccountWrapper.class);
		return jsonResponse.getMap().get("data");
	}
	
	public String getUsableAccountsJSON(Long userid, Region region, Integer amount){
		String lolAccounts = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid + "/region/" + region + "/limit/" + amount, String.class);
		return lolAccounts;
	}
	
	public LolAccount getByUserIdAndAccount(Long userid, String account){
		LolAccount lolAccount = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid + "/account/" + account, LolAccount.class);
		return lolAccount;
	}

	public List<LolAccount> updateLolAccounts(Long userid, List<LolAccount> lolAccounts) {
		LolAccountMap lolAccountMap = new LolAccountMap();
		for(LolAccount lolAccount : lolAccounts){
			lolAccountMap.add(lolAccount.getId().toString(), lolAccount);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LolAccountMap> request = new HttpEntity<>(lolAccountMap, headers);
		HttpEntity<LolAccountWrapper> response = restTemplate.exchange(URI_ACCOUNTS + "/user/" + userid, HttpMethod.PUT,request, LolAccountWrapper.class);
		LolAccountWrapper lolAccountWrapperResponse = response.getBody();
		return lolAccountWrapperResponse.getMap().get("data");
	} 
	
	public boolean sendInfernalAccounts(Long userid, LolMixedAccountMap map){
		boolean result = true;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LolMixedAccountMap> request = new HttpEntity<>(map, headers);
		HttpEntity<StringResponseMap> response = restTemplate.exchange(URI_ACCOUNTS + "/user/" + userid + "/infernalImport", HttpMethod.PUT,request, StringResponseMap.class);
		StringResponseMap stringResponseMap = response.getBody();
		for(Entry<String,String> entry : stringResponseMap.getMap().entrySet()){
			if(!entry.getValue().equals("OK")){
				System.out.println("Error(" + entry.getKey() + ": " + entry.getValue() + ")");
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
