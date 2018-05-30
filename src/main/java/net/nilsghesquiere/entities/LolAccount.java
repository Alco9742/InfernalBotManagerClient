package net.nilsghesquiere.entities;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Data;
import net.nilsghesquiere.util.enums.AccountStatus;
import net.nilsghesquiere.util.enums.Region;

@Data
public class LolAccount implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String account;
	private String password;
	private String summoner;
	private Region region;
	private Integer level;
	private Integer maxLevel;
	private Integer xp;
	private Integer be;
	private Integer maxBe;
	private Integer priority;
	private Integer playTime;
	private Integer sleepTime;
	private boolean active;
	private AccountStatus accountStatus;
	private String assignedTo;
	private String info;
	
	public LolAccount() {}
	
	public LolAccount(Long id, String account, String password,
			String summoner, Region region,
			Integer level, Integer maxLevel, Integer xp, Integer be,
			Integer maxBe, Integer priority, Integer playTime,
			Integer sleepTime, boolean active, AccountStatus accountStatus,
			String assignedTo, String info) {
		super();
		this.id = id;
		this.account = account;
		this.password = password;
		this.summoner = summoner;
		this.region = region;
		this.level = level;
		this.maxLevel = maxLevel;
		this.xp = xp;
		this.be = be;
		this.maxBe = maxBe;
		this.priority = priority;
		this.playTime = playTime;
		this.sleepTime = sleepTime;
		this.active = active;
		this.accountStatus = accountStatus;
		this.assignedTo = assignedTo;
		this.info = info;
	}
	
	public static LolAccount buildFromResultSet(ResultSet resultSet) throws SQLException{
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
			lolAccount.setAccountStatus(AccountStatus.BANNED);
		}
		return lolAccount;	
	}
}
