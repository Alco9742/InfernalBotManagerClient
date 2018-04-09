package net.nilsghesquiere.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ClientData implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String tag;
	private List<Queuer> queuers;
	private LocalDateTime date;
	private String status;
	private String ramInfo;
	private String cpuInfo;
	
	public ClientData() {}

	public ClientData(String tag) {
		this.tag = tag;
		this.queuers = new ArrayList<>();
		this.date = LocalDateTime.now();
		this.status= "";
	}

	public ClientData(String tag,String status) {
		this.tag = tag;
		this.queuers = new ArrayList<>();
		this.date = LocalDateTime.now();
		this.status=status;
	}
}
