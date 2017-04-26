package com.creants.creants_2x.core.managers;

import java.util.List;

import com.creants.creants_2x.core.ICoreService;
import com.creants.creants_2x.core.config.ZoneSettings;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.exception.QAntException;

/**
 * @author LamHM
 *
 */
public interface IZoneManager extends ICoreService {
	List<Zone> getZoneList();


	Zone getZoneByName(String name);


	Zone getZoneById(int id);


	void initializeZones() throws QAntException;


	void addZone(Zone zone);


	Zone createZone(ZoneSettings setting) throws QAntException;


	Room createRoom(Zone zone, ZoneSettings.RoomSettings setting) throws QAntException;
}
