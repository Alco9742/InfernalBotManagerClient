import java.io.IOException;
import java.util.List;

import net.nilsghesquiere.entities.InfernalSettings;
import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.enums.Region;
import net.nilsghesquiere.restclients.InfernalSettingsRestClient;
import net.nilsghesquiere.restclients.LolAccountRestClient;

import org.springframework.web.client.HttpClientErrorException;

public class Main {

	public static void main(String[] args) throws InterruptedException, IOException {
		leagueInformerAPITest();
	}

	private static void leagueInformerAPITest(){
		LolAccountRestClient lolAccountsRestClient = new LolAccountRestClient();
		InfernalSettingsRestClient infernalSettingsRestClient = new InfernalSettingsRestClient();
		try{
			//List<LolAccount> lolAccounts = leagueInformerAPIGetByUserTest(lolAccountsRestClient);
			//List<LolAccount> lolAccounts = leagueInformerAPIUpdateTest(lolAccountsRestClient);
			//System.out.println(lolAccounts);
			
			InfernalSettings infernalSettings = leagueInformerAPIGetInfernalSettingsByUserTest(infernalSettingsRestClient);
			System.out.println(infernalSettings);
			
		} catch(HttpClientErrorException e){
			System.out.println(e.getResponseBodyAsString());
		}

	}
	
	private static List<LolAccount> leagueInformerAPIGetByUserTest(LolAccountRestClient lolAccountsRestClient){
		List<LolAccount> lolAccounts = lolAccountsRestClient.getUserLolAccounts(3L);
		return lolAccounts;
	}
	
	
	private static List<LolAccount> leagueInformerAPIUpdateTest(LolAccountRestClient lolAccountsRestClient){
		List<LolAccount> lolAccounts = lolAccountsRestClient.getUserLolAccounts(3L);
		lolAccounts.get(1).setAccount("testRest");
		return lolAccountsRestClient.updateLolAccounts(3L, lolAccounts);
	}
	
	private static InfernalSettings leagueInformerAPIGetInfernalSettingsByUserTest(InfernalSettingsRestClient infernalSettingsRestClient){
		System.out.println(infernalSettingsRestClient.getUserInfernalSettingJSON(3L));
		return infernalSettingsRestClient.getUserInfernalSettings(3L);
	}
}
