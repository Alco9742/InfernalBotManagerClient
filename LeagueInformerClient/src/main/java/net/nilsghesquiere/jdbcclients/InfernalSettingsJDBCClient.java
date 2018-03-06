package net.nilsghesquiere.jdbcclients;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.nilsghesquiere.entities.InfernalSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		//TODO opvullen met waarden
		return infernalSettings;
	}
	
}

