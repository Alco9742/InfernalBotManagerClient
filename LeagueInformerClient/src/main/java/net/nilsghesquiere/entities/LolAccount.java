package net.nilsghesquiere.entities;

import java.io.Serializable;

import lombok.Data;
import net.nilsghesquiere.enums.AccountStatus;
import net.nilsghesquiere.enums.Region;

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
	
	
}
