package net.nilsghesquiere.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ClientData implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long userId;
	private String tag;
	private List<Queuer> queuers;
	private List<ClientStatus> statusList;
	
	public ClientData() {}

	public ClientData(Long userId, String tag) {
		this.userId = userId;
		this.tag = tag;
		this.queuers = new ArrayList<>();
		this.statusList = new ArrayList<>();
	}
	
}
