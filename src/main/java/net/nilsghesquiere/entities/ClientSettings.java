package net.nilsghesquiere.entities;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import net.nilsghesquiere.enums.Region;
import net.nilsghesquiere.util.ProgramConstants;

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
	private Integer accountAmount;
	private Integer accountBuffer;
	private Boolean uploadNewAccounts;
	private String clientTag;
	private Region clientRegion;
	private Boolean reboot;
	private Integer rebootTime;
	private Boolean fetchSettings;
	private Boolean overwriteSettings;
	Map<String, String> settingsOverwriteMap;
	private Boolean rebootFromManager;
	
	private Boolean enableDevMode;
	private Boolean bypassDevChecks;
	private Boolean testMode;
	private Boolean debugHTTP;
	private Boolean debugThreads;
	private String infernalProgramName;
	private String webServer;
	private String port;

	private Boolean readme;
	
	public ClientSettings(String username, String password, String infernalMap,
			Integer accountAmount,Integer accountBuffer, Boolean uploadNewAccounts, String clientTag, Region clientRegion,
			Boolean reboot, Integer rebootTime,
			Boolean fetchSettings, Boolean overwriteSettings, Map<String, String> settingsOverwriteMap,
			Boolean rebootFromManager,Boolean enableDevMode, Boolean bypassDevChecks, Boolean testMode, Boolean debugHTTP, Boolean debugThreads, String infernalProgramName, String webServer, String port, Boolean readme) {
		super();
		this.userId = -1L;
		this.username = username;
		this.password = password;
		this.infernalMap = infernalMap;
		this.accountAmount = accountAmount;
		this.accountBuffer = accountBuffer;
		this.uploadNewAccounts = uploadNewAccounts;
		this.clientTag = clientTag;
		this.clientRegion = clientRegion;
		this.reboot = reboot;
		this.rebootTime = rebootTime;
		this.fetchSettings = fetchSettings;
		this.overwriteSettings = overwriteSettings;
		this.settingsOverwriteMap = settingsOverwriteMap;
		this.rebootFromManager = rebootFromManager;
		this.enableDevMode = enableDevMode;
		this.bypassDevChecks = bypassDevChecks;
		this.testMode = testMode;
		this.debugThreads = debugThreads;
		this.debugHTTP = debugHTTP;
		this.infernalProgramName = infernalProgramName;
		this.webServer = webServer;
		this.port = port;
		this.readme = readme;
	}
	
	public static ClientSettings buildFromIni(Wini ini){
		boolean hasError = false;
		boolean readmeRead = false;
		//login
		String username = ini.get("login", "username", String.class);
		String password = ini.get("login", "password", String.class);
		
		//clientifno
		String clientTag = ini.get("clientinfo", "clienttag", String.class);
		Region clientRegion = ini.get("clientinfo", "region", Region.class);
		
		//clientsettings
		String infernalMap = ini.get("clientsettings", "infernalpath", String.class);
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
		Boolean enableDevMode = ini.get("dev", "devmode", Boolean.class);
		Boolean bypassDevChecks = ini.get("dev", "bypassdev", Boolean.class);
		Boolean testMode = ini.get("dev", "testmode", Boolean.class);
		Boolean debugHTTP = ini.get("dev", "debughttp", Boolean.class);
		Boolean debugThreads = ini.get("dev", "debugthreads", Boolean.class);
		String infernalProgramName = ini.get("dev", "infernalprogramname", String.class);
		String webServer = ini.get("dev", "webserver", String.class);
		String port = ini.get("dev", "port", String.class);
		
		String readme = ini.get("extra", "readme", String.class);
		
		if(username == null || username.isEmpty()){
			LOGGER.error("Bad value in settings.ini: value '" + username + "' is not accepted for username");
			hasError = true;
		}
		if(password == null || username.isEmpty()){
			LOGGER.error("Bad value in settings.ini: value '" + password + "' is not accepted for password");
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
			LOGGER.error("Bad value in settings.ini: value '" + infernalMap + "' is not accepted for infernalpath");
			hasError = true;
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
		
		if(enableDevMode != null && enableDevMode){
			if(bypassDevChecks == null){
				bypassDevChecks = false;
			}
			if(testMode == null){
				testMode = false;
			}
			if(debugHTTP == null){
				debugHTTP = false;
			}
			if(debugThreads == null){
				debugThreads = false;
			}
			if(webServer == null){
				webServer = ProgramConstants.WEBSERVER;
			}
			if(port == null){
				port = ProgramConstants.PORT;
			}
			if(infernalProgramName == null){
				infernalProgramName = ProgramConstants.INFERNAL_PROG_NAME;
			} 
			
		} else {
			enableDevMode = false;
			bypassDevChecks = false;
			testMode = false;
			debugHTTP = false;
			debugThreads = false;
			webServer = ProgramConstants.WEBSERVER;
			port = ProgramConstants.PORT;
			infernalProgramName = ProgramConstants.INFERNAL_PROG_NAME;
		}
		
		if(readme == null || !readme.equals("read")){
			LOGGER.error("Bad value in settings.ini: value '" + readme + "' is not accepted for readme");
			hasError = true;
		} else {
			readmeRead = true;
		}
		
		if(!hasError){
			ClientSettings settings = new ClientSettings(username,password,infernalMap,numberOfAccounts,accountBuffer, uploadNewAccounts, clientTag, clientRegion, reboot, rebootTime, fetchSettings, overwriteSettings, settingsOverwriteMap, rebootFromManager, enableDevMode, bypassDevChecks,testMode,debugHTTP,debugThreads, infernalProgramName, webServer, port, readmeRead);
			LOGGER.info("Loaded settings from settings.ini");
			return settings;
		} else {
			LOGGER.error("Failure loading settings from settings.ini");
			return null;
		}
	}
}
