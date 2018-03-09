package net.nilsghesquiere.entities;

import java.io.Serializable;

import lombok.Data;
@Data
public class GlobalVariable implements Serializable{
	private static final long serialVersionUID = 1L;
	Long id;
	private String name;
	private String value;
	
	public GlobalVariable() {}
	
}
