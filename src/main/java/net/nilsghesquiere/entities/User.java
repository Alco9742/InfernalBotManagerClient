package net.nilsghesquiere.entities;

import java.io.Serializable;

import lombok.Data;


@Data
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private UserSettings userSettings;
	//private String email; //check if maybe we should include this, ignored for now
	//private String password; //check if maybe we should include this, ignored for now
	//private boolean enabled; TODO check, this is probably redundant (won't be able to authenticate when disabled)
	//private Collection<Role> roles;
	
	public User() {
	}
	
	public User(Long id){
		this.id= id;
	}
}
