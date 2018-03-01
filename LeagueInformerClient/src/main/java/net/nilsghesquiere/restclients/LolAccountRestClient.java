package net.nilsghesquiere.restclients;

import java.util.Collections;
import java.util.List;

import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.util.wrappers.LolAccountMap;
import net.nilsghesquiere.util.wrappers.LolAccountWrapper;

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
		LolAccountWrapper jsonResponse = response.getBody();
		return jsonResponse.getMap().get("data");
	} 
	
	public void test(){
		HttpHeaders httpHeaders = restTemplate.headForHeaders("http://localhost:8080/api/accounts/user/3");
		System.out.println(httpHeaders.toString());
	}
}
