package net.nilsghesquiere.util.wrappers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.nilsghesquiere.entities.ClientData;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class ClientDataWrapper implements Serializable{
	private static final long serialVersionUID = 1L;
	private Map<String, List<ClientData>> map;
	private String error;

	public ClientDataWrapper() {
		this.map = new HashMap<String, List<ClientData>>();
		this.setError("");
	}	
	
	@JsonAnySetter 
	public void add(String key, List<ClientData> clientDatas) {
		map.put(key, clientDatas);
	}

	@JsonAnyGetter
	public Map<String, List<ClientData>> getMap() {
		return map;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
