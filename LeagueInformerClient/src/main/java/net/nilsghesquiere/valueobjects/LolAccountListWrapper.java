package net.nilsghesquiere.valueobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.nilsghesquiere.entities.LolAccount;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class LolAccountListWrapper{
	private Map<String, ArrayList<LolAccount>> map;

	public LolAccountListWrapper() {
		this.map = new HashMap<String, ArrayList<LolAccount>>();
	}	
	
	@JsonAnySetter 
	public void add(String key, ArrayList<LolAccount> value) {
		map.put(key, value);
	}

	@JsonAnyGetter
	public Map<String, ArrayList<LolAccount>> getMap() {
		return map;
	}
	
}
