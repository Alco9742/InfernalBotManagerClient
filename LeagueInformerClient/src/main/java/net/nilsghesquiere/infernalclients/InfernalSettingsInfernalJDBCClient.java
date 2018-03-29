package net.nilsghesquiere.infernalclients;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.nilsghesquiere.entities.InfernalSettings;
import net.nilsghesquiere.util.ProgramUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO Transactions
public class InfernalSettingsInfernalJDBCClient implements InfernalSettingsInfernalClient{
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalSettingsInfernalJDBCClient.class);
	private final String DATABASE_URI;
	private static final String VERSION_PRAGMASTRING = "0IDINTEGER1null11SetsVARCHAR(100)0null02UserVARCHAR(100)0null03PasswordVARCHAR(100)0null04GroupsVARCHAR(4)0null05LevelVARCHAR(100)0null06ClientPathVARCHAR(250)0null07CurrentVersionVARCHAR(100)0null08WildcardVARCHAR(3)0null09MaxLevelVARCHAR(2)0null010SleeptimeVARCHAR(10)0null011PlaytimeVARCHAR(10)0null012RegionVARCHAR(100)0null013PrioVARCHAR(100)0null014GrSizeVARCHAR(1)0null015ClientUpdateSelBIT(1)0null016replaceConfigBIT(1)0null017lolHeightVARCHAR(10)0null018lolWidthVARCHAR(10)0null019MaxIPVARCHAR(100)0null020AktiveVARCHAR(5)0null021ClientHideBIT(1)0null022ConsoleHideBIT(1)0null023RamManagerBIT(1)0null024RamMinVARCHAR(10)0null025RamMaxVARCHAR(10)0null026LeaderHideBIT(1)0null027SurenderBIT(1)0null028RenderDisableBIT(1)0null029LeaderRenderDisableBIT(1)0null030CPUBoostBIT(1)0null031LeaderCPUBoostBIT(1)0null032LevelToBeginnerBotVARCHAR(10)0null033TimeSpanVARCHAR(10)0null034SoftEndDefaultBIT(1)0null035SoftEndValueVARCHAR(10)0null036QueuerAutoCloseBIT(1)0null037QueuerCloseValueVARCHAR(10)0null038WinRebootBIT(1)0null039WinShutdownBIT(1)0null040TimeoutLoginVARCHAR(10)0null041TimeoutLobbyVARCHAR(10)0null042TimeoutChampVARCHAR(10)0null043TimeoutMasteryVARCHAR(10)0null044TimeoutLoadGameVARCHAR(10)0null045TimeoutInGameVARCHAR(10)0null046TimeoutInGameFFVARCHAR(10)0null047TimeoutEndOfGameVARCHAR(10)0null048TimeUntilCheckBIT(1)0null049TimeUntilRebootVARCHAR(10)0null050ServerCONBIT(1)0null051ServerPORTVARCHAR(10)0null052OpenChestBIT(1)0null053OpenHexTechBIT(1)0null054DisChestBIT(1)0null055APIClientBIT(1)0null056EnableAutoExportBIT(1)0null057ExportPathVARCHAR(250)0null058ExportWildcardVARCHAR(2)0null059ExportRegionBIT(1)0null060ExportLevelBIT(1)0null061ExportBEBIT(1)0null062MySQLServerVARCHAR(100)0null063MySQLDatabaseVARCHAR(100)0null064MySQLUSerVARCHAR(100)0null065MySQLPasswordVARCHAR(100)0null066MySQLQueueTableVARCHAR(100)0null067MySqlAktivTableVARCHAR(100)0null0";
	private static final String PRAGMA_SQL = "PRAGMA table_info(Settings)";
	private static final String SELECT_DEFAULT_SQL = "SELECT * FROM Settings WHERE Sets='Default'";
	private static final String INSERT_SQL = "INSERT INTO Settings(Sets,User,Password,Groups,Level,ClientPath,CurrentVersion,Wildcard,MaxLevel,Sleeptime,Playtime,Region,Prio,GrSize,ClientUpdateSel,replaceConfig,lolHeight,lolWidth,MaxIP,Aktive,ClientHide,ConsoleHide,RamManager,RamMin,RamMax,LeaderHide,Surender,RenderDisable,LeaderRenderDisable,CPUBoost,LeaderCPUBoost,LevelToBeginnerBot,TimeSpan,SoftEndDefault,SoftEndValue,QueuerAutoClose,QueuerCloseValue,WinReboot,WinShutdown,TimeoutLogin,TimeoutLobby,TimeoutChamp,TimeoutMastery,TimeoutLoadGame,TimeoutInGame,TimeoutInGameFF,TimeoutEndOfGame,TimeUntilCheck,TimeUntilReboot,ServerCON,ServerPORT,OpenChest,OpenHexTech,DisChest,APIClient,MySQLServer,MySQLDatabase,MySQLUSer,MySQLPassword,MySQLQueueTable,MySqlAktivTable,EnableAutoExport,ExportPath,ExportWildcard,ExportRegion,ExportLevel,ExportBE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String UPDATE_SQL = "REPLACE INTO Settings(ID,Sets,User,Password,Groups,Level,ClientPath,CurrentVersion,Wildcard,MaxLevel,Sleeptime,Playtime,Region,Prio,GrSize,ClientUpdateSel,replaceConfig,lolHeight,lolWidth,MaxIP,Aktive,ClientHide,ConsoleHide,RamManager,RamMin,RamMax,LeaderHide,Surender,RenderDisable,LeaderRenderDisable,CPUBoost,LeaderCPUBoost,LevelToBeginnerBot,TimeSpan,SoftEndDefault,SoftEndValue,QueuerAutoClose,QueuerCloseValue,WinReboot,WinShutdown,TimeoutLogin,TimeoutLobby,TimeoutChamp,TimeoutMastery,TimeoutLoadGame,TimeoutInGame,TimeoutInGameFF,TimeoutEndOfGame,TimeUntilCheck,TimeUntilReboot,ServerCON,ServerPORT,OpenChest,OpenHexTech,DisChest,APIClient,MySQLServer,MySQLDatabase,MySQLUSer,MySQLPassword,MySQLQueueTable,MySqlAktivTable,EnableAutoExport,ExportPath,ExportWildcard,ExportRegion,ExportLevel,ExportBE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	
	public InfernalSettingsInfernalJDBCClient(String infernalMap){
		this.DATABASE_URI = "jdbc:sqlite:" + infernalMap +"InfernalDatabase.sqlite";
	}
	
	public boolean connect(){
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			LOGGER.info("Connected to InfernalBot database.");
			return true;
		} catch (SQLException e) {
			LOGGER.error("Failure connecting to InfernalBot database.");
			LOGGER.debug(e.getMessage());
			return false;
		} 
	}
	
	public boolean checkPragmas() {
		if (getPragmaString().equals(VERSION_PRAGMASTRING)){
			return true;
		} else {
			return false;
		}
	}
	
	public String getPragmaString(){
		String result= "";
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(PRAGMA_SQL);
			StringBuilder sb=new StringBuilder("");  
			while (resultSet.next()){
				sb.append(resultSet.getLong("cid"));
				sb.append(resultSet.getString("Name"));
				sb.append(resultSet.getString("Type"));
				sb.append(resultSet.getString("notnull"));
				sb.append(resultSet.getString("dflt_value"));
				sb.append(resultSet.getString("pk"));
			}
			result = sb.toString();
		} catch (SQLException e) {
			LOGGER.error("Failure receiving settings table pragmas from InfernalBot.");
			LOGGER.debug(e.getMessage());
			return result;
		} 
		return result;
	}
	
	public InfernalSettings getDefaultInfernalSettings(){
		InfernalSettings infernalSettings = null;
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(SELECT_DEFAULT_SQL);
			while (resultSet.next()){
				infernalSettings = InfernalSettings.buildFromResultSet(resultSet);
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
			//new 29/03/2018
			statement.setString(62, ProgramUtil.getCapitalizedString(infernalSettings.getEnableAutoExport()));
			statement.setString(63, infernalSettings.getExportPath());
			statement.setString(64, infernalSettings.getExportWildCard());
			statement.setString(65, ProgramUtil.getCapitalizedString(infernalSettings.getExportRegion()));
			statement.setString(66, ProgramUtil.getCapitalizedString(infernalSettings.getExportLevel()));
			statement.setString(67, ProgramUtil.getCapitalizedString(infernalSettings.getExportBE()));
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
			//new 29/03/2018
			statement.setString(63, ProgramUtil.getCapitalizedString(infernalSettings.getEnableAutoExport()));
			statement.setString(64, infernalSettings.getExportPath());
			statement.setString(65, infernalSettings.getExportWildCard());
			statement.setString(66, ProgramUtil.getCapitalizedString(infernalSettings.getExportRegion()));
			statement.setString(67, ProgramUtil.getCapitalizedString(infernalSettings.getExportLevel()));
			statement.setString(68, ProgramUtil.getCapitalizedString(infernalSettings.getExportBE()));
			statement.executeUpdate();
			LOGGER.info("Updated the Default settings in InfernalBot.");
			ResultSet rs = statement.getGeneratedKeys();
			if (rs != null && rs.next()) {
				key = rs.getLong(1);
			}
			return key;
		} catch (SQLException e) {
			LOGGER.error("Failure updating the Default settings in InfernalBot.");
			LOGGER.debug(e.getMessage());
			return key;
		}
	}
}

