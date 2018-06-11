package net.nilsghesquiere.util.wrappers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.nilsghesquiere.entities.User;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserSingleWrapper implements Serializable{
	private static final long serialVersionUID = 1L;
	private Map<String, User> map;
	@JsonProperty("error")
	private String error;

	public UserSingleWrapper() {
		this.map = new HashMap<String, User>();
		this.setError("");
	}	
	
	@JsonAnySetter 
	public void add(String key, User user) {
		map.put(key, user);
	}

	@JsonAnyGetter
	public Map<String, User> getMap() {
		return map;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
