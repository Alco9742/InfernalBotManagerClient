package net.nilsghesquiere.jdbcclients;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.nilsghesquiere.entities.InfernalSettings;
import net.nilsghesquiere.enums.Region;

//TODO Transactions
public class InfernalSettingsJDBCClient {
	private static final Logger LOGGER = LoggerFactory.getLogger("InfernalBotAccountDatabaseClient");
	private final String DATABASE_URI;
	private static final String SELECT_DEFAULT_SQL = "SELECT * FROM Settings WHERE Sets='Default'";
	private static final String SELECT_INFERNALBOTMANAGER_SQL = "SELECT * FROM Settings WHERE Sets='InfernalBotManager'";
	private static final String INSERT_SQL = "INSERT INTO Accountlist(Account,Password,Summoner,Region,Level,MaxLevel,XP,IP,MaxIP,Prioity,Status,Playtime,Sleeptime,Active) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	//TODO^
	
	public InfernalSettingsJDBCClient(String infernalMap){
		this.DATABASE_URI = "jdbc:sqlite:" + infernalMap +"InfernalDatabase.sqlite";
	}
	
	public void connect(){
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			LOGGER.info("Successfully connected to InfernalBot database.");
		} catch (SQLException e) {
			LOGGER.info("Error conencting to InfernalBot database");
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
			LOGGER.info("Successfully received the default settings from the InfernalBot database.");
		} catch (SQLException e) {
			LOGGER.info("Error receiving the default settings from the InfernalBot database.");
			LOGGER.debug(e.getMessage());
		} 
		return infernalSettings;
	}
	
	public int insertInfernalSettings(InfernalSettings infernalSettings){
		int aantalToegevoegdeAccounts = 0;
		try(Connection connection = DriverManager.getConnection(DATABASE_URI);
			PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.setAutoCommit(false);
			//TODO 1 enkele commit
			connection.commit();
			LOGGER.info("Successfully inserted InfernalBotManager settings into the InfernalBot database.");
		} catch (SQLException e) {
			LOGGER.info("Error inserting InfernalBotManager settings into the InfernalBot database.");
			LOGGER.debug(e.getMessage());
		}
		return aantalToegevoegdeAccounts; 
	}
	
	private InfernalSettings buildInfernalSetting(ResultSet resultSet) throws SQLException {
		InfernalSettings infernalSettings = new InfernalSettings();
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

