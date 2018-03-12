package net.nilsghesquiere.entities;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Queuer implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String queuer;
	private Boolean softEnd;
	private Integer afterGame;
	//private ?? playTime;
	//private ?? sleepTime;
	private Integer playedGames;
	private Integer winGames;
	private Integer defeatGames;
	//private ?? state;
	private List<QueuerLolAccount> queuerAccounts;
	
	public Queuer() {
		this.queuerAccounts = new ArrayList<>();
	}
	
	public static Queuer buildFromResultSet(ResultSet resultSet) throws SQLException {
		Queuer queuer = new Queuer();
		queuer.setId(0L);
		queuer.setQueuer(resultSet.getString("Queuer"));
		queuer.setSoftEnd(Boolean.valueOf(resultSet.getString("Softend")));
		queuer.setAfterGame(resultSet.getInt("AfterGame"));
		queuer.setPlayedGames(resultSet.getInt("PlayedGames"));
		queuer.setWinGames(resultSet.getInt("WinGames"));
		queuer.setDefeatGames(resultSet.getInt("DefeatGames"));
		return queuer;
	}
	
}
