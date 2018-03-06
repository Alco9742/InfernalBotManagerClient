package net.nilsghesquiere.entities;

import lombok.Data;
import net.nilsghesquiere.enums.Region;

import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class InfernalBotManagerClientSettings {
	private static final Logger LOGGER = LoggerFactory.getLogger("InfernalBotManagerClientSettings");
	private Long userId;
	private String infernalMap;
	private String infernalProgname;
	private Integer accountAmount;
	private String clientTag;
	private Region clientRegion;
	private String webServer;
	private String port;
	private Boolean reboot;
	private Integer rebootTime;
	private Boolean bypassDevChecks;
	
	public InfernalBotManagerClientSettings(Long userId, String infernalMap, String infernalProgname,
			Integer accountAmount, String clientTag, Region clientRegion,
			String webServer, String port, Boolean reboot, Integer rebootTime,
			Boolean bypassDevChecks) {
		super();
		this.userId = userId;
		this.infernalMap = infernalMap;
		this.infernalProgname = infernalProgname;
		this.accountAmount = accountAmount;
		this.clientTag = clientTag;
		this.clientRegion = clientRegion;
		this.webServer = webServer;
		this.port = port;
		this.reboot = reboot;
		this.rebootTime = rebootTime;
		this.bypassDevChecks = bypassDevChecks;
	}
	
	public static InfernalBotManagerClientSettings buildFromIni(Wini ini){
		boolean hasError = false;
		Long userId = ini.get("main", "userid", Long.class);
		String infernalMap = ini.get("main", "infernalmap", String.class);
		Integer numberOfAccounts = ini.get("main", "accounts", Integer.class);
		String clientTag = ini.get("main", "clienttag", String.class);
		Region clientRegion = ini.get("main", "region", Region.class);
		String webServer = ini.get("main", "webserver", String.class);
		String port = ini.get("main", "port", String.class);
		Boolean reboot = ini.get("main", "reboot", boolean.class);
		Integer rebootTime = ini.get("main", "reboottime", Integer.class);
		Boolean bypassDevChecks = ini.get("main", "bypassdev", boolean.class);
		if(userId == null){
			LOGGER.info("Error in settings.ini: value '" + userId + "' is not accepted for userid");
			hasError = true;
		}
		if(infernalMap == null){
			LOGGER.info("Error in settings.ini: value '" + infernalMap + "' is not accepted for infernalmap");
			hasError = true;
		}
		if(numberOfAccounts == null){
			LOGGER.info("Error in settings.ini: value '" + numberOfAccounts + "' is not accepted for accounts");
			hasError = true;
		} 
		if(clientTag == null){
			LOGGER.info("Error in settings.ini: value '" + clientTag + "' is not accepted for clienttag");
			hasError = true;
		}
		if(clientRegion == null){
			LOGGER.info("Error in settings.ini: value '" + clientRegion + "' is not accepted for region");
			hasError = true;
		}
		if(webServer == null){
			LOGGER.info("Error in settings.ini: value '" + webServer + "' is not accepted for webserver");
			hasError = true;
		}
		if(port == null){
			LOGGER.info("Error in settings.ini: value '" + port + "' is not accepted for port");
			hasError = true;
		}
		if(reboot == null){
			LOGGER.info("Error in settings.ini: value '" + reboot + "' is not accepted for reboot");
			hasError = true;
		} else {
			if (reboot){
				if(rebootTime == null){
					LOGGER.info("Error in settings.ini: value '" + rebootTime + "' is not accepted for reboottime");
					hasError = true;
				}
			}
		}
		if(infernalMap == null){
			LOGGER.info("Error in settings.ini: value '" + infernalMap + "' is not accepted for infernalmap");
			hasError = true;
		}
		if(bypassDevChecks == null){
			bypassDevChecks = false;
		}
		if(!hasError){
			InfernalBotManagerClientSettings settings = new InfernalBotManagerClientSettings(userId,infernalMap,"notepad.exe",numberOfAccounts,clientTag, clientRegion, webServer,port, reboot, rebootTime, bypassDevChecks);
			LOGGER.info("Succesfully loaded settings from settings.ini");
			return settings;
		} else {
			LOGGER.info("Error loading settings from settings.ini");
			return null;
		}
	}
}
