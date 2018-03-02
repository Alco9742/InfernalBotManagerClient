package net.nilsghesquiere.jdbcclients;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.nilsghesquiere.entities.LolAccount;
import net.nilsghesquiere.enums.AccountStatus;
import net.nilsghesquiere.enums.Region;

//TODO Transactions
public class LoLAccountJDBCClient {
	private final String DATABASE_URI;
	private static final String SELECT_SQL = "SELECT * FROM Accountlist";
	private static final String DELETE_SQL = "DELETE FROM Accountlist";
	private static final String INSERT_SQL = "INSERT INTO Accountlist(Account,Password,Summoner,Region,Level,MaxLevel,XP,IP,MaxIP,Prioity,Playtime,Sleeptime,Active) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	public LoLAccountJDBCClient(String infernalMap){
		this.DATABASE_URI = "jdbc:sqlite:" + infernalMap +"/InfernalDatabase.sqlite";
	}
	
	public void connect(){
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			System.out.println("Connection to SQLite has been established.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} 
	}
	
	public List<LolAccount> getAccounts(){
		List<LolAccount> lolAccounts = new ArrayList<>();
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(SELECT_SQL);
			while (resultSet.next()){
				LolAccount lolAccount = buildLolAccount(resultSet);
				lolAccounts.add(lolAccount);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} 
		return lolAccounts;
	}

	public void deleteAccounts(){
		try(Connection connection = DriverManager.getConnection(DATABASE_URI)){
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			statement.executeUpdate(DELETE_SQL);
			connection.commit();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} 
	}
	
	public int insertAccounts(List<LolAccount> lolAccounts){
		int aantalToegevoegdeAccounts = 0;
		try(Connection connection = DriverManager.getConnection(DATABASE_URI);
			PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.setAutoCommit(false);
			for (LolAccount lolAccount: lolAccounts){
				System.out.println(lolAccount);
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
				statement.setInt(11, lolAccount.getPlayTime());
				statement.setInt(12, lolAccount.getSleepTime());
				String activeString = String.valueOf(lolAccount.isActive());
				String activeStringCapitalized = activeString.substring(0, 1).toUpperCase() + activeString.substring(1);
				statement.setString(13, activeStringCapitalized);
				statement.addBatch();
			}
			int[] aantalToegevoegdeRecordsPerInsert = statement.executeBatch();
			connection.commit();
			for(int aantalToegevoegdeRecords : aantalToegevoegdeRecordsPerInsert){
				aantalToegevoegdeAccounts += aantalToegevoegdeRecords;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return aantalToegevoegdeAccounts; 
	}
	
	private LolAccount buildLolAccount(ResultSet resultSet) throws SQLException {
		LolAccount lolAccount = new LolAccount();
		lolAccount.setAccount(resultSet.getString("Account"));
		lolAccount.setPassword(resultSet.getString("Password"));
		lolAccount.setSummoner(resultSet.getString("Summoner"));
		lolAccount.setRegion(Region.valueOf(resultSet.getString("Region")));
		lolAccount.setLevel(resultSet.getInt("Level"));
		lolAccount.setMaxLevel(resultSet.getInt("MaxLevel"));
		lolAccount.setXp(resultSet.getInt("XP"));
		lolAccount.setBe(resultSet.getInt("IP"));
		lolAccount.setMaxBe(resultSet.getInt("MaxIP"));
		lolAccount.setPriority(resultSet.getInt("Prioity"));
		lolAccount.setPlayTime(resultSet.getInt("Playtime"));
		lolAccount.setSleepTime(resultSet.getInt("Sleeptime"));
		lolAccount.setActive(Boolean.valueOf(resultSet.getString("Active")));
		String statusString = resultSet.getString("Status");
		if (statusString != null && !statusString.isEmpty() && statusString.toLowerCase().contains("banned")){
			//TODO: dit verfijnen
			lolAccount.setAccountStatus(AccountStatus.ERROR);
		}
		return lolAccount;
	}
	
}

