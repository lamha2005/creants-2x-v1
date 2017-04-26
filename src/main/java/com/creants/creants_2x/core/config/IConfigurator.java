package com.creants.creants_2x.core.config;

import java.io.IOException;
import java.util.List;

import com.creants.creants_2x.core.exception.QAntException;

/**
 * @author LamHM
 *
 */
public interface IConfigurator {
	void loadConfiguration() throws Exception;


	List<ZoneSettings> loadZonesConfiguration() throws QAntException;


	void saveServerSettings(boolean saveSetting) throws IOException;


	void saveZoneSettings(ZoneSettings setting, boolean saveSetting) throws IOException;


	void saveZoneSettings(ZoneSettings setting, boolean saveSetting, String name) throws IOException;


	List<ZoneSettings> getZoneSettings();


	ZoneSettings getZoneSetting(String setting);


	ZoneSettings getZoneSetting(int id);


	void saveNewZoneSettings(ZoneSettings setting) throws IOException;


	void removeZoneSetting(String name) throws IOException;
}
