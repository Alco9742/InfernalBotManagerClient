package net.nilsghesquiere.infernalclients;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.nilsghesquiere.entities.LolAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoLAccountInfernalJDBCClient implements LolAccountInfernalClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoLAccountInfernalJDBCClient.class);
	private final String DATABASE_URI;
	private static final String VERSION_PRAGMASTRING = "0IDINTEGER1null11AccountVARCHAR(100)0null02PasswordVARCHAR(100)0null03SummonerVARCHAR(100)0null04RegionVARCHAR(100)0null05LevelVARCHAR(100)0null06MaxLevelVARCHAR(100)0null07XPVARCHAR(100)0null08IPVARCHAR(100)0null09MaxIPVARCHAR(100)0null010PrioityVARCHAR(100)0null011StatusVARCHAR(100)0null012TotaltimeVARCHAR(100)0null013CurrenttimeVARCHAR(100)0null014PlaytimeVARCHAR(100)0null015SleeptimeVARCHAR(100)0null016ActiveVARCHAR(5)0null0";
	private static final String PRAGMA_SQL = "PRAGMA table_info(AccountList)";
	private static final String SELECT_SQL = "SELECT * FROM Accountlist";
	private static final String DELETE_SQL = "DELETE FROM Accountlist";
	private static final String INSERT_SQL = "INSERT INTO Accountlist(Account,Password,Summoner,Region,Level,MaxLevel,XP,IP,MaxIP,Prioity,Status,Playtime,Sleeptime,Active) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String COUNT_ACTIVE_SQL ="SELECT count(*) as total FROM Accountlist WHERE  Active='True'";
	private Properties readOnlyConfig;
	
	public LoLAccountInfernalJDBCClient(String infernalMap){
		this.DATABASE_URI = "jdbc:sqlite:" + infernalMap +"InfernalDatabase.sqlite";
		readOnlyConfig = new Properties();
		readOnlyConfig.setProperty("open_mode", "1"); // 1 == readonly
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
			LOGGER.error("Failure receiving account table pragmas from InfernalBot.");
			LOGGER.debug(e.getMessage());
			return result;
		} 
		return result;
	}
	
	public List<LolAccount> getAccounts(Boolean log){
		List<LolAccount> lolAccounts = new ArrayList<>();
		try(Connection connection = DriverManager.getConnection(DATABASE_URI,readOnlyConfig)){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(SELECT_SQL);
			while (resultSet.next()){
				LolAccount lolAccount = LolAccount.buildFromResultSet(resultSet);
				lolAccounts.add(lolAccount);
			}
			if (lolAccounts.size() > 0){
				if (log){
					LOGGER.info("Received " + lolAccounts.size() + " accounts from InfernalBot.");
				}
			} else {
				if (log){
					LOGGER.warn("Infernalbot accountlist is empty: not sending to the server.");
				}
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
	
	
	@Override
	public int countActiveAccounts() {
		int result = 0;
		try(Connection connection = DriverManager.getConnection(DATABASE_URI,readOnlyConfig)){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(COUNT_ACTIVE_SQL);
			while (resultSet.next()){
				result = resultSet.getInt("total");
			}
		} catch (SQLException e) {
			LOGGER.error("Failure receiving the number of active accounts from InfernalBot.");
			LOGGER.debug(e.getMessage());
			return result;
		} 
		return result;
	}
	
	//TEST Methods
	public String getOldPragmas(){
		return VERSION_PRAGMASTRING;
	}
	
	public String getNewPragmas(){
		return getPragmaString();
	}
}
