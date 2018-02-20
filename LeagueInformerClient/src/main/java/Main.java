import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.web.client.HttpClientErrorException;

import net.nilsghesquiere.entities.AppUser;
import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.restclients.LolAccountsRestClient;

public class Main {

	public static void main(String[] args) throws InterruptedException, IOException {
		//leagueRTMPTest();
		leagueInformerAPITest();
	}
	
	private static void leagueRTMPTest(){
		//Asynchronous
		//LoginQueue queue = new LoginQueue(Shard.NA);
		//QueueTimer timer = queue.waitInQueue("Username", "password");
		//String authToken = timer.await();
		//System.out.println(timer.getPosition());
		//System.out.println(timer.getCurrentDelay());
		//System.out.println(timer.getName());
		//System.out.println(timer.isAlive());
		//System.out.println(timer.isFinished());
		//System.out.println(timer.isDaemon());
		
		//Synchronous
		//LoginQueue queue = new LoginQueue(Shard.NA);
		//String authToken = queue.waitInQueueBlocking("Username", "password");
		//System.out.println(Shard.NA.apiUrl);
		//System.out.println(Shard.NA.loginQueue);
		//System.out.println(Shard.NA.prodUrl);
	}

	private static void leagueInformerAPITest(){
		LolAccountsRestClient lolAccountsRestClient = new LolAccountsRestClient();
	//	leagueInformerAPIGetJSONTest(lolAccountsRestClient);
	//	leagueInformerAPIGetTest(lolAccountsRestClient);
	//	leagueInformerAPIGetByUserJSONTest(lolAccountsRestClient);
	//	leagueInformerAPIGetByUserTest(lolAccountsRestClient);
	//	leagueInformerAPICreateTest(lolAccountsRestClient);
		leagueInformerAPIUpdateTest(lolAccountsRestClient);
	}

	private static void leagueInformerAPIGetTest(LolAccountsRestClient lolAccountsRestClient){
		try{
			LolAccount account = lolAccountsRestClient.getLolAccount(13L);
			System.out.println(account);
		} catch(HttpClientErrorException e){
			System.out.println(e.getMessage());
			System.out.println(e.getResponseHeaders().toString());
			System.out.println(e.getResponseBodyAsString());

		}
	}
	
	private static void leagueInformerAPIGetJSONTest(LolAccountsRestClient lolAccountsRestClient){
		try{
			String account = lolAccountsRestClient.getLolAccountJSON(13L);
			System.out.println(account);
		} catch(HttpClientErrorException e){
			System.out.println(e.getMessage());
			System.out.println(e.getResponseHeaders().toString());
			System.out.println(e.getResponseBodyAsString());

		}
	}
	
	private static void leagueInformerAPIGetByUserTest(LolAccountsRestClient lolAccountsRestClient){
		try{
			List<LolAccount> lolAccounts = lolAccountsRestClient.getUserLolAccounts(9L);
			System.out.println(lolAccounts);
		} catch(HttpClientErrorException e){
			System.out.println(e.getMessage());
			System.out.println(e.getResponseHeaders().toString());
			System.out.println(e.getResponseBodyAsString());
		}
	}
	
	private static void leagueInformerAPIGetByUserJSONTest(LolAccountsRestClient lolAccountsRestClient){
		try{
			String lolAccounts = lolAccountsRestClient.getUserLolAccountsJSON(9L);
			System.out.println(lolAccounts);
		} catch(HttpClientErrorException e){
			System.out.println(e.getMessage());
			System.out.println(e.getResponseHeaders().toString());
			System.out.println(e.getResponseBodyAsString());
		}
	}
	
	private static void leagueInformerAPICreateTest(LolAccountsRestClient lolAccountsRestClient){
		try{
			LolAccount accountToCreate = new LolAccount("JosDevos","JosDevos123","EUW", true);
			LolAccount updatedAccount = lolAccountsRestClient.createLolAccount(9L, accountToCreate);
			System.out.println(updatedAccount);
		} catch(HttpClientErrorException e){
			System.out.println(e.getMessage());
			System.out.println(e.getResponseHeaders().toString());
			System.out.println(e.getResponseBodyAsString());

		}
	}
	
	private static void leagueInformerAPIUpdateTest(LolAccountsRestClient lolAccountsRestClient){
		try{
			LolAccount account = lolAccountsRestClient.getLolAccount(77L);
			System.out.println(account);
			account.setUsername("Nils");
			account.setRegion("EUNE");
			LolAccount updatedAccount = lolAccountsRestClient.updateLolAccount(9L, account);
			System.out.println(updatedAccount);
		} catch(HttpClientErrorException e){
			System.out.println(e.getMessage());
			System.out.println(e.getResponseHeaders().toString());
			System.out.println(e.getResponseBodyAsString());

		}
	}
}
