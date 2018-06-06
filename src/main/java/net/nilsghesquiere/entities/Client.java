package net.nilsghesquiere.entities;

import java.time.LocalDateTime;

import lombok.Data;
import net.nilsghesquiere.util.enums.ClientStatus;

@Data
public class Client {
	private Long id;
	private String tag;
	private String HWID;
	private User user;
	private InfernalSettings infernalSettings;
	private ClientSettings clientSettings;
	private LocalDateTime lastPing;
	private ClientStatus clientStatus;
	private Boolean error;
	
	public Client() {}
	
	public Client(String tag, User user, InfernalSettings infernalSettings,
			ClientSettings clientSettings) {
		super();
		this.tag = tag;
		this.HWID = "";
		this.user = user;
		this.infernalSettings = infernalSettings;
		this.clientSettings = clientSettings;
		this.clientStatus = ClientStatus.UNASSIGNED;
		this.lastPing = null;
		this.error = false;
	}
}
