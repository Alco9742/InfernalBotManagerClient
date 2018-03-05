package net.nilsghesquiere;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import net.nilsghesquiere.enums.Region;
import net.nilsghesquiere.runnables.CheckInfernalRunnable;
import net.nilsghesquiere.services.LolAccountService;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger("InfernalBotManager Client");
	private static final String INI_NAME = "settings.ini";
	private static Long USER_ID;
	private static String  INFERNAL_MAP_LOCATION;
	private static Integer AMOUNT_OF_ACCOUNTS;
	private static String CLIENT_TAG;
	private static Region CLIENT_REGION;
	private static String WEBSERVER;
	private static String PORT;
	private static Boolean REBOOT;
	private static Integer REBOOT_TIME;
	private static Boolean BYPASS_DEV_CHECKS;
	private static final String INFERNAL_PROG_NAME = "notepad.exe";
	
	public static void main(String[] args){
		boolean initializationOK= initialize();
		boolean connectionOK = true;
		if (BYPASS_DEV_CHECKS == false){
			try {
				connectionOK = checkConnection();
			} catch (IOException e) {
				LOGGER.info("Error establishing connection: " + e.getMessage());
				LOGGER.debug(e.getMessage());
			} catch (InterruptedException e) {
				LOGGER.info("Error establishing connection: " + e.getMessage());
				LOGGER.debug(e.getMessage());
			}
		}
		if(initializationOK && connectionOK){
			//upload accounts in infernal db to server and gtab accounts for use
			if (accountExchange()){
				//start infernalbot checker in a thread
				CheckInfernalRunnable checkInfernalRunnable = new CheckInfernalRunnable(INFERNAL_MAP_LOCATION,INFERNAL_PROG_NAME);
				Thread checkInfernalThread = new Thread(checkInfernalRunnable);
				checkInfernalThread.setDaemon(true); //stop when main trhead stops
				checkInfernalRunnable.run();
			} else {
				LOGGER.info("Closing InfernalBotManager Client");
				System.exit(0);
			}
		} else {
			LOGGER.info("Closing InfernalBotManager Client");
			System.exit(0);
		}
	}



	private static boolean initialize(){
		String iniFile = System.getProperty("user.dir") + "/" + INI_NAME;
		Path iniFilePath = Paths.get(iniFile);
		Boolean hasError= false;
		if(Files.exists(iniFilePath)){
			try {
				Wini ini = new Wini(new File(iniFile));
				USER_ID = ini.get("main", "userid", Long.class);
				INFERNAL_MAP_LOCATION = ini.get("main", "infernalmap", String.class);
				AMOUNT_OF_ACCOUNTS = ini.get("main", "accounts", Integer.class);
				CLIENT_TAG = ini.get("main", "clienttag", String.class);
				CLIENT_REGION = ini.get("main", "region", Region.class);
				WEBSERVER = ini.get("main", "webserver", String.class);
				PORT = ini.get("main", "port", String.class);
				REBOOT = ini.get("main", "reboot", boolean.class);
				REBOOT_TIME = ini.get("main", "reboottime", Integer.class);
				BYPASS_DEV_CHECKS = ini.get("main", "bypassdev", boolean.class);
				if(USER_ID == null){
					LOGGER.info("Error in settings.ini: value '" + USER_ID + "' is not accepted for userid");
					hasError = true;
				}
				if(INFERNAL_MAP_LOCATION == null){
					LOGGER.info("Error in settings.ini: value '" + INFERNAL_MAP_LOCATION + "' is not accepted for infernalmap");
					hasError = true;
				}
				if(AMOUNT_OF_ACCOUNTS == null){
					LOGGER.info("Error in settings.ini: value '" + AMOUNT_OF_ACCOUNTS + "' is not accepted for accounts");
				} 
				if(CLIENT_TAG == null){
					LOGGER.info("Error in settings.ini: value '" + CLIENT_TAG + "' is not accepted for clienttag");
				}
				if(CLIENT_REGION == null){
					LOGGER.info("Error in settings.ini: value '" + CLIENT_REGION + "' is not accepted for region");
					hasError = true;
				}
				if(WEBSERVER == null){
					LOGGER.info("Error in settings.ini: value '" + WEBSERVER + "' is not accepted for webserver");
					hasError = true;
				}
				if(PORT == null){
					LOGGER.info("Error in settings.ini: value '" + PORT + "' is not accepted for port");
					hasError = true;
				}
				if(REBOOT == null){
					LOGGER.info("Error in settings.ini: value '" + REBOOT + "' is not accepted for reboot");
					hasError = true;
				} else {
					if (REBOOT){
						if(REBOOT_TIME == null){
							LOGGER.info("Error in settings.ini: value '" + REBOOT_TIME + "' is not accepted for reboottime");
							hasError = true;
						} else{
							Process p = Runtime.getRuntime().exec("shutdown -r -t " + REBOOT_TIME);
							LOGGER.info("Shutdown scheduled in " + REBOOT_TIME + "seconds");
						}
					}
				}
				LOGGER.info("Succesfully loaded  from settings.ini");
			if(INFERNAL_MAP_LOCATION == null){
				LOGGER.info("Error in settings.ini: value '" + INFERNAL_MAP_LOCATION + "' is not accepted for infernalmap");
				hasError = true;
			}
			if(BYPASS_DEV_CHECKS == null){
				BYPASS_DEV_CHECKS = false;
			}
			} catch (InvalidFileFormatException e) {
				LOGGER.info("Error: " + e.getMessage());
				LOGGER.debug(e.getMessage());
			} catch (IOException e) {
				LOGGER.info("Error: " + e.getMessage());
				LOGGER.debug(e.getMessage());
			}
		} else {
			LOGGER.info("Error: .ini file not found at path: " + iniFilePath);
			hasError = true;
		}
		return !hasError;
	}
	
	private static boolean checkConnection() throws IOException, InterruptedException {
		//Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 8.8.8.8");
		Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 google.be");
		int returnVal = p1.waitFor();
		boolean reachable = (returnVal==0);
		if (reachable){
			LOGGER.info("Network connection established");
			Process p2 = java.lang.Runtime.getRuntime().exec("ping -n 1 " + WEBSERVER);
			int returnVal2 = p2.waitFor();
			boolean reachable2 = (returnVal2==0);
			if (reachable2){
				LOGGER.info("Connection to the server established");
				return true;
			} else {
				LOGGER.info("Error: Connection to the server failed");
				return false;
			}
		} else {
			LOGGER.info("Error: Network connection failed");
			return false;
		}
	}
	
	private static boolean accountExchange(){
		if(backUpInfernalDatabase(INFERNAL_MAP_LOCATION)){
			try{
				LolAccountService lolAccountService = new LolAccountService(INFERNAL_MAP_LOCATION, "http://" + WEBSERVER + ":" + PORT);
				lolAccountService.exchangeAccounts(USER_ID, CLIENT_REGION, CLIENT_TAG, AMOUNT_OF_ACCOUNTS);
				return true;
			} catch (ResourceAccessException e) {
				LOGGER.info("Error retrieving the requested resource");
				LOGGER.debug(e.getMessage());
				return false;
			}
		} else {
			return false;
		}
	}
	
	private static boolean checkDir(){
		Path infernalPath = Paths.get(INFERNAL_MAP_LOCATION + INFERNAL_PROG_NAME);
		return Files.exists(infernalPath);
	}
	private static boolean backUpInfernalDatabase(String infernalMap){
		if(checkDir()){
			LOGGER.info("Successfully located Infernalbot");
			Path backupDir = Paths.get(infernalMap + "InfernalManager") ;
			Path file = Paths.get(infernalMap + "InfernalDatabase.sqlite") ;
			Path backupFile = Paths.get(infernalMap + "InfernalManager/InfernalDatabase.bak") ;
			if(!Files.exists(backupDir)){
				try {
					Files.createDirectories(backupDir);
				} catch (IOException e1) {
					//Path exists, do nothing
				}
			}
			if (Files.exists(file)){
				try {
					Files.copy(file,backupFile, StandardCopyOption.REPLACE_EXISTING);
					LOGGER.info("Succesfully backed up Infernalbot database" );
				} catch (IOException e) {
					LOGGER.info("Error backing up Infernal Database: " + e.getMessage());
					LOGGER.debug(e.getMessage());
					return false;
				}
			} else {
				LOGGER.info("Infernalbot database not found, check your path.");
				return false;
			}
		} else {
			LOGGER.info("Error: couldn't locate Infernalbot");
			return false;
		}
		return true;
	}
}
