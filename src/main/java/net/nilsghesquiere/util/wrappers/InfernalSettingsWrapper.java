package net.nilsghesquiere.util.wrappers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.nilsghesquiere.entities.InfernalSettings;

public class InfernalSettingsWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, InfernalSettings> map;
	@JsonProperty("error")
	private String error;

	public InfernalSettingsWrapper() {
		this.map = new HashMap<String, InfernalSettings>();
		this.setError("");
	}	
	
	@JsonAnySetter 
	public void add(String key, InfernalSettings infernalSettings) {
		map.put(key, infernalSettings);
	}

	@JsonAnyGetter
	public Map<String, InfernalSettings> getMap() {
		return map;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
