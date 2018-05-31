package net.nilsghesquiere.util.wrappers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.nilsghesquiere.entities.Client;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class ClientSingleWrapper implements Serializable{
	private static final long serialVersionUID = 1L;
	private Map<String, Client> map;
	private String error;

	public ClientSingleWrapper() {
		this.map = new HashMap<String, Client>();
		this.setError("");
	}	
	
	@JsonAnySetter 
	public void add(String key, Client client) {
		map.put(key, client);
	}

	@JsonAnyGetter
	public Map<String, Client> getMap() {
		return map;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
