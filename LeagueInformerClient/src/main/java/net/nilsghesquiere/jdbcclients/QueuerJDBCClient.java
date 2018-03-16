package net.nilsghesquiere.jdbcclients;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.nilsghesquiere.entities.Queuer;
import net.nilsghesquiere.entities.QueuerLolAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO Transactions
public class QueuerJDBCClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueuerJDBCClient.class);
	private final String DATABASE_URI;
	private Properties config;
	private static final String SELECT_SQL = "SELECT * FROM QeuerExtent";
	private static final String COUNT_SQL = "SELECT COUNT(*) AS rows FROM QeuerExtent";
	
	
	public QueuerJDBCClient(String infernalMap){
		this.DATABASE_URI = "jdbc:sqlite:" + infernalMap +"InfernalDatabase.sqlite";
		config = new Properties();
		config.setProperty("open_mode", "1"); // 1 == readonly
	}
	
	public boolean connect(){
		try(Connection connection = DriverManager.getConnection(DATABASE_URI,config)){
			LOGGER.info("Connected to the InfernalBot database.");
			return true;
		} catch (SQLException e) {
			LOGGER.error("Failure connecting to InfernalBot database");
			LOGGER.debug(e.getMessage());
			return false;
		} 
	}
	
	public List<Queuer> getQueuers(){
		List<Queuer> queuers = new ArrayList<>();
		try(Connection connection = DriverManager.getConnection(DATABASE_URI,config)){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(SELECT_SQL);
			while (resultSet.next()){
				Queuer queuer = Queuer.buildFromResultSet(resultSet);
				queuers.add(queuer);
			}
		} catch (SQLException e) {
			LOGGER.error("Failure receiving queuers from InfernalBot.");
			LOGGER.debug(e.getMessage());
		} 
		return queuers;
	}
	
	public List<QueuerLolAccount> getQueuerAccounts(Queuer queuer){
		List<QueuerLolAccount> queuerAccounts = new ArrayList<>();
		try(Connection connection = DriverManager.getConnection(DATABASE_URI,config)){
			Statement statement = connection.createStatement();
			String selectQueuerAccountsSQL = "SELECT * FROM " + queuer.getQueuer();
			ResultSet resultSet = statement.executeQuery(selectQueuerAccountsSQL);
			while (resultSet.next()){
				QueuerLolAccount account = QueuerLolAccount.buildFromResultSet(resultSet);
				queuerAccounts.add(account);
			}
		} catch (SQLException e) {
			LOGGER.error("Failure receiving queuers from InfernalBot.");
			LOGGER.debug(e.getMessage());
		} 
		return queuerAccounts;
	}
	
	public Integer countQueuers(){
		int queuerCount = 0;
		try(Connection connection = DriverManager.getConnection(DATABASE_URI,config)){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(COUNT_SQL);
			while (resultSet.next()){
				queuerCount = resultSet.getInt("rows");
			}
		} catch (SQLException e) {
			LOGGER.error("Failure receiving queuers from InfernalBot.");
			LOGGER.debug(e.getMessage());
		} 
		return queuerCount;
	}
	
}