package net.nilsghesquiere.entities;

import java.nio.file.Path;

import lombok.Data;
import net.nilsghesquiere.util.enums.ActionOnNoQueuers;
import net.nilsghesquiere.util.enums.Region;

@Data
public class ClientSettings {
	private String name;
	private Region clientRegion;
	private Path infernalMap;
	private Integer accountAmount;
	private Integer accountBufferAmount;
	private Boolean reboot;
	private Integer rebootTime;
	private Boolean fetchInfernalSettings;
	private ActionOnNoQueuers actionOnNoQueuers; 
	
	private Boolean debug;
	private String infernalProgramName;

	public ClientSettings() {}
	
	public ClientSettings(String name, Region clientRegion,
			Path infernalMap, Integer accountAmount,
			Integer accountBufferAmount, Boolean reboot, Integer rebootTime,
			Boolean fetchInfernalSettings, ActionOnNoQueuers actionOnNoQueuers,
			Boolean debug) {
		super();
		this.name = name;
		this.clientRegion = clientRegion;
		this.infernalMap = infernalMap;
		this.accountAmount = accountAmount;
		this.accountBufferAmount = accountBufferAmount;
		this.reboot = reboot;
		this.rebootTime = rebootTime;
		this.fetchInfernalSettings = fetchInfernalSettings;
		this.actionOnNoQueuers = actionOnNoQueuers;
		this.debug = debug;
		this.infernalProgramName = "Infernal-Start.exe";
	}
}




