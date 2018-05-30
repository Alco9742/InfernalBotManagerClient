package net.nilsghesquiere.entities;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Data;
import net.nilsghesquiere.util.enums.Lane;

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
		lolAccount.setXpCap(getXpCap(lolAccount.getLevel()));
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
	
	private static int getXpCap(Integer level){
		switch (level){
			case 1: return 144;
			case 2: return 144;
			case 3: return 192;
			case 4: return 240;
			case 5: return 336;
			case 6: return 432;
			case 7: return 528;
			case 8: return 624;
			case 9: return 720;
			case 10: return 816;
			case 11: return 912;
			case 12: return 984;
			case 13: return 1056;
			case 14: return 1128;
			case 15: return 1344;
			case 16: return 1440;
			case 17: return 1536;
			case 18: return 1680;
			case 19: return 1824;
			case 20: return 1968;
			case 21: return 2112;
			case 22: return 2208;
			case 23: return 2448;
			case 24: return 2304;
			case 25: return 2496;
			case 26: return 2496;
			case 27: return 2592;
			case 28: return 2688;
			case 29: return 3168;
			case 30: return 2688;
			case 31: return 2688;
			case 32: return 2688;
			case 33: return 2784;
			case 34: return 2784;
			case 35: return 2784;
			case 36: return 2880;
			case 37: return 2880;
			case 38: return 2880;
			case 39: return 3072;
			case 40: return 3072;
			default: return 0;
		}
	}
}
