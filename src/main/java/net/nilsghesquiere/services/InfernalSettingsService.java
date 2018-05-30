package net.nilsghesquiere.services;

import net.nilsghesquiere.entities.Client;
import net.nilsghesquiere.entities.InfernalSettings;
import net.nilsghesquiere.entities.IniSettings;
import net.nilsghesquiere.infernalclients.InfernalSettingsInfernalClient;
import net.nilsghesquiere.infernalclients.InfernalSettingsInfernalJDBCClient;
import net.nilsghesquiere.managerclients.InfernalSettingsManagerClient;
import net.nilsghesquiere.managerclients.InfernalSettingsManagerRESTClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfernalSettingsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalSettingsService.class);
	private final Client client;
	private final InfernalSettingsInfernalClient infernalClient;
	private final InfernalSettingsManagerClient managerClient;

	
	public InfernalSettingsService(Client client, IniSettings iniSettings){
		this.client = client;
		this.infernalClient =  new InfernalSettingsInfernalJDBCClient(client.getClientSettings().getInfernalMap());
		if(iniSettings.getPort().equals("")){
			this.managerClient = new InfernalSettingsManagerRESTClient(iniSettings.getWebServer(), iniSettings.getUsername(), iniSettings.getPassword(), iniSettings.getDebugHTTP());
		} else {
			this.managerClient = new InfernalSettingsManagerRESTClient(iniSettings.getWebServer() + ":" + iniSettings.getPort(), iniSettings.getUsername(), iniSettings.getPassword(), iniSettings.getDebugHTTP());
		}
	}
	
	public boolean checkPragmas(){
		return infernalClient.checkPragmas();
	}
	
	public boolean updateInfernalSettings(Long userid){
		InfernalSettings infernalSettingsFromREST = managerClient.getUserInfernalSettings(userid);
		InfernalSettings infernalSettingsFromJDBC = infernalClient.getDefaultInfernalSettings();
		if (infernalSettingsFromREST != null){
			if (infernalSettingsFromJDBC != null){
				InfernalSettings infernalSettingsForUpdate = prepareInfernalSettingsForJDBC(infernalSettingsFromJDBC, infernalSettingsFromREST);
				Long newId = infernalClient.updateInfernalSettings(infernalSettingsForUpdate);
				if (newId == infernalSettingsForUpdate.getId()){
					return true;
				} else{
					LOGGER.debug("ID of updated record not the same as ID of record to update");
					return false;
				}
			} else {
				InfernalSettings infernalSettingsDefaultFromJDBC = infernalClient.getDefaultInfernalSettings();
				if (infernalSettingsDefaultFromJDBC != null){
					infernalSettingsDefaultFromJDBC.setId(-1L);
					InfernalSettings infernalSettingsForInsert = prepareInfernalSettingsForJDBC(infernalSettingsDefaultFromJDBC, infernalSettingsFromREST);
					Long newId = infernalClient.insertInfernalSettings(infernalSettingsForInsert);
					if(newId != -1L){
						//LOGGER.info("Successfully created InfernalBotManager setting in the InfernalBot database");
					} else {
						//LOGGER.info("Error: Failed to create InfernalBotManager settings in the InfernalBot database");
						return false;
					}
				} else {
					return false;
				}
			}
		} else {
			LOGGER.error("Failure retrieving Infernal settings from the InfernalBotManager server");
			return false;
		}
		return true;
	}

	//todo welke velden de API velden beheren en toevoegen
	private InfernalSettings prepareInfernalSettingsForJDBC(InfernalSettings infernalSettingsFromJDBC, InfernalSettings infernalSettingsFromREST) {
		//update all settings from JDBC with settings from REST
	//	infernalSettingsFromJDBC.setSets(infernalSettingsFromREST.getSets());
	//	Not able to select sets right now so just leave it at default
	//	infernalSettingsFromJDBC.setSets("InfernalBotManager");
		infernalSettingsFromJDBC.setSets("Default");
	//	infernalSettingsFromJDBC.setUsername(infernalSettingsFromREST.getUsername());
	//	infernalSettingsFromJDBC.setPassword(infernalSettingsFromREST.getPassword());
		infernalSettingsFromJDBC.setGroups(infernalSettingsFromREST.getGroups());
	//	infernalSettingsFromJDBC.setLevel(infernalSettingsFromREST.getLevel());
		infernalSettingsFromJDBC.setClientPath(infernalSettingsFromREST.getClientPath());
	//	infernalSettingsFromJDBC.setCurrentVersion(infernalSettingsFromREST.getCurrentVersion());
		infernalSettingsFromJDBC.setWildcard(infernalSettingsFromREST.getWildcard());
		infernalSettingsFromJDBC.setMaxLevel(infernalSettingsFromREST.getMaxLevel());
		infernalSettingsFromJDBC.setSleepTime(infernalSettingsFromREST.getSleepTime());
		infernalSettingsFromJDBC.setPlayTime(infernalSettingsFromREST.getPlayTime());
		infernalSettingsFromJDBC.setPrio(infernalSettingsFromREST.getPrio());
	//	infernalSettingsFromJDBC.setGrSize(infernalSettingsFromREST.getGrSize());
	//	infernalSettingsFromJDBC.setClientUpdateSel(infernalSettingsFromREST.getClientUpdateSel());
		//autostart after login, temporarely set this to true manually 
		infernalSettingsFromJDBC.setClientUpdateSel(true);
		infernalSettingsFromJDBC.setReplaceConfig(infernalSettingsFromREST.getReplaceConfig());
		infernalSettingsFromJDBC.setLolHeight(infernalSettingsFromREST.getLolHeight());
		infernalSettingsFromJDBC.setLolWidth(infernalSettingsFromREST.getLolWidth());
		infernalSettingsFromJDBC.setMaxBe(infernalSettingsFromREST.getMaxBe());
		infernalSettingsFromJDBC.setAktive(infernalSettingsFromREST.getAktive());
		infernalSettingsFromJDBC.setClientHide(infernalSettingsFromREST.getClientHide());
		infernalSettingsFromJDBC.setConsoleHide(infernalSettingsFromREST.getConsoleHide());
		infernalSettingsFromJDBC.setRamManager(infernalSettingsFromREST.getRamManager());
		infernalSettingsFromJDBC.setRamMin(infernalSettingsFromREST.getRamMin());
		infernalSettingsFromJDBC.setRamMax(infernalSettingsFromREST.getRamMax());
		infernalSettingsFromJDBC.setLeaderHide(infernalSettingsFromREST.getLeaderHide());
		infernalSettingsFromJDBC.setSurrender(infernalSettingsFromREST.getSurrender());
		infernalSettingsFromJDBC.setRenderDisable(infernalSettingsFromREST.getRenderDisable());
		infernalSettingsFromJDBC.setLeaderRenderDisable(infernalSettingsFromREST.getLeaderRenderDisable());
		infernalSettingsFromJDBC.setCpuBoost(infernalSettingsFromREST.getCpuBoost());
		infernalSettingsFromJDBC.setLeaderCpuBoost(infernalSettingsFromREST.getLeaderCpuBoost());
		infernalSettingsFromJDBC.setLevelToBeginnerBot(infernalSettingsFromREST.getLevelToBeginnerBot());
		infernalSettingsFromJDBC.setTimeSpan(infernalSettingsFromREST.getTimeSpan());
		infernalSettingsFromJDBC.setSoftEndDefault(infernalSettingsFromREST.getSoftEndDefault());
		infernalSettingsFromJDBC.setSoftEndValue(infernalSettingsFromREST.getSoftEndValue());
		infernalSettingsFromJDBC.setQueuerAutoClose(infernalSettingsFromREST.getQueuerAutoClose());
		infernalSettingsFromJDBC.setQueueCloseValue(infernalSettingsFromREST.getQueueCloseValue());
		infernalSettingsFromJDBC.setWinReboot(infernalSettingsFromREST.getWinReboot());
		infernalSettingsFromJDBC.setWinShutdown(infernalSettingsFromREST.getWinShutdown());
		infernalSettingsFromJDBC.setTimeoutLogin(infernalSettingsFromREST.getTimeoutLogin());
		infernalSettingsFromJDBC.setTimeoutLobby(infernalSettingsFromREST.getTimeoutLobby());
		infernalSettingsFromJDBC.setTimeoutChamp(infernalSettingsFromREST.getTimeoutChamp());
		infernalSettingsFromJDBC.setTimeoutMastery(infernalSettingsFromREST.getTimeoutMastery());
		infernalSettingsFromJDBC.setTimeoutLoadGame(infernalSettingsFromREST.getTimeoutLoadGame());
		infernalSettingsFromJDBC.setTimeoutInGame(infernalSettingsFromREST.getTimeoutInGame());
		infernalSettingsFromJDBC.setTimeoutInGameFF(infernalSettingsFromREST.getTimeoutInGameFF());
		infernalSettingsFromJDBC.setTimeoutEndOfGame(infernalSettingsFromREST.getTimeoutEndOfGame());
		infernalSettingsFromJDBC.setTimeUntilCheck(infernalSettingsFromREST.getTimeUntilCheck());
		infernalSettingsFromJDBC.setTimeUntilReboot(infernalSettingsFromREST.getTimeUntilReboot());
	//	infernalSettingsFromJDBC.setServerCon(infernalSettingsFromREST.getServerCon());
	//	infernalSettingsFromJDBC.setServerPort(infernalSettingsFromREST.getServerPort());
		infernalSettingsFromJDBC.setOpenChest(infernalSettingsFromREST.getOpenChest());
		infernalSettingsFromJDBC.setOpenHexTech(infernalSettingsFromREST.getOpenHexTech());
		infernalSettingsFromJDBC.setDisChest(infernalSettingsFromREST.getDisChest());
	//	infernalSettingsFromJDBC.setApiClient(infernalSettingsFromREST.getApiClient());
	//	infernalSettingsFromJDBC.setMySQLServer(infernalSettingsFromREST.getMySQLServer());
	//	infernalSettingsFromJDBC.setMySQLDatabase(infernalSettingsFromREST.getMySQLDatabase());
	//	infernalSettingsFromJDBC.setMySQLUser(infernalSettingsFromREST.getMySQLUser());
	//	infernalSettingsFromJDBC.setMySQLPassword(infernalSettingsFromREST.getMySQLPassword());
	//	infernalSettingsFromJDBC.setMySQLQueueTable(infernalSettingsFromREST.getMySQLQueueTable());
	//	infernalSettingsFromJDBC.setMySQLAktivTable(infernalSettingsFromREST.getMySQLAktivTable());
		//new 29/03/2018
		infernalSettingsFromJDBC.setEnableAutoExport(infernalSettingsFromREST.getEnableAutoExport());
		infernalSettingsFromJDBC.setExportPath(infernalSettingsFromREST.getExportPath());
		infernalSettingsFromJDBC.setExportWildCard(infernalSettingsFromREST.getExportWildCard());
		infernalSettingsFromJDBC.setExportRegion(infernalSettingsFromREST.getExportRegion());
		infernalSettingsFromJDBC.setExportLevel(infernalSettingsFromREST.getExportLevel());
		infernalSettingsFromJDBC.setExportBE(infernalSettingsFromREST.getExportBE());
	//	Set Region from the settings in the ini
		infernalSettingsFromJDBC.setRegion(client.getClientSettings().getClientRegion());
		return infernalSettingsFromJDBC;
	}
	
	//TEST METHODS
	public void testPragmas(){
		String oldPragmas = infernalClient.getOldPragmas();
		String newPragmas = infernalClient.getNewPragmas();
		if (newPragmas.equals(oldPragmas)){
			LOGGER.info("Settings pragmas identitical");
			LOGGER.debug("Old settings pragmas: " + oldPragmas);
			LOGGER.debug("New settings pragmas: " + newPragmas);
		} else {
			LOGGER.info("Settings pragmas changed");
			LOGGER.info("Old settings pragmas: " + oldPragmas);
			LOGGER.info("New settings pragmas: " + newPragmas);
		}
	}
}
