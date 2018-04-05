package net.nilsghesquiere.util.wrappers;

import java.util.HashMap;
import java.util.Map;

import net.nilsghesquiere.entities.ClientData;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class ClientDataMap{
	private Map<String, ClientData> map;

	public ClientDataMap() {
		this.map = new HashMap<>();
	}	
	
	@JsonAnySetter 
	public void add(String key, ClientData value) {
		map.put(key, value);
	}

	@JsonAnyGetter
	public Map<String, ClientData> getMap() {
		return map;
	}
	
}
