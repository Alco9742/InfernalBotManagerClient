package net.nilsghesquiere.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import net.nilsghesquiere.enums.AccountStatus;
import net.nilsghesquiere.enums.Server;

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
	private String summonername;
	private String password;
	private Server server;
	private Long level;
	private Long maxlevel;
	private AccountStatus accountStatus;
	private String assignedTo;
	private String info;
	private boolean enabled;
	
	public LolAccount() {}

	public LolAccount(String username, String password, Server server, Long maxlevel, boolean enabled) {
		super();
		this.username = username;
		this.summonername = "";
		this.password = password;
		this.level = 0L;
		this.maxlevel = maxlevel;
		this.server = server;
		this.enabled = enabled;
		this.accountStatus = AccountStatus.NEW;
		this.assignedTo = "";
		this.info = "";
	}
	
}
