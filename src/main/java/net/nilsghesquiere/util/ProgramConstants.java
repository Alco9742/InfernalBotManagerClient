package net.nilsghesquiere.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProgramConstants {
	public static final String CLIENT_VERSION = "0.9.8.1";
	public static final String SERVER_VERSION = "0.9.8";
	public static final String INI_NAME = "settings.ini";
	public static final String INFERNAL_PROG_NAME = "Infernal-Start.exe" ;
	public static final String LEGACY_LAUNCHER_NAME = "Infernal Launcher.exe";
	public static final String WEBSERVER = "https://infernalbotmanager.com" ;
	public static final String PORT = "";
	public static final String UPDATER_NAME = "InfernalBotManagerUpdater.exe";
	public static final Boolean enableOshiCPUCheck = true;
	public static final List<String> programsToClose = 
		    Collections.unmodifiableList(Arrays.asList("League of legends.exe"));
	public static final List<String> programsToKeepOpen = 
		    Collections.unmodifiableList(Arrays.asList("InfernalBotManager.exe"));
}
