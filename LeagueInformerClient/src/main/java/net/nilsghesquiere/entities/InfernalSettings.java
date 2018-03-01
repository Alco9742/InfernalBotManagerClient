package net.nilsghesquiere.entities;

import java.io.Serializable;

import lombok.Data;
import net.nilsghesquiere.enums.Region;

@Data
public class InfernalSettings implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String sets;
	private String username;
	private String password;
	private Integer groups;
	private Integer level;
	private String clientPath;
	private String currentVersion;
	private String wildcard;
	private Integer maxLevel;
	private Integer sleepTime;
	private Integer playTime;
	private Region region;
	private Integer prio;
	private Integer grSize;
	private Boolean clientUpdateSel;
	private Boolean replaceConfig;
	private Integer lolHeight;
	private Integer lolWidth;
	private Integer maxBe;
	private Boolean aktive;
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
	private String timeUntilReboot; //TODO check this out
	private Boolean serverCon;
	private Integer serverPort;
	private Boolean openChest;
	private Boolean openHexTech;
	private Boolean disChest;
	private Boolean apiClient;
	private String mySQLServer;
	private String mySQLDatabase;
	private String mySQLUser;
	private String mySQLPassword;
	private String mySQLQueueTable;
	private String mySQLAktivTable;

	public InfernalSettings(){} 
}
