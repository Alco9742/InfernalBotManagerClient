package net.nilsghesquiere.infernalclients;

import net.nilsghesquiere.util.dto.InfernalSettingsDTO;

public interface InfernalSettingsInfernalClient {
	public boolean connect();
	public boolean checkPragmas();
	public String getPragmaString();
	public InfernalSettingsDTO getDefaultInfernalSettings();
	public Long insertInfernalSettings(InfernalSettingsDTO infernalSettingsDTO);
	public boolean updateInfernalSettings(InfernalSettingsDTO infernalSettingsDTO);
	public String getOldPragmas();
	public String getNewPragmas();
}
