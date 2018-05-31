package net.nilsghesquiere.entities;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserSettings implements Serializable{
	private static final long serialVersionUID = 1L;
	//Manager VARS
	Long id;
	private Long activeImportSettings;
	private Integer maxQueuers;
	
	public UserSettings(){
	} 

}
