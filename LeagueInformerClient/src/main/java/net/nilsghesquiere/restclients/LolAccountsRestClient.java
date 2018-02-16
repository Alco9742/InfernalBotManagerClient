package net.nilsghesquiere.restclients;

import java.util.Collections;

import net.nilsghesquiere.entities.LolAccount;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class LolAccountsRestClient {
	private static final String URI_ACCOUNTS = "http://localhost:8080/api/accounts";
	private RestTemplate restTemplate = new RestTemplate();
	
	public LolAccount getLolAccount(Long accountid){
		LolAccount lolAccount = restTemplate.getForObject(URI_ACCOUNTS + "/" + accountid, LolAccount.class);
		return lolAccount;
	}
	
	public LolAccount[] getUserLolAccounts(Long userid){
		LolAccount[] lolAccounts = restTemplate.getForObject(URI_ACCOUNTS + "/user/" + userid, LolAccount[].class);
		return lolAccounts;
	}
	
	public LolAccount createLolAccount(Long userid, LolAccount lolAccount) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		LolAccount createdAccount = restTemplate.postForObject(URI_ACCOUNTS + "/user/" + userid, entity, LolAccount.class); 
		return createdAccount;
	}
	
	public void test(){
		HttpHeaders httpHeaders = restTemplate.headForHeaders("http://localhost:8080/api/accounts/user/9");
		System.out.println(httpHeaders.toString());
	}
}
