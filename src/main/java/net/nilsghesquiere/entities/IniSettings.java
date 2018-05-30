package net.nilsghesquiere.entities;

import lombok.Data;
import net.nilsghesquiere.util.ProgramConstants;

import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class IniSettings {
	private static final Logger LOGGER = LoggerFactory.getLogger(IniSettings.class);
	private String username;
	private String password;
	private String clientTag;
	private Boolean devmode;
	private String webServer;
	private String port;
	private Boolean debugHTTP;
	private Boolean debugThreads;
	private Boolean testmode;

	public IniSettings(String username, String password, String clientTag, Boolean devmode, String webServer, String port, Boolean debugHTTP, Boolean debugThreads, Boolean testmode) {
		super();
		this.username = username;
		this.password = password;
		this.clientTag = clientTag;
		this.devmode = devmode;
		this.webServer = webServer;
		this.port = port;
		this.debugHTTP = debugHTTP;
		this.debugThreads = debugThreads;
		this.testmode = testmode;
	}
	
	public static IniSettings buildFromIni(Wini ini){
		boolean hasError = false;
		//login
		String username = ini.get("login", "username", String.class);
		String password = ini.get("login", "password", String.class);
		
		//client
		String clientTag = ini.get("client", "tag", String.class);
		
		//dev
		Boolean devmode = ini.get("dev", "devmode", Boolean.class);
		String webServer = ini.get("dev", "webserver", String.class);
		String port = ini.get("dev", "port", String.class);
		Boolean debugHTTP = ini.get("dev", "debugHTTP", Boolean.class);
		Boolean debugThreads = ini.get("dev", "debugThreads", Boolean.class);
		Boolean testmode = ini.get("dev", "testmode", Boolean.class);
		
		if(username == null || username.isEmpty()){
			LOGGER.error("Bad value in settings.ini: value '" + username + "' is not accepted for username");
			hasError = true;
		}
		if(password == null || password.isEmpty()){
			LOGGER.error("Bad value in settings.ini: value '" + password + "' is not accepted for password");
			hasError = true;
		}
		
		if(clientTag == null || clientTag.isEmpty()){
			LOGGER.error("Bad value in settings.ini: value '" + clientTag + "' is not accepted for tag");
			hasError = true;
		}

		if(devmode != null && devmode){
			devmode = true;
			if(webServer == null){
				webServer = ProgramConstants.WEBSERVER;
			}
			if(port == null){
				port = ProgramConstants.PORT;
			}
			if(debugHTTP == null){
				debugHTTP = false;
			}
			if(debugThreads == null){
				debugThreads = false;
			}
			if(testmode == null){
				testmode = false;
			}
		} else {
			devmode = false;
			webServer = ProgramConstants.WEBSERVER;
			port = ProgramConstants.PORT;
			debugHTTP = false;
			debugThreads = false;
			testmode = false;
		}

		if(!hasError){
			IniSettings settings = new IniSettings(username,password,clientTag, devmode, webServer,port, debugHTTP, debugThreads, testmode);
			LOGGER.info("Loaded settings from settings.ini");
			return settings;
		} else {
			LOGGER.error("Failure loading settings from settings.ini");
			return null;
		}
	}
}
