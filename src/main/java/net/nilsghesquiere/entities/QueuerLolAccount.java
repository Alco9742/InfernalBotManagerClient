package net.nilsghesquiere.entities;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Data;
import net.nilsghesquiere.enums.Lane;

@Data
public class QueuerLolAccount implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String account;
	private Integer level;
	private Integer maxLevel;
	private Integer xp;
	private Integer xpCap;
	private Integer be;
	private String champ;
	private Lane lane;
	private Boolean lpq;
	
	public QueuerLolAccount(){};
	
	public QueuerLolAccount(String champ, Lane lane, Boolean lpq) {
		super();
		this.champ = champ;
		this.lane = lane;
		this.lpq = lpq;
	}
	
	public static QueuerLolAccount buildFromResultSet(ResultSet resultSet) throws SQLException{
		QueuerLolAccount lolAccount = new QueuerLolAccount();
		lolAccount.setId(0L);
		lolAccount.setAccount(resultSet.getString("Account"));
		lolAccount.setLevel(resultSet.getInt("Level"));
		lolAccount.setMaxLevel(resultSet.getInt("MaxLevel"));
		lolAccount.setXp(resultSet.getInt("XP"));
		//TODO: xpCap --> levelchart
		lolAccount.setBe(resultSet.getInt("IP"));
		lolAccount.setChamp(resultSet.getString("Champ"));
		Integer laneInt = resultSet.getInt("Lane");
		switch (laneInt) {
			case 0: lolAccount.setLane(Lane.TOP); break;
			case 1: lolAccount.setLane(Lane.MID); break;
			case 3: lolAccount.setLane(Lane.BOT); break;
			default: lolAccount.setLane(Lane.UNKNOWN); break;
		}
		Boolean lpq = Boolean.valueOf(resultSet.getString("LPQ"));
		lolAccount.setLpq(lpq);
		return lolAccount;	
	}
}
