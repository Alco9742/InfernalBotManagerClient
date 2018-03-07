package net.nilsghesquiere.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;
import net.nilsghesquiere.enums.Region;

import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class InfernalBotManagerClientSettings {
	private static final Logger LOGGER = LoggerFactory.getLogger("InfernalBotManagerClientSettings");
	private static final String INFERNAL_PROCESS_NAME ="Infernal Launcher.exe";
	private Long userId;
	private String infernalMap;
	private String infernalProg;
	private Integer accountAmount;
	private Integer accountBuffer;
	private String clientTag;
	private Region clientRegion;
	private String webServer;
	private String port;
	private Boolean reboot;
	private Integer rebootTime;
	private Boolean fetchSettings;
	private Boolean overwriteSettings;
	Map<String, String> settingsOverwriteMap;
	private Boolean bypassDevChecks;


	
	public InfernalBotManagerClientSettings(Long userId, String infernalMap, String infernalProgname,
			Integer accountAmount, String clientTag, Region clientRegion,
			String webServer, String port, Boolean reboot, Integer rebootTime,
			Boolean fetchSettings, Boolean overwriteSettings, Map<String, String> settingsOverwriteMap,
			Boolean bypassDevChecks) {
		super();
		this.userId = userId;
		this.infernalMap = infernalMap;
		this.infernalProg = infernalProgname;
		this.accountAmount = accountAmount;
		this.clientTag = clientTag;
		this.clientRegion = clientRegion;
		this.webServer = webServer;
		this.port = port;
		this.reboot = reboot;
		this.rebootTime = rebootTime;
		this.fetchSettings = fetchSettings;
		this.overwriteSettings = overwriteSettings;
		this.settingsOverwriteMap = settingsOverwriteMap;
		this.bypassDevChecks = bypassDevChecks;
	}
	
	public static InfernalBotManagerClientSettings buildFromIni(Wini ini){
		boolean hasError = false;
		Long userId = ini.get("main", "userid", Long.class);
		String infernalMap = ini.get("main", "infernalmap", String.class);
		Integer numberOfAccounts = ini.get("main", "accounts", Integer.class);
		Integer accountBuffer = ini.get("main", "accountbuffer", Integer.class);
		String clientTag = ini.get("main", "clienttag", String.class);
		Region clientRegion = ini.get("main", "region", Region.class);
		String webServer = ini.get("main", "webserver", String.class);
		String port = ini.get("main", "port", String.class);
		Boolean reboot = ini.get("main", "reboot", boolean.class);
		Integer rebootTime = ini.get("main", "reboottime", Integer.class);
		Boolean fetchSettings = ini.get("main","fetchsettings", boolean.class);
		Boolean overwriteSettings = ini.get("main","overwritesettings", boolean.class);
		Map<String, String> settingsOverwriteMap = new HashMap<>();
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
		if(accountBuffer == null){
			LOGGER.info("Error in settings.ini: value '" + accountBuffer + "' is not accepted for accountbuffer");
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
		if(fetchSettings == null){
			LOGGER.info("Error in settings.ini: value '" + fetchSettings + "' is not accepted for fetchsettings");
			hasError = true;
		} 
		if(overwriteSettings == null){
			LOGGER.info("Error in settings.ini: value '" + overwriteSettings + "' is not accepted for overwritesettings");
			hasError = true;
		} 
		
		if(fetchSettings != null && overwriteSettings != null){
			if(!fetchSettings){
				if(overwriteSettings){
					LOGGER.info("Error in settings.ini: overwritesettings can't be true if fetchsettings is false");
					hasError = true;
				}
			} else {
				if (overwriteSettings){
					Section section = ini.get("botsettings");
					for (String optionKey: section.keySet()) {
						settingsOverwriteMap.put(optionKey, section.get(optionKey));
					}
				}
			}
		}
		
		
		if(!hasError){
			InfernalBotManagerClientSettings settings = new InfernalBotManagerClientSettings(userId,infernalMap,INFERNAL_PROCESS_NAME,numberOfAccounts,clientTag, clientRegion, webServer,port, reboot, rebootTime, fetchSettings, overwriteSettings, settingsOverwriteMap, bypassDevChecks);
			LOGGER.info("Successfully loaded settings from settings.ini");
			return settings;
		} else {
			LOGGER.info("Error loading settings from settings.ini");
			return null;
		}
	}
}
