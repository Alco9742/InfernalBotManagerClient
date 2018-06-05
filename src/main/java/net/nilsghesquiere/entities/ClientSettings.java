package net.nilsghesquiere.entities;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.nilsghesquiere.util.enums.ActionOnNoQueuers;
import net.nilsghesquiere.util.enums.Region;

@Data
public class ClientSettings {
	private Long id;
	private String name;
	private Region clientRegion;
	@Setter(AccessLevel.NONE)
	private Path infernalPath;
	private Integer queuerAmount;
	private Integer accountBufferAmount;
	private Boolean reboot;
	private Integer rebootTime;
	private Boolean fetchInfernalSettings;
	private ActionOnNoQueuers actionOnNoQueuers; 

	public ClientSettings() {
	}
	
	@SuppressWarnings("unused")
	private void setInfernalPath(String infernalpath){
		this.infernalPath = Paths.get(infernalpath);
	}
}




