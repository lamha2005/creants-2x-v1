package com.creants.creants_2x.core.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.creants.creants_2x.core.exception.QAntException;

/**
 * @author LamHM
 *
 */
public class QAntConfig implements IConfigurator {

	public void loadConfiguration() throws Exception {

	}


	@Override
	public List<ZoneSettings> loadZonesConfiguration() throws QAntException {
		List<ZoneSettings> settings = new ArrayList<>();
		ZoneSettings zone = new ZoneSettings();
		zone.name = "Mu Fantasy";
		settings.add(zone);
		return settings;
	}


	@Override
	public void saveServerSettings(boolean saveSetting) throws IOException {
		// TODO Auto-generated method stub

	}


	@Override
	public void saveZoneSettings(ZoneSettings setting, boolean saveSetting) throws IOException {
		// TODO Auto-generated method stub

	}


	@Override
	public void saveZoneSettings(ZoneSettings setting, boolean saveSetting, String name) throws IOException {
		// TODO Auto-generated method stub

	}


	@Override
	public List<ZoneSettings> getZoneSettings() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ZoneSettings getZoneSetting(String setting) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ZoneSettings getZoneSetting(int id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void saveNewZoneSettings(ZoneSettings setting) throws IOException {
		// TODO Auto-generated method stub

	}


	@Override
	public void removeZoneSetting(String name) throws IOException {
		// TODO Auto-generated method stub

	}

}
