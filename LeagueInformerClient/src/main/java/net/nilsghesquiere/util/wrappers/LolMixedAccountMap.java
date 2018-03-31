package net.nilsghesquiere.util.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.nilsghesquiere.entities.LolAccount;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LolMixedAccountMap{
	private Map<String, LolAccount> map;
	@JsonProperty("newAccs")
	private List<LolAccount> newAccs;

	public LolMixedAccountMap() {
		this.map = new HashMap<>();
		this.setNewAccs(new ArrayList<>());
	}	
	
	@JsonAnySetter 
	public void add(String key, LolAccount value) {
		map.put(key, value);
	}

	@JsonAnyGetter
	public Map<String, LolAccount> getMap() {
		return map;
	}

	public List<LolAccount> getNewAccs() {
		return newAccs;
	}

	public void setNewAccs(List<LolAccount> newAccs) {
		this.newAccs = newAccs;
	}
	
}
