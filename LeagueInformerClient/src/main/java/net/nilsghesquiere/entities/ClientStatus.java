package net.nilsghesquiere.entities;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ClientStatus implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private LocalDate date;
	private String status;
	
	public ClientStatus() {}
	
}
