package net.nilsghesquiere.jdbcclients;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.nilsghesquiere.entities.InfernalSettings;
import net.nilsghesquiere.enums.Region;
import net.nilsghesquiere.util.ProgramUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO Transactions
public class InfernalSettingsJDBCClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalSettingsJDBCClient.class);
	private final String DATABASE_URI;
	private static final String SELECT_DEFAULT_SQL = "SELECT * FROM Settings WHERE Sets='Default'";
	private static final String SELECT_INFERNALBOTMANAGER_SQL = "SELECT * FROM Settings WHERE Sets='InfernalBotManager'";
	private static final String INSERT_SQL = "INSERT INTO Settings(Sets,User,Password,Groups,Level,ClientPath,CurrentVersion,Wildcard,MaxLevel,Sleeptime,Playtime,Region,Prio,GrSize,ClientUpdateSel,replaceConfig,lolHeight,lolWidth,MaxIP,Aktive,ClientHide,ConsoleHide,RamManager,RamMin,RamMax,LeaderHide,Surender,RenderDisable,LeaderRenderDisable,CPUBoost,LeaderCPUBoost,LevelToBeginnerBot,TimeSpan,SoftEndDefault,SoftEndValue,QueuerAutoClose,QueuerCloseValue,WinReboot,WinShutdown,TimeoutLogin,TimeoutLobby,TimeoutChamp,TimeoutMastery,TimeoutLoadGame,TimeoutInGame,TimeoutInGameFF,TimeoutEndOfGame,TimeUntilCheck,TimeUntilReboot,ServerCON,ServerPORT,OpenChest,OpenHexTech,DisChest,APIClient,MySQLServer,MySQLDatabase,MySQLUSer,MySQLPassword,MySQLQueueTable,MySqlAktivTable) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String UPDATE_SQL = "REPLACE INTO Settings(ID,Sets,User,Password,Groups,Level,ClientPath,CurrentVersion,Wildcard,MaxLevel,Sleeptime,Playtime,Region,Prio,GrSize,ClientUpdateSel,replaceConfig,lolHeight,lolWidth,MaxIP,Aktive,ClientHide,ConsoleHide,RamManager,RamMin,RamMax,LeaderHide,Surender,RenderDisable,LeaderRenderDisable,CPUBoost,LeaderCPUBoost,LevelToBeginnerBot,TimeSpan,SoftEndDefault,SoftEndValue,QueuerAutoClose,QueuerCloseValue,WinReboot,WinShutdown,TimeoutLogin,TimeoutLobby,TimeoutChamp,TimeoutMastery,TimeoutLoadGame,TimeoutInGame,TimeoutInGameFF,TimeoutEndOfGame,TimeUntilCheck,TimeUntilReboot,ServerCON,ServerPORT,OpenChest,OpenHexTech,DisChest,APIClient,MySQLServer,MySQLDatabase,MySQLUSer,MySQLPassword,MySQLQueueTable,MySqlAktivTable) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	
	public InfernalSettingsJDBCClient(String infernalMap){
		this.DATABASE_URI = "jdbc:sqlite:" + infernalMap +"InfernalDatabase.sqlite";
	}
	
	public void connect(){
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			LOGGER.info("Connected to InfernalBot database.");
		} catch (SQLException e) {
			LOGGER.error("Failure connecting to InfernalBot database.");
			LOGGER.debug(e.getMessage());
		} 
	}
	
	public InfernalSettings getDefaultInfernalSettings(){
		InfernalSettings infernalSettings = null;
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(SELECT_DEFAULT_SQL);
			while (resultSet.next()){
				infernalSettings = buildInfernalSetting(resultSet);
			}
			if (infernalSettings != null){
				LOGGER.info("Received the Default settings from InfernalBot.");
			} else {
				LOGGER.error("Could not retrieve default settings from InfernalBot.");
			}
		} catch (SQLException e) {
			LOGGER.error("Failure receiving the default settings from InfernalBot.");
			LOGGER.debug(e.getMessage());
		} 
		return infernalSettings;
	}
	
	public InfernalSettings getInfernalBotManagerInfernalSettings(){
		InfernalSettings infernalSettings = null;
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(SELECT_INFERNALBOTMANAGER_SQL);
			while (resultSet.next()){
				infernalSettings = buildInfernalSetting(resultSet);
			}
			if (infernalSettings != null){
				LOGGER.info("Received InfernalBotManager settings from InfernalBot.");
			} else {
				//LOGGER.warn("InfernalBotManager settings not found in the InfernalBot database, creating new set.");
			}
		} catch (SQLException e) {
			LOGGER.error("Failure receiving the InfernalBotManager settings from InfernalBot.");
			LOGGER.debug(e.getMessage());
		} 
		return infernalSettings;
	}
	
	public Long insertInfernalSettings(InfernalSettings infernalSettings){
		Long key = -1L;
		try(Connection connection = DriverManager.getConnection(DATABASE_URI);
			PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement.setString(1, infernalSettings.getSets());
			statement.setString(2, infernalSettings.getUsername());
			statement.setString(3, infernalSettings.getPassword());
			statement.setInt(4, infernalSettings.getGroups());
			statement.setString(5, infernalSettings.getLevel());
			statement.setString(6, infernalSettings.getClientPath());
			statement.setString(7, infernalSettings.getCurrentVersion());
			statement.setString(8, infernalSettings.getWildcard());
			statement.setInt(9, infernalSettings.getMaxLevel());
			statement.setInt(10, infernalSettings.getSleepTime());
			statement.setInt(11, infernalSettings.getPlayTime());
			statement.setString(12, infernalSettings.getRegion().name());
			statement.setInt(13, infernalSettings.getPrio());
			statement.setInt(14, infernalSettings.getGrSize());
			statement.setString(15, ProgramUtil.getCapitalizedString(infernalSettings.getClientUpdateSel()));
			statement.setString(16, ProgramUtil.getCapitalizedString(infernalSettings.getReplaceConfig()));
			statement.setInt(17, infernalSettings.getLolHeight());
			statement.setInt(18, infernalSettings.getLolWidth());
			statement.setInt(19, infernalSettings.getMaxBe());
			statement.setString(20, ProgramUtil.getCapitalizedString(infernalSettings.getAktive()));
			statement.setString(21, ProgramUtil.getCapitalizedString(infernalSettings.getClientHide()));
			statement.setString(22, ProgramUtil.getCapitalizedString(infernalSettings.getConsoleHide()));
			statement.setString(23, ProgramUtil.getCapitalizedString(infernalSettings.getRamManager()));
			statement.setInt(27, infernalSettings.getRamMin());
			statement.setInt(25, infernalSettings.getRamMax());
			statement.setString(26, ProgramUtil.getCapitalizedString(infernalSettings.getLeaderHide()));
			statement.setString(27, ProgramUtil.getCapitalizedString(infernalSettings.getSurrender()));
			statement.setString(28, ProgramUtil.getCapitalizedString(infernalSettings.getRenderDisable()));
			statement.setString(29, ProgramUtil.getCapitalizedString(infernalSettings.getLeaderRenderDisable()));
			statement.setString(30, ProgramUtil.getCapitalizedString(infernalSettings.getCpuBoost()));
			statement.setString(31, ProgramUtil.getCapitalizedString(infernalSettings.getLeaderCpuBoost()));
			statement.setInt(32, infernalSettings.getLevelToBeginnerBot());
			statement.setInt(33, infernalSettings.getTimeSpan());
			statement.setString(34, ProgramUtil.getCapitalizedString(infernalSettings.getSoftEndDefault()));
			statement.setInt(35, infernalSettings.getSoftEndValue());
			statement.setString(36, ProgramUtil.getCapitalizedString(infernalSettings.getQueuerAutoClose()));
			statement.setInt(37, infernalSettings.getQueueCloseValue());
			statement.setString(38, ProgramUtil.getCapitalizedString(infernalSettings.getWinReboot()));
			statement.setString(39, ProgramUtil.getCapitalizedString(infernalSettings.getWinShutdown()));
			statement.setInt(40, infernalSettings.getTimeoutLogin());
			statement.setInt(41, infernalSettings.getTimeoutLobby());
			statement.setInt(42, infernalSettings.getTimeoutChamp());
			statement.setInt(43, infernalSettings.getTimeoutMastery());
			statement.setInt(44, infernalSettings.getTimeoutLoadGame());
			statement.setInt(45, infernalSettings.getTimeoutInGame());
			statement.setInt(46, infernalSettings.getTimeoutInGameFF());
			statement.setInt(47, infernalSettings.getTimeoutEndOfGame());
			statement.setString(48, ProgramUtil.getCapitalizedString(infernalSettings.getTimeUntilCheck()));
			statement.setString(49, infernalSettings.getTimeUntilReboot());
			statement.setString(50, ProgramUtil.getCapitalizedString(infernalSettings.getServerCon()));
			statement.setInt(51, infernalSettings.getServerPort());
			statement.setString(52, ProgramUtil.getCapitalizedString(infernalSettings.getOpenChest()));
			statement.setString(53, ProgramUtil.getCapitalizedString(infernalSettings.getOpenHexTech()));
			statement.setString(54, ProgramUtil.getCapitalizedString(infernalSettings.getDisChest()));
			statement.setString(55, ProgramUtil.getCapitalizedString(infernalSettings.getApiClient()));
			statement.setString(56, infernalSettings.getMySQLServer());
			statement.setString(57, infernalSettings.getMySQLDatabase());
			statement.setString(58, infernalSettings.getMySQLUser());
			statement.setString(59, infernalSettings.getMySQLPassword());
			statement.setString(60, infernalSettings.getMySQLQueueTable());
			statement.setString(61, infernalSettings.getMySQLAktivTable());
			statement.executeUpdate();
			LOGGER.info("Inserted InfernalBotManager settings into InfernalBot.");
			ResultSet rs = statement.getGeneratedKeys();
			if (rs != null && rs.next()) {
				key = rs.getLong(1);
			}
			return key;
		} catch (SQLException e) {
			LOGGER.info("Failure inserting InfernalBotManager settings into InfernalBot.");
			LOGGER.debug(e.getMessage());
			return key;
		}
	}
	
	public Long updateInfernalSettings(InfernalSettings infernalSettings){
		Long key = infernalSettings.getId();
		try(Connection connection = DriverManager.getConnection(DATABASE_URI);
			PreparedStatement statement = connection.prepareStatement(UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)){
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement.setLong(1, infernalSettings.getId());
			statement.setString(2,infernalSettings.getSets());
			statement.setString(3,infernalSettings.getUsername());
			statement.setString(4,infernalSettings.getPassword());
			statement.setInt(5,infernalSettings.getGroups());
			statement.setString(6,infernalSettings.getLevel());
			statement.setString(7,infernalSettings.getClientPath());
			statement.setString(8,infernalSettings.getCurrentVersion());
			statement.setString(9,infernalSettings.getWildcard());
			statement.setInt(10,infernalSettings.getMaxLevel());
			statement.setInt(11,infernalSettings.getSleepTime());
			statement.setInt(12,infernalSettings.getPlayTime());
			statement.setString(13,infernalSettings.getRegion().name());
			statement.setInt(14,infernalSettings.getPrio());
			statement.setInt(15,infernalSettings.getGrSize());
			statement.setString(16,ProgramUtil.getCapitalizedString(infernalSettings.getClientUpdateSel()));
			statement.setString(17,ProgramUtil.getCapitalizedString(infernalSettings.getReplaceConfig()));
			statement.setInt(18,infernalSettings.getLolHeight());
			statement.setInt(19,infernalSettings.getLolWidth());
			statement.setInt(20,infernalSettings.getMaxBe());
			statement.setString(21,ProgramUtil.getCapitalizedString(infernalSettings.getAktive()));
			statement.setString(22,ProgramUtil.getCapitalizedString(infernalSettings.getClientHide()));
			statement.setString(23,ProgramUtil.getCapitalizedString(infernalSettings.getConsoleHide()));
			statement.setString(24,ProgramUtil.getCapitalizedString(infernalSettings.getRamManager()));
			statement.setInt(25,infernalSettings.getRamMin());
			statement.setInt(26,infernalSettings.getRamMax());
			statement.setString(27,ProgramUtil.getCapitalizedString(infernalSettings.getLeaderHide()));
			statement.setString(28,ProgramUtil.getCapitalizedString(infernalSettings.getSurrender()));
			statement.setString(29,ProgramUtil.getCapitalizedString(infernalSettings.getRenderDisable()));
			statement.setString(30,ProgramUtil.getCapitalizedString(infernalSettings.getLeaderRenderDisable()));
			statement.setString(31,ProgramUtil.getCapitalizedString(infernalSettings.getCpuBoost()));
			statement.setString(32,ProgramUtil.getCapitalizedString(infernalSettings.getLeaderCpuBoost()));
			statement.setInt(33,infernalSettings.getLevelToBeginnerBot());
			statement.setInt(34,infernalSettings.getTimeSpan());
			statement.setString(35,ProgramUtil.getCapitalizedString(infernalSettings.getSoftEndDefault()));
			statement.setInt(36,infernalSettings.getSoftEndValue());
			statement.setString(37,ProgramUtil.getCapitalizedString(infernalSettings.getQueuerAutoClose()));
			statement.setInt(38,infernalSettings.getQueueCloseValue());
			statement.setString(39,ProgramUtil.getCapitalizedString(infernalSettings.getWinReboot()));
			statement.setString(40,ProgramUtil.getCapitalizedString(infernalSettings.getWinShutdown()));
			statement.setInt(41,infernalSettings.getTimeoutLogin());
			statement.setInt(42,infernalSettings.getTimeoutLobby());
			statement.setInt(43,infernalSettings.getTimeoutChamp());
			statement.setInt(44,infernalSettings.getTimeoutMastery());
			statement.setInt(45,infernalSettings.getTimeoutLoadGame());
			statement.setInt(46,infernalSettings.getTimeoutInGame());
			statement.setInt(47,infernalSettings.getTimeoutInGameFF());
			statement.setInt(48,infernalSettings.getTimeoutEndOfGame());
			statement.setString(49,ProgramUtil.getCapitalizedString(infernalSettings.getTimeUntilCheck()));
			statement.setString(50,infernalSettings.getTimeUntilReboot());
			statement.setString(51,ProgramUtil.getCapitalizedString(infernalSettings.getServerCon()));
			statement.setInt(52,infernalSettings.getServerPort());
			statement.setString(53,ProgramUtil.getCapitalizedString(infernalSettings.getOpenChest()));
			statement.setString(54,ProgramUtil.getCapitalizedString(infernalSettings.getOpenHexTech()));
			statement.setString(55,ProgramUtil.getCapitalizedString(infernalSettings.getDisChest()));
			statement.setString(56,ProgramUtil.getCapitalizedString(infernalSettings.getApiClient()));
			statement.setString(57,infernalSettings.getMySQLServer());
			statement.setString(58,infernalSettings.getMySQLDatabase());
			statement.setString(59,infernalSettings.getMySQLUser());
			statement.setString(60,infernalSettings.getMySQLPassword());
			statement.setString(61,infernalSettings.getMySQLQueueTable());
			statement.setString(62,infernalSettings.getMySQLAktivTable());
			statement.executeUpdate();
			LOGGER.info("Updated the Default settings in InfernalBot.");
			//LOGGER.info("Successfully updated InfernalBotManager settings in the InfernalBot database.");
			ResultSet rs = statement.getGeneratedKeys();
			if (rs != null && rs.next()) {
				key = rs.getLong(1);
			}
			return key;
		} catch (SQLException e) {
			LOGGER.error("Failure updating the Default settings in InfernalBot.");
			//LOGGER.info("Error updating InfernalBotManager settings in the InfernalBot database.");
			LOGGER.debug(e.getMessage());
			return key;
		}
	}
	
	private InfernalSettings buildInfernalSetting(ResultSet resultSet) throws SQLException {
		InfernalSettings infernalSettings = new InfernalSettings();
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
		return infernalSettings;
	}
	
}

