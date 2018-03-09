package net.nilsghesquiere.util.wrappers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.nilsghesquiere.entities.GlobalVariable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class GlobalVariableSingleWrapper implements Serializable{
	private static final long serialVersionUID = 1L;
	private Map<String, GlobalVariable> map;
	private String error;

	public GlobalVariableSingleWrapper() {
		this.map = new HashMap<String, GlobalVariable>();
		this.setError("");
	}	
	
	@JsonAnySetter 
	public void add(String key, GlobalVariable globalVariable) {
		map.put(key, globalVariable);
	}

	@JsonAnyGetter
	public Map<String, GlobalVariable> getMap() {
		return map;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
