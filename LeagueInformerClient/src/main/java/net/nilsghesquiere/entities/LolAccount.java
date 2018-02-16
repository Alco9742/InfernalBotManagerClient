package net.nilsghesquiere.entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;

@Data
public class LolAccount implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String username;
	private String password;
	private String region;
	private boolean enabled;
	
	public LolAccount() {}

	public LolAccount(String username, String password, String region, boolean enabled) {
		super();
		this.username = username;
		this.password = password;
		this.region = region;
		this.enabled = enabled;
	}
}
