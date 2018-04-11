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
	private Boolean lpq;
	
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
			case "0000001": state="Performing Login"; break;
			case "0000011": state="In Lobby"; break;
			case "0000111": state="In Champ Select"; break;
			case "0001111": state="In Mastery Select"; break;
			case "0011111": state="Loading Game"; break;
			case "0111111": state="In Game"; break;
			case "1111111": state="End Of Game"; break;
			default: state="Unknown State";
		}
		queuer.setState(state);
		return queuer;
	}

	/* 
	 * INFO ABOUT STATE:
	 * 0000001 -> Performing Login
	 * 0000011 -> In Lobby
	 * 0000111 -> In Champ Select
	 * 0001111 -> In Mastery Select
	 * 0011111 -> Loading Game
	 * 0111111 -> In Game
	 * 1111111 -> End Of Game
	 */
}
