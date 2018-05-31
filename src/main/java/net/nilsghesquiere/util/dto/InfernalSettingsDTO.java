package net.nilsghesquiere.util.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Data;
import net.nilsghesquiere.util.enums.Region;

@Data
public class InfernalSettingsDTO {
	private Long id;
	private String sets;
	private String username;
	private String password;
	private Integer groups;
	private String level;
	private String clientPath;
	private String currentVersion;
	private String wildcard;
	private Integer maxLevel;
	private Integer sleepTime;
	private Integer playTime;
	private Region region;
	private Integer prio;
	private Integer grSize;
	private Boolean clientUpdateSel;
	private Boolean replaceConfig; //TODO check how this works now
	private Integer lolHeight;
	private Integer lolWidth;
	private Integer maxBe;
	private Boolean aktive;
	private Boolean clientHide;
	private Boolean consoleHide;
	private Boolean ramManager;
	private Integer ramMin;
	private Integer ramMax;
	private Boolean leaderHide;
	private Boolean surrender;
	private Boolean renderDisable;
	private Boolean leaderRenderDisable;
	private Boolean cpuBoost;
	private Boolean leaderCpuBoost;
	private Integer levelToBeginnerBot;
	private Integer timeSpan;
	private Boolean softEndDefault;
	private Integer softEndValue;
	private Boolean queuerAutoClose;
	private Integer queueCloseValue;
	private Boolean winReboot;
	private Boolean winShutdown;
	private Integer timeoutLogin;
	private Integer timeoutLobby;
	private Integer timeoutChamp;
	private Integer timeoutMastery;
	private Integer timeoutLoadGame;
	private Integer timeoutInGame;
	private Integer timeoutInGameFF;
	private Integer timeoutEndOfGame;
	private Boolean timeUntilCheck;
	private String timeUntilReboot; 
	private Boolean serverCon;
	private Integer serverPort;
	private Boolean openChest;
	private Boolean openHexTech;
	private Boolean disChest;
	private Boolean apiClient;
	private String mySQLServer;
	private String mySQLDatabase;
	private String mySQLUser;
	private String mySQLPassword;
	private String mySQLQueueTable;
	private String mySQLAktivTable;
	private Boolean enableAutoExport;
	private String exportPath;
	private String exportWildCard;
	private Boolean exportRegion;
	private Boolean exportLevel;
	private Boolean exportBE;
	
	public InfernalSettingsDTO() {
		super();
	}

	public static InfernalSettingsDTO buildFromResultSet(ResultSet resultSet) throws SQLException {
		InfernalSettingsDTO infernalSettings = new InfernalSettingsDTO();
		infernalSettings.setId(resultSet.getLong("ID"));
		infernalSettings.setSets(resultSet.getString("Sets"));
		infernalSettings.setUsername(resultSet.getString("User"));
		infernalSettings.setPassword(resultSet.getString("Password"));
		infernalSettings.setGroups(resultSet.getInt("Groups"));
		infernalSettings.setLevel(resultSet.getString("Level"));
		infernalSettings.setClientPath(resultSet.getString("ClientPath"));
		infernalSettings.setCurrentVersion(resultSet.getString("CurrentVersion"));
		infernalSettings.setWildcard(resultSet.getString("Wildcard"));
		infernalSettings.setMaxLevel(resultSet.getInt("MaxLevel"));
		infernalSettings.setSleepTime(resultSet.getInt("Sleeptime"));
		infernalSettings.setPlayTime(resultSet.getInt("Playtime"));
		infernalSettings.setRegion(Region.valueOf(resultSet.getString("Region")));
		infernalSettings.setPrio(resultSet.getInt("Prio"));
		infernalSettings.setGrSize(resultSet.getInt("GrSize"));
		infernalSettings.setClientUpdateSel(resultSet.getBoolean("ClientUpdateSel"));
		infernalSettings.setReplaceConfig(resultSet.getBoolean("replaceConfig"));
		infernalSettings.setLolHeight(resultSet.getInt("lolHeight"));
		infernalSettings.setLolWidth(resultSet.getInt("lolWidth"));
		infernalSettings.setMaxBe(resultSet.getInt("MaxIP"));
		infernalSettings.setAktive(resultSet.getBoolean("Aktive"));
		infernalSettings.setClientHide(resultSet.getBoolean("ClientHide"));
		infernalSettings.setConsoleHide(resultSet.getBoolean("ConsoleHide"));
		infernalSettings.setRamManager(resultSet.getBoolean("RamManager"));
		infernalSettings.setRamMin(resultSet.getInt("RamMin"));
		infernalSettings.setRamMax(resultSet.getInt("RamMax"));
		infernalSettings.setLeaderHide(resultSet.getBoolean("LeaderHide"));
		infernalSettings.setSurrender(resultSet.getBoolean("Surender"));
		infernalSettings.setRenderDisable(resultSet.getBoolean("RenderDisable"));
		infernalSettings.setLeaderRenderDisable(resultSet.getBoolean("LeaderRenderDisable"));
		infernalSettings.setCpuBoost(resultSet.getBoolean("CPUBoost"));
		infernalSettings.setLeaderCpuBoost(resultSet.getBoolean("LeaderCPUBoost"));
		infernalSettings.setLevelToBeginnerBot(resultSet.getInt("LevelToBeginnerBot"));
		infernalSettings.setTimeSpan(resultSet.getInt("TimeSpan"));
		infernalSettings.setSoftEndDefault(resultSet.getBoolean("SoftEndDefault"));
		infernalSettings.setSoftEndValue(resultSet.getInt("SoftEndValue"));
		infernalSettings.setQueuerAutoClose(resultSet.getBoolean("QueuerAutoClose"));
		infernalSettings.setQueueCloseValue(resultSet.getInt("QueuerCloseValue"));
		infernalSettings.setWinReboot(resultSet.getBoolean("WinReboot"));
		infernalSettings.setWinShutdown(resultSet.getBoolean("WinShutdown"));
		infernalSettings.setTimeoutLogin(resultSet.getInt("TimeoutLogin"));
		infernalSettings.setTimeoutLobby(resultSet.getInt("TimeoutLobby"));
		infernalSettings.setTimeoutChamp(resultSet.getInt("TimeoutChamp"));
		infernalSettings.setTimeoutMastery(resultSet.getInt("TimeoutMastery"));
		infernalSettings.setTimeoutLoadGame(resultSet.getInt("TimeoutLoadGame"));
		infernalSettings.setTimeoutInGame(resultSet.getInt("TimeoutIngame"));
		infernalSettings.setTimeoutInGameFF(resultSet.getInt("TimeoutInGameFF"));
		infernalSettings.setTimeoutEndOfGame(resultSet.getInt("TimeoutEndOfGame"));
		infernalSettings.setTimeUntilCheck(resultSet.getBoolean("TimeUntilCheck"));
		infernalSettings.setTimeUntilReboot(resultSet.getString("TimeUntiLReboot"));
		infernalSettings.setServerCon(resultSet.getBoolean("ServerCON"));
		infernalSettings.setServerPort(resultSet.getInt("ServerPORT"));
		infernalSettings.setOpenChest(resultSet.getBoolean("OpenChest"));
		infernalSettings.setOpenHexTech(resultSet.getBoolean("OpenHexTech"));
		infernalSettings.setDisChest(resultSet.getBoolean("DisChest"));
		infernalSettings.setApiClient(resultSet.getBoolean("APIClient"));
		infernalSettings.setMySQLServer(resultSet.getString("MySQLServer"));
		infernalSettings.setMySQLDatabase(resultSet.getString("MySQLDatabase"));
		infernalSettings.setMySQLUser(resultSet.getString("MySQLUSer"));
		infernalSettings.setMySQLPassword(resultSet.getString("MySQLPassword"));
		infernalSettings.setMySQLQueueTable(resultSet.getString("MySQLQueueTable"));
		infernalSettings.setMySQLAktivTable(resultSet.getString("MySQLAktivTable"));
		infernalSettings.setEnableAutoExport(resultSet.getBoolean("EnableAutoExport"));
		infernalSettings.setExportPath(resultSet.getString("ExportPath"));
		infernalSettings.setExportWildCard(resultSet.getString("ExportWildCard"));
		infernalSettings.setExportRegion(resultSet.getBoolean("ExportRegion"));
		infernalSettings.setExportLevel(resultSet.getBoolean("ExportLevel"));
		infernalSettings.setExportBE(resultSet.getBoolean("ExportBE"));
		return infernalSettings;
	}
}


