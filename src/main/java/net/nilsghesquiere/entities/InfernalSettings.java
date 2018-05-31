package net.nilsghesquiere.entities;

import java.io.Serializable;

import lombok.Data;

@Data
public class InfernalSettings implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String sets;
	private Integer groups;
	private String clientPath;
	private String currentVersion;
	private Boolean autoBotStart; //for this we need to write into the bot ini file, maybe add to usersettings and if yes -> infernal user and pass fields
	private Integer lolHeight; //check on live version
	private Integer lolWidth; //check on live version
	private Boolean clientHide;
	private Boolean consoleHide;
	private Boolean ramManager;
	private Integer ramMin;
	private Integer ramMax;
	private Boolean leaderHide;
	private Boolean surrender;
	private Boolean renderDisable;
	private Boolean leaderRenderDisable;
	private Boolean cpuBoost;
	private Boolean leaderCpuBoost;
	private Integer levelToBeginnerBot;
	private Integer timeSpan;
	private Boolean softEndDefault;
	private Integer softEndValue;
	private Boolean queuerAutoClose;
	private Integer queueCloseValue;
	private Boolean winReboot;
	private Boolean winShutdown;
	private Integer timeoutLogin;
	private Integer timeoutLobby;
	private Integer timeoutChamp;
	private Integer timeoutMastery;
	private Integer timeoutLoadGame;
	private Integer timeoutInGame;
	private Integer timeoutInGameFF;
	private Integer timeoutEndOfGame;
	private Boolean timeUntilCheck;
	private String timeUntilReboot; 
	private Boolean openChest;
	private Boolean openHexTech;
	private Boolean disChest;
	private Boolean enableAutoExport;
	private String exportPath;
	private String exportWildCard;
	private Boolean exportRegion;
	private Boolean exportLevel;
	private Boolean exportBE;
	
	
	public InfernalSettings(){} 
	
}
