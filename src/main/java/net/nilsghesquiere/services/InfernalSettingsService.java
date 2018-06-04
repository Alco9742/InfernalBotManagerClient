package net.nilsghesquiere.services;

import net.nilsghesquiere.entities.Client;
import net.nilsghesquiere.entities.InfernalSettings;
import net.nilsghesquiere.infernalclients.InfernalSettingsInfernalClient;
import net.nilsghesquiere.infernalclients.InfernalSettingsInfernalJDBCClient;
import net.nilsghesquiere.util.dto.InfernalSettingsDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfernalSettingsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalSettingsService.class);
	private final Client client;
	private final InfernalSettingsInfernalClient infernalClient;

	
	public InfernalSettingsService(Client client){
		this.client = client;
		this.infernalClient =  new InfernalSettingsInfernalJDBCClient(client.getClientSettings().getInfernalPath());
	}
	
	public boolean checkPragmas(){
		return infernalClient.checkPragmas();
	}
	
	public boolean updateInfernalSettings(Long userid){
		InfernalSettings infernalSettingsFromREST = client.getInfernalSettings();
		InfernalSettingsDTO infernalSettingsFromJDBC = infernalClient.getDefaultInfernalSettings();
		if (infernalSettingsFromREST != null){
			if (infernalSettingsFromJDBC != null){
				InfernalSettingsDTO infernalSettingsForUpdate = prepareInfernalSettingsForJDBC(infernalSettingsFromJDBC, infernalSettingsFromREST);
				return infernalClient.updateInfernalSettings(infernalSettingsForUpdate);
			} else {
				return false;
			}
		} else {
			LOGGER.error("Failure retrieving Infernal settings from the InfernalBotManager server");
			return false;
		}
	}

	private InfernalSettingsDTO prepareInfernalSettingsForJDBC(InfernalSettingsDTO infernalSettingsFromJDBC, InfernalSettings infernalSettingsFromREST) {
		//update all settings from JDBC with settings from REST
		infernalSettingsFromJDBC.setSets("Default");
		infernalSettingsFromJDBC.setGroups(infernalSettingsFromREST.getGroups());
		infernalSettingsFromJDBC.setClientPath(infernalSettingsFromREST.getClientPath());
		infernalSettingsFromJDBC.setCurrentVersion(infernalSettingsFromREST.getCurrentVersion());
		infernalSettingsFromJDBC.setClientUpdateSel(infernalSettingsFromREST.getAutoBotStart());
		//TODO check how this works now on live version
		//infernalSettingsFromJDBC.setReplaceConfig(infernalSettingsFromREST.getReplaceConfig());
		//infernalSettingsFromJDBC.setLolHeight(infernalSettingsFromREST.getLolHeight());
		//infernalSettingsFromJDBC.setLolWidth(infernalSettingsFromREST.getLolWidth());
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
		infernalSettingsFromJDBC.setOpenChest(infernalSettingsFromREST.getOpenChest());
		infernalSettingsFromJDBC.setOpenHexTech(infernalSettingsFromREST.getOpenHexTech());
		infernalSettingsFromJDBC.setDisChest(infernalSettingsFromREST.getDisChest());
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
