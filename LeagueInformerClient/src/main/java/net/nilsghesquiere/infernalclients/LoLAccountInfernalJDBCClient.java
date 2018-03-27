package net.nilsghesquiere.infernalclients;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.nilsghesquiere.entities.LolAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO Transactions
public class LoLAccountInfernalJDBCClient implements LolAccountInfernalClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoLAccountInfernalJDBCClient.class);
	private final String DATABASE_URI;
	private static final String SELECT_SQL = "SELECT * FROM Accountlist";
	private static final String DELETE_SQL = "DELETE FROM Accountlist";
	private static final String INSERT_SQL = "INSERT INTO Accountlist(Account,Password,Summoner,Region,Level,MaxLevel,XP,IP,MaxIP,Prioity,Status,Playtime,Sleeptime,Active) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	
	public LoLAccountInfernalJDBCClient(String infernalMap){
		this.DATABASE_URI = "jdbc:sqlite:" + infernalMap +"InfernalDatabase.sqlite";
	}
	
	public boolean connect(){
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			LOGGER.info("Connected to the InfernalBot database.");
			return true;
		} catch (SQLException e) {
			LOGGER.error("Failure connecting to InfernalBot database");
			LOGGER.debug(e.getMessage());
			return false;
		} 
	}
	
	public List<LolAccount> getAccounts(){
		List<LolAccount> lolAccounts = new ArrayList<>();
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(SELECT_SQL);
			while (resultSet.next()){
				LolAccount lolAccount = LolAccount.buildFromResultSet(resultSet);
				lolAccounts.add(lolAccount);
			}
			if (lolAccounts.size() > 0){
				LOGGER.info("Received " + lolAccounts.size() + " accounts from InfernalBot.");
			} else {
				LOGGER.warn("Infernalbot accountlist is empty: not sending to the server.");
			}
		} catch (SQLException e) {
			LOGGER.error("Failure receiving accounts from InfernalBot.");
			LOGGER.debug(e.getMessage());
		} 
		return lolAccounts;
	}

	public void deleteAllAccounts(){
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			statement.executeUpdate(DELETE_SQL);
			connection.commit();
			LOGGER.info("Deleted all accounts from InfernalBot.");
		} catch (SQLException e) {
			LOGGER.error("Failure deleting accounts from InfernalBot .");
			LOGGER.debug(e.getMessage());
		} 
	}
	
	public int insertAccounts(List<LolAccount> lolAccounts, Boolean buffer){
		int aantalToegevoegdeAccounts = 0;
		try(Connection connection = DriverManager.getConnection(DATABASE_URI);
			PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.setAutoCommit(false);
			for (LolAccount lolAccount: lolAccounts){
				statement.setString(1, lolAccount.getAccount());
				statement.setString(2, lolAccount.getPassword());
				statement.setString(3, lolAccount.getSummoner());
				statement.setString(4, lolAccount.getRegion().name());
				statement.setInt(5, lolAccount.getLevel());
				statement.setInt(6, lolAccount.getMaxLevel());
				statement.setInt(7, lolAccount.getXp());
				statement.setInt(8, lolAccount.getBe());
				statement.setInt(9, lolAccount.getMaxBe());
				statement.setInt(10, lolAccount.getPriority());
				statement.setString(11, ""); // doesn't work when null
				statement.setInt(12, lolAccount.getPlayTime());
				statement.setInt(13, lolAccount.getSleepTime());
				String activeString = String.valueOf(lolAccount.isActive());
				String activeStringCapitalized = activeString.substring(0, 1).toUpperCase() + activeString.substring(1);
				statement.setString(14, activeStringCapitalized);
				statement.addBatch();
			}
			int[] aantalToegevoegdeRecordsPerInsert = statement.executeBatch();
			connection.commit();
			for(int aantalToegevoegdeRecords : aantalToegevoegdeRecordsPerInsert){
				aantalToegevoegdeAccounts += aantalToegevoegdeRecords;
			}
			if(!buffer){
				LOGGER.info("Inserted " + aantalToegevoegdeAccounts + " accounts into InfernalBot.");
			} else {
				LOGGER.info("Inserted " + aantalToegevoegdeAccounts + " bufferaccounts into InfernalBot.");	
			}
		} catch (SQLException e) {
			LOGGER.error("Failure inserting accounts into InfernalBot.");
			LOGGER.debug(e.getMessage());
		}
		return aantalToegevoegdeAccounts; 
	}
}
