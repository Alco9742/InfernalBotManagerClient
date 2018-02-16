package net.nilsghesquiere.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Data
public class AppUser implements Serializable{
	private static final long serialVersionUID = 1L;
	Long id;
	private String username;
	@JsonIgnore
	private String password;
	private boolean enabled;
	private Set<Role> roles;
	
	public AppUser() {}
	
	public AppUser(String username, String password, Set<Role> roles, boolean enabled) {
		super();
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.enabled = enabled;
	}
	
	public AppUser(String username, String password, Set<Role> roles) {
		super();
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.enabled =true;
	}
}
