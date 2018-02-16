package net.nilsghesquiere.restclients;

import java.io.IOException;

import net.nilsghesquiere.entities.LolAccount;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppUserRestClient {
	private static final String URI_USERS = "http://localhost:8080/api/users";
	private RestTemplate restTemplate = new RestTemplate();
	
	public LolAccount[] getAllAppUsers(){
		LolAccount[] lolAccounts = restTemplate.getForObject(URI_USERS, LolAccount[].class);
		return lolAccounts;
	}

	public LolAccount getAppUserById(Long id){
		LolAccount lolAccount = restTemplate.getForObject(URI_USERS + "/" + id, LolAccount.class);
		return lolAccount;
	}
	
	public String getAllAppUsersJSON(){
		String response = restTemplate.getForObject(URI_USERS, String.class);
		return response;
	}

	public String getAppUserByIdJSON(Long id) throws IOException{
		String response = restTemplate.getForObject(URI_USERS +"/" + id, String.class);
		return response;
	}
}
