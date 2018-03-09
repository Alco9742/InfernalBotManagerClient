package net.nilsghesquiere.util.wrappers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.nilsghesquiere.entities.GlobalVariable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class GlobalVariableWrapper implements Serializable{
	private static final long serialVersionUID = 1L;
	private Map<String, List<GlobalVariable>> map;
	private String error;

	public GlobalVariableWrapper() {
		this.map = new HashMap<String, List<GlobalVariable>>();
		this.setError("");
	}	
	
	@JsonAnySetter 
	public void add(String key, List<GlobalVariable> globalVariables) {
		map.put(key, globalVariables);
	}

	@JsonAnyGetter
	public Map<String, List<GlobalVariable>> getMap() {
		return map;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
