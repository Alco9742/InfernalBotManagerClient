import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
		
		//LolAccount[] lolAccounts = lolAccountsRestClient.getUserLolAccounts(3L);
		//System.out.println(Arrays.asList(lolAccounts));
		
		//LolAccount lolAccount = lolAccountsRestClient.getLolAccount(4L);
		//System.out.println(lolAccount);
		
		LolAccount accountToCreate = new LolAccount("JosDevos","JosDevos123","EUW", true);
		LolAccount createdAccount = lolAccountsRestClient.createLolAccount(6L, accountToCreate);
		System.out.println(createdAccount);
		
	}
}
