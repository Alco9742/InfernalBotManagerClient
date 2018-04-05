package net.nilsghesquiere.infernalclients;

import net.nilsghesquiere.entities.InfernalSettings;

public interface InfernalSettingsInfernalClient {
	public boolean connect();
	public boolean checkPragmas();
	public String getPragmaString();
	public InfernalSettings getDefaultInfernalSettings();
	public Long insertInfernalSettings(InfernalSettings infernalSettings);
	public Long updateInfernalSettings(InfernalSettings infernalSettings);
	public String getOldPragmas();
	public String getNewPragmas();
}
