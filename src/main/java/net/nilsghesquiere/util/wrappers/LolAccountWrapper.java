package net.nilsghesquiere.util.wrappers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.nilsghesquiere.entities.LolAccount;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LolAccountWrapper implements Serializable{
	private static final long serialVersionUID = 1L;
	private Map<String, List<LolAccount>> map;
	@JsonProperty("error")
	private String error;

	public LolAccountWrapper() {
		this.map = new HashMap<String, List<LolAccount>>();
		this.setError("");
	}	
	
	@JsonAnySetter 
	public void add(String key, List<LolAccount> lolAccounts) {
		map.put(key, lolAccounts);
	}

	@JsonAnyGetter
	public Map<String, List<LolAccount>> getMap() {
		return map;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
