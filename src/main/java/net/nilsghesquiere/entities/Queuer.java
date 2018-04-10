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
	//This is backwards: False is softEnd enabled
	private Boolean softEnd;
	private Integer afterGame;
	//private ?? playTime;
	//private ?? sleepTime;
	private Integer playedGames;
	private Integer winGames;
	private Integer defeatGames;
	private String state;
	private List<QueuerLolAccount> queuerLolAccounts;
	
	public Queuer() {
		this.queuerLolAccounts = new ArrayList<>();
	}
	
	public static Queuer buildFromResultSet(ResultSet resultSet) throws SQLException {
		Queuer queuer = new Queuer();
		queuer.setId(0L);
		queuer.setQueuer(resultSet.getString("Queuer"));
		Boolean softEnd = Boolean.valueOf(resultSet.getString("Softend"));
		queuer.setSoftEnd(softEnd);
		queuer.setAfterGame(resultSet.getInt("AfterGame"));
		queuer.setPlayedGames(resultSet.getInt("PlayedGames"));
		queuer.setWinGames(resultSet.getInt("WinGames"));
		queuer.setDefeatGames(resultSet.getInt("DefeatGames"));
		String stateCode = resultSet.getString("State");
		String state = "";
		switch(stateCode){
			case "000001": state="Login"; break;
			case "000011": state="In Lobby"; break;
			case "111111": state="End Of Game"; break;
			default: state="Unknown state";
		}
		queuer.setState(state);
		return queuer;
	}

	/* 
	 * INFO ABOUT STATE:
	 * 000001 -> Login
	 * 000011 -> Lobby
	 * 111111 -> End of game stats
	 */
}
