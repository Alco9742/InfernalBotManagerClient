package net.nilsghesquiere.util.wrappers;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class StringResponseMap {
	private Map<String, String> map;

	public StringResponseMap() {
		this.map = new HashMap<String, String>();
	}	
	
	@JsonAnySetter 
	public void add(String key, String value) {
		map.put(key, value);
	}

	@JsonAnyGetter
	public Map<String, String> getMap() {
		return map;
	}
}
