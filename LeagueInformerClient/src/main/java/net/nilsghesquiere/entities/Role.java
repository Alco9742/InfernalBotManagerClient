package net.nilsghesquiere.entities;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Role implements Serializable{
	private static final long serialVersionUID = 1L;
	private  Long id;
	private String name;
	
	public Role(){}

	public Role(String name) {
		this.name = name;
	}

}
