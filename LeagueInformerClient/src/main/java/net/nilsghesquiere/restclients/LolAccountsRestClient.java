package net.nilsghesquiere.restclients;

import java.util.Collections;
import java.util.List;

import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.valueobjects.LolAccountListWrapper;
import net.nilsghesquiere.valueobjects.LolAccountMap;
import net.nilsghesquiere.valueobjects.LolAccountWrapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;


public class LolAccountsRestClient {
	private static final String URI_ACCOUNTS = "http://localhost:8080/api/accounts";
	private RestTemplate restTemplate = new RestTemplate();
	
	public LolAccount getLolAccount(Long accountid){
		LolAccountListWrapper lolAccountListWrapper = restTemplate.getForObject(URI_ACCOUNTS + "/" + accountid, LolAccountListWrapper.class);
		return lolAccountListWrapper.getMap().get("data").get(0);
	}
	
	public String getLolAccountJSON(Long accountid){
		String JSONstring = restTemplate.getForObject(URI_ACCOUNTS + "/" + accountid, String.class);
		return JSONstring;
	}
	public List<LolAccount >getUserLolAccounts(Long userid){
		LolAccountListWrapper lolAccountlistWrapper = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid, LolAccountListWrapper.class);
		return lolAccountlistWrapper.getMap().get("data");
	}
	
	public String getUserLolAccountsJSON(Long userid){
		String lolAccounts = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid, String.class);
		return lolAccounts;
	}
	
	public LolAccount createLolAccount(Long userid, LolAccount lolAccount) {
		LolAccountMap lolAccountMap = new LolAccountMap();
		lolAccountMap.add("0", lolAccount);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LolAccountMap> request = new HttpEntity<>(lolAccountMap, headers);
		LolAccountListWrapper createdAccountListWrapper = restTemplate.postForObject(URI_ACCOUNTS + "/user/" + userid,request , LolAccountListWrapper.class);
		return createdAccountListWrapper.getMap().get("data").get(0);
	}
	
	public LolAccount updateLolAccount(Long userid, LolAccount lolAccount) {
		LolAccountMap lolAccountMap = new LolAccountMap();
		lolAccountMap.add(lolAccount.getId().toString(), lolAccount);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LolAccountMap> request = new HttpEntity<>(lolAccountMap, headers);
		HttpEntity<LolAccountListWrapper> response = restTemplate.exchange(URI_ACCOUNTS + "/user/" + userid, HttpMethod.PUT,request , LolAccountListWrapper.class);
		LolAccountListWrapper lolAccountListWrapper = response.getBody();
		return lolAccountListWrapper.getMap().get("data").get(0);
	}
	
	public void test(){
		HttpHeaders httpHeaders = restTemplate.headForHeaders("http://localhost:8080/api/accounts/user/9");
		System.out.println(httpHeaders.toString());
	}
}
