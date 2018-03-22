package net.nilsghesquiere.entities;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import net.nilsghesquiere.enums.Region;

import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class ClientSettings {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientSettings.class);
	private Long userId;
	private String username;
	private String password;
	private String infernalMap;
	private String infernalProgramName;
	private String infernalProcessName;
	private Integer accountAmount;
	private Integer accountBuffer;
	private Boolean uploadNewAccounts;
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
	private Boolean rebootFromManager;


	
	public ClientSettings(String username, String password, String infernalMap, String infernalProgramName, String infernalProcessName,
			Integer accountAmount,Integer accountBuffer, Boolean uploadNewAccounts, String clientTag, Region clientRegion,
			String webServer, String port, Boolean reboot, Integer rebootTime,
			Boolean fetchSettings, Boolean overwriteSettings, Map<String, String> settingsOverwriteMap,
			Boolean bypassDevChecks, boolean rebootFromManager) {
		super();
		this.userId = -1L;
		this.username = username;
		this.password = password;
		this.infernalMap = infernalMap;
		this.infernalProgramName = infernalProgramName;
		this.infernalProcessName = infernalProcessName;
		this.accountAmount = accountAmount;
		this.accountBuffer = accountBuffer;
		this.uploadNewAccounts = uploadNewAccounts;
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
		this.rebootFromManager = rebootFromManager;
	}
	
	public static ClientSettings buildFromIni(Wini ini){
		boolean hasError = false;
		
		//login
		String username = ini.get("login", "username", String.class);
		String password = ini.get("login", "password", String.class);
		
		//clientifno
		String clientTag = ini.get("clientinfo", "clienttag", String.class);
		Region clientRegion = ini.get("clientinfo", "region", Region.class);
		
		//clientsettings
		String infernalMap = ini.get("clientsettings", "infernalmap", String.class);
		String infernalProgramName = ini.get("clientsettings", "infernalprogramname", String.class);
		String infernalProcessName = ini.get("clientsettings", "infernalprocessname", String.class);
		Integer numberOfAccounts = ini.get("clientsettings", "accounts", Integer.class);
		Integer accountBuffer = ini.get("clientsettings", "accountbuffer", Integer.class);
		Boolean uploadNewAccounts = ini.get("clientsettings","uploadnewaccounts", Boolean.class);
		Boolean reboot = ini.get("clientsettings", "reboot", Boolean.class);
		Integer rebootTime = ini.get("clientsettings", "reboottime", Integer.class);
		Boolean fetchSettings = ini.get("clientsettings","fetchsettings", Boolean.class);
		Boolean overwriteSettings = ini.get("clientsettings","overwritesettings", Boolean.class);
		Map<String, String> settingsOverwriteMap = new HashMap<>();
		Boolean rebootFromManager = ini.get("clientsettings", "rebootfrommanager", Boolean.class);
		
		//dev
		String webServer = ini.get("dev", "webserver", String.class);
		String port = ini.get("dev", "port", String.class);
		Boolean bypassDevChecks = ini.get("dev", "bypassdev", Boolean.class);
		
		if(username == null || username.isEmpty()){
			LOGGER.error("Bad value in settings.ini: value '" + username + "' is not accepted for username");
			hasError = true;
		}
		if(password == null || username.isEmpty()){
			LOGGER.error("Bad value in settings.ini: value '" + password + "' is not accepted for password");
			hasError = true;
		}
		if(infernalMap == null){
			LOGGER.error("Bad value in settings.ini: value '" + infernalMap + "' is not accepted for infernalmap");
			hasError = true;
		}
		if(infernalProgramName == null){
			LOGGER.error("Bad value in settings.ini: value '" + infernalProgramName + "' is not accepted for infernalprogramname");
			hasError = true;
		}
		if(infernalProcessName == null){
			LOGGER.error("Bad value in settings.ini: value '" + infernalProcessName + "' is not accepted for infernalprocessname");
			hasError = true;
		}
		if(numberOfAccounts == null){
			LOGGER.error("Bad value in settings.ini: value '" + numberOfAccounts + "' is not accepted for accounts");
			hasError = true;
		} 
		if(accountBuffer == null){
			LOGGER.error("Bad value in settings.ini: value '" + accountBuffer + "' is not accepted for accountbuffer");
			hasError = true;
		} 
		if(uploadNewAccounts == null){
			LOGGER.error("Bad value in settings.ini: value '" + uploadNewAccounts + "' is not accepted for uploadnewaccounts");
			hasError = true;
		} 
		if(clientTag == null){
			LOGGER.error("Bad value in settings.ini: value '" + clientTag + "' is not accepted for clienttag");
			hasError = true;
		}
		if(clientRegion == null){
			LOGGER.error("Bad value in settings.ini: value '" + clientRegion + "' is not accepted for region");
			hasError = true;
		}
		if(webServer == null){
			LOGGER.error("Bad value in settings.ini: value '" + webServer + "' is not accepted for webserver");
			hasError = true;
		}
		if(port == null){
			LOGGER.error("Bad value in settings.ini: value '" + port + "' is not accepted for port");
			hasError = true;
		}
		if(reboot == null){
			LOGGER.error("Bad value in settings.ini: value '" + reboot + "' is not accepted for reboot");
			hasError = true;
		} else {
			if (reboot){
				if(rebootTime == null){
					LOGGER.error("Bad value in settings.ini: value '" + rebootTime + "' is not accepted for reboottime");
					hasError = true;
				}
			}
		}
		if(infernalMap == null){
			LOGGER.error("Bad value in settings.ini: value '" + infernalMap + "' is not accepted for infernalmap");
			hasError = true;
		}
		if(bypassDevChecks == null){
			bypassDevChecks = false;
		}
		if(fetchSettings == null){
			LOGGER.error("Bad value in settings.ini: value '" + fetchSettings + "' is not accepted for fetchsettings");
			hasError = true;
		} 
		if(overwriteSettings == null){
			LOGGER.error("Bad value in settings.ini: value '" + overwriteSettings + "' is not accepted for overwritesettings");
			hasError = true;
		} 
		
		if(fetchSettings != null && overwriteSettings != null){
			if(!fetchSettings){
				if(overwriteSettings){
					LOGGER.error("Bad value in settings.ini: overwritesettings can't be true if fetchsettings is false");
					hasError = true;
				}
			} else {
				if (overwriteSettings){
					//botsettings
					Section section = ini.get("botsettings");
					for (String optionKey: section.keySet()) {
						settingsOverwriteMap.put(optionKey, section.get(optionKey));
					}
				}
			}
		}
		
		if(rebootFromManager == null){
			LOGGER.error("Bad value in settings.ini: value '" + rebootFromManager + "' is not accepted for rebootfrommanager");
			hasError = true;
		}
		
		if(!hasError){
			ClientSettings settings = new ClientSettings(username,password,infernalMap,infernalProgramName,infernalProcessName,numberOfAccounts,accountBuffer, uploadNewAccounts, clientTag, clientRegion, webServer,port, reboot, rebootTime, fetchSettings, overwriteSettings, settingsOverwriteMap, bypassDevChecks, rebootFromManager);
			LOGGER.info("Loaded settings from settings.ini");
			return settings;
		} else {
			LOGGER.error("Failure loading settings from settings.ini");
			return null;
		}
	}
}
