package com.creants.creants_2x.core.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.BaseCoreService;
import com.creants.creants_2x.core.config.IConfigurator;
import com.creants.creants_2x.core.config.ZoneSettings;
import com.creants.creants_2x.core.config.ZoneSettings.RoomSettings;
import com.creants.creants_2x.core.entities.QAntZone;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.exception.QAntException;
import com.creants.creants_2x.core.exception.QAntExtensionException;
import com.creants.creants_2x.core.exception.QAntRuntimeException;
import com.creants.creants_2x.core.extension.ExtensionLevel;
import com.creants.creants_2x.core.setting.CreateRoomSettings;
import com.creants.creants_2x.core.util.QAntTracer;

/**
 * @author LamHM
 *
 */
public class QAntZoneManager extends BaseCoreService implements IZoneManager {
	protected ConcurrentMap<String, Zone> zones;
	protected QAntServer qant;
	protected IConfigurator configurator;
	private boolean inited;


	public QAntZoneManager() {
		this.inited = false;
		this.zones = new ConcurrentHashMap<String, Zone>();
	}


	@Override
	public synchronized void init(Object o) {
		if (!this.inited) {
			super.init(o);
			this.qant = QAntServer.getInstance();
			this.configurator = qant.getConfigurator();
			this.inited = true;
		}
	}


	@Override
	public List<Zone> getZoneList() {
		return new ArrayList<>(zones.values());
	}


	@Override
	public Zone getZoneByName(String name) {
		return zones.get(name);
	}


	@Override
	public Zone getZoneById(int id) {
		Zone theZone = null;
		for (Zone zone : this.zones.values()) {
			if (zone.getId() == id) {
				theZone = zone;
				break;
			}
		}

		return theZone;
	}


	@Override
	public void initializeZones() throws QAntException {
		List<ZoneSettings> zoneSettings = configurator.loadZonesConfiguration();
		for (ZoneSettings settings : zoneSettings) {
			QAntTracer.info(this.getClass(), String.format("%n%n%s%n >> Zone: %s %n%s%n",
					"::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::", settings.name,
					"::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::"));
			addZone(createZone(settings));
		}
	}


	@Override
	public void addZone(Zone zone) {
		if (zones.containsKey(zone.getName())) {
			throw new QAntRuntimeException(
					"Zone already exists: " + zone.getName() + ". Can't add the same zone more than once.");
		}

		zones.put(zone.getName(), zone);
	}


	@Override
	public Zone createZone(ZoneSettings settings) throws QAntException {
		Zone zone = new QAntZone(settings.name);
		zone.setId(settings.getId());
		zone.setCustomLogin(settings.isCustomLogin);
		zone.setGuestUserAllowed(true);
		zone.setGuestUserNamePrefix(settings.guestUserNamePrefix);
		zone.setMaxAllowedUsers(settings.maxUsers);
		zone.setZoneManager(this);
		for (ZoneSettings.RoomSettings roomSettings : settings.rooms) {
			try {
				createRoom(zone, roomSettings);
			} catch (QAntException e) {
				QAntTracer.warn(this.getClass(),
						"Error while creating Room: " + roomSettings.name + " -> " + e.getMessage());
			}
		}

		if (settings.extension != null && settings.extension.name != null && settings.extension.name.length() > 0) {
			try {
				qant.getExtensionManager().createExtension(settings.extension, ExtensionLevel.ZONE, zone, null);
			} catch (QAntExtensionException err) {
				String extName = (settings.extension.name == null) ? "{Unknown}" : settings.extension.name;
				throw new QAntException("Extension creation failure: " + extName + " - " + err.getMessage());
			}
		}
		zone.setActive(true);
		return zone;
	}


	@Override
	public Room createRoom(Zone zone, RoomSettings roomSettings) throws QAntException {
		CreateRoomSettings params = new CreateRoomSettings();
		params.setName(roomSettings.name);
		params.setGroupId(roomSettings.groupId);
		params.setPassword(roomSettings.password);
		params.setMaxUsers(roomSettings.maxUsers);
		params.setMaxSpectators(roomSettings.maxSpectators);
		params.setGame(roomSettings.isGame);
		params.setHidden(roomSettings.isHidden);
		Room room = qant.getAPIManager().getQAntApi().createRoom(zone, params, null, false, null, false, false);
		if (roomSettings.extension != null && roomSettings.extension.name != null
				&& roomSettings.extension.name.length() > 0) {
			try {
				qant.getExtensionManager().createExtension(roomSettings.extension, ExtensionLevel.ROOM, zone, room);
			} catch (QAntExtensionException err) {
				String extName = (roomSettings.extension.name == null) ? "{Unknown}" : roomSettings.extension.name;
				throw new QAntException(
						"Room Extension creation failure: " + extName + " - " + err.getMessage() + " - Room: " + room);
			}
		}
		return room;
	}

}
