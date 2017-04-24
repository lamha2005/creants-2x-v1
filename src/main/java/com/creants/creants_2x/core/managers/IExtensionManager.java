package com.creants.creants_2x.core.managers;

import java.util.List;

import com.creants.creants_2x.core.IQAntEvent;
import com.creants.creants_2x.core.IQAntEventListener;
import com.creants.creants_2x.core.QAntEventType;
import com.creants.creants_2x.core.config.ZoneSettings;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.exception.QAntExtensionException;
import com.creants.creants_2x.core.extension.ExtensionLevel;
import com.creants.creants_2x.core.extension.IQAntExtension;

/**
 * @author LamHM
 *
 */
public interface IExtensionManager {
	void createExtension(ZoneSettings.ExtensionSettings setting, ExtensionLevel extLevel, Zone zone, Room room)
			throws QAntExtensionException;


	void destroyExtension(IQAntExtension extension);


	void addExtension(IQAntExtension Extension);


	IQAntExtension getRoomExtension(Room room);


	IQAntExtension getZoneExtension(Zone zone);


	int getExtensionsCount();


	List<IQAntExtension> getExtensions();


	void init();


	void destroy();


	void activateAllExtensions();


	void deactivateAllExtensions();


	void reloadExtension(IQAntExtension extension);


	void reloadRoomExtension(String extensionName, Room room);


	void reloadZoneExtension(String zoneName, Zone zone);


	void addZoneEventListener(QAntEventType eventType, IQAntEventListener listener, Zone zone);


	void addRoomEventListener(QAntEventType eventType, IQAntEventListener listener, Room room);


	void removeZoneEventListener(QAntEventType eventType, IQAntEventListener listener, Zone zone);


	void removeRoomEventListener(QAntEventType eventType, IQAntEventListener listener, Room room);


	void removeListenerFromZone(IQAntEventListener listener, Zone zone);


	void removeListenerFromRoom(IQAntEventListener listener, Room room);


	void dispatchEvent(IQAntEvent event, ExtensionLevel extLevel);


	boolean isExtensionMonitorActive();


	void setExtensionMonitorActive(boolean isActive);
}
