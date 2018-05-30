package net.nilsghesquiere.entities;

import java.io.Serializable;

import lombok.Data;


@Data
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	//private String email;
	//private String password;
	//private boolean enabled;
	//private Collection<Role> roles;
	
	public User() {
		super();
	//	this.enabled = false;
	}
	
	public User(Long id){
		this.id= id;
	}
}
