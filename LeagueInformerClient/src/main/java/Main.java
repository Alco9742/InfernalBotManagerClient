import java.io.IOException;
import java.util.List;

import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.enums.Region;
import net.nilsghesquiere.restclients.LolAccountRestClient;
import net.nilsghesquiere.services.LolAccountService;

public class Main {
	private static final Long USER_ID = 3L;
	private static final String  INFERNAL_MAP_LOCATION = "C:/temp/";
	private static final Integer AMOUNT_OF_ACCOUNTS = 5;
	private static final String CLIENT_TAG = "TestClient1";
	private static final Region CLIENT_REGION = Region.EUW;
	
	public static void main(String[] args) throws InterruptedException, IOException {
		LolAccountService lolAccountService = new LolAccountService(INFERNAL_MAP_LOCATION);
	//	lolAccountService.exchangeAccounts(USER_ID, CLIENT_REGION, CLIENT_TAG, AMOUNT_OF_ACCOUNTS);
		LolAccountRestClient rest = new LolAccountRestClient();
		List<LolAccount> accounts = rest.getUsableAccounts(USER_ID, CLIENT_REGION, AMOUNT_OF_ACCOUNTS);
		for (LolAccount account : accounts){
			System.out.println(account);
		}
	}
}
