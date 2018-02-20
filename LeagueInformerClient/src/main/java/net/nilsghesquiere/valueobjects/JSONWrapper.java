package net.nilsghesquiere.valueobjects;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

@Data
public class JSONWrapper {
	private Map<String, Object> map;

	public JSONWrapper() {
		this.map = new HashMap<>();
	}	
	
	@JsonAnySetter 
	public void add(String key, Object value) {
		map.put(key, value);
	}

	@JsonAnyGetter
	public Map<String,Object> getMap() {
		return map;
	}
}
