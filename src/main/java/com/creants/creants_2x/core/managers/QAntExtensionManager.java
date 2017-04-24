package com.creants.creants_2x.core.managers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.IQAntEvent;
import com.creants.creants_2x.core.IQAntEventListener;
import com.creants.creants_2x.core.IQAntEventManager;
import com.creants.creants_2x.core.QAntEventParam;
import com.creants.creants_2x.core.QAntEventSysParam;
import com.creants.creants_2x.core.QAntEventType;
import com.creants.creants_2x.core.QAntSystemEvent;
import com.creants.creants_2x.core.config.ZoneSettings;
import com.creants.creants_2x.core.config.ZoneSettings.ExtensionSettings;
import com.creants.creants_2x.core.controllers.IControllerCommand;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.exception.QAntExtensionException;
import com.creants.creants_2x.core.extension.ExtensionLevel;
import com.creants.creants_2x.core.extension.ExtensionReloadMode;
import com.creants.creants_2x.core.extension.ExtensionType;
import com.creants.creants_2x.core.extension.IQAntExtension;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHM
 *
 */
public class QAntExtensionManager implements IExtensionManager, IQAntEventListener {
	private final ConcurrentMap<Zone, IQAntExtension> extensionsByZone;
	private final ConcurrentMap<Room, IQAntExtension> extensionsByRoom;
	private final Map<Room, Map<QAntEventType, Set<IQAntEventListener>>> listenersByRoom;
	private final Map<Zone, Map<QAntEventType, Set<IQAntEventListener>>> listenersByZone;
	private QAntServer qant;
	private IQAntEventManager eventManager;
	private boolean inited;

	public QAntExtensionManager() {
		inited = false;
		extensionsByZone = new ConcurrentHashMap<Zone, IQAntExtension>();
		extensionsByRoom = new ConcurrentHashMap<Room, IQAntExtension>();
		listenersByRoom = new ConcurrentHashMap<Room, Map<QAntEventType, Set<IQAntEventListener>>>();
		listenersByZone = new ConcurrentHashMap<Zone, Map<QAntEventType, Set<IQAntEventListener>>>();
	}

	@Override
	public void createExtension(ExtensionSettings settings, ExtensionLevel extLevel, Zone parentZone, Room parentRoom)
			throws QAntExtensionException {
		if (settings.file == null || settings.file.length() == 0) {
			throw new QAntExtensionException("Extension file parameter is missing!");
		}

		if (settings.name == null || settings.name.length() == 0) {
			throw new QAntExtensionException("Extension name parameter is missing!");
		}

		if (settings.type == null) {
			throw new QAntExtensionException("Extension type was not specified: " + settings.name);
		}

		if (settings.reloadMode == null) {
			settings.reloadMode = "";
		}

		ExtensionReloadMode reloadMode = ExtensionReloadMode.valueOf(settings.reloadMode.toUpperCase());
		if (reloadMode == null) {
			reloadMode = ExtensionReloadMode.MANUAL;
		}

		ExtensionType extensionType = ExtensionType.valueOf(settings.type.toUpperCase());
		IQAntExtension extension = null;
		if (extensionType == ExtensionType.JAVA) {
			extension = createJavaExtension(settings);
		}

		extension.setLevel(extLevel);
		extension.setName(settings.name);
		extension.setExtensionFileName(settings.file);
		extension.setReloadMode(reloadMode);
		extension.setParentZone(parentZone);
		extension.setParentRoom(parentRoom);
		try {
			if (settings.propertiesFile != null
					&& (settings.propertiesFile.startsWith("../") || settings.propertiesFile.startsWith("/"))) {
				throw new QAntExtensionException(
						"Illegal path for Extension property file. File path outside the extensions/ folder is not valid: "
								+ settings.propertiesFile);
			}
			extension.setPropertiesFileName(settings.propertiesFile);
		} catch (IOException e) {
			throw new QAntExtensionException("Unable to load extension properties file: " + settings.propertiesFile);
		}

		try {
			extension.init();
			addExtension(extension);
			if (parentRoom != null) {
				parentRoom.setExtension(extension);
			} else {
				parentZone.setExtension(extension);
			}
		} catch (Exception err) {
			QAntTracer.error(this.getClass(), "Extension initialization failed.");
		}
	}

	private IQAntExtension createJavaExtension(ZoneSettings.ExtensionSettings settings) throws QAntExtensionException {
		IQAntExtension extension;
		File jarFile = new File("extensions/" + settings.name);
		try (URLClassLoader extensionClassLoader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() },
				getClass().getClassLoader())) {
			Class<?> extensionClass = extensionClassLoader.loadClass(settings.file);
			if (!IQAntExtension.class.isAssignableFrom(extensionClass)) {
				throw new QAntExtensionException(
						"Extension does not implement IQAntExtension interface: " + settings.name);
			}
			extension = (IQAntExtension) extensionClass.newInstance();
			extension.setType(ExtensionType.JAVA);
		} catch (Exception e) {
			throw new QAntExtensionException("Extension boot error. " + e.getMessage());
		}

		return extension;
	}

	@Override
	public void destroyExtension(IQAntExtension extension) {
		try {
			extension.destroy();
		} finally {
			if (extension.getLevel() == ExtensionLevel.ROOM) {
				extensionsByRoom.remove(extension.getParentRoom());
			} else {
				extensionsByZone.remove(extension.getParentZone());
			}
			QAntTracer.debug(this.getClass(), "Removed: " + extension);
		}
		if (extension.getLevel() == ExtensionLevel.ROOM) {
			extensionsByRoom.remove(extension.getParentRoom());
		} else {
			extensionsByZone.remove(extension.getParentZone());
		}

		QAntTracer.debug(this.getClass(), "Removed: " + extension);
	}

	@Override
	public void addExtension(IQAntExtension extension) {
		if (extension.getLevel() == ExtensionLevel.ZONE) {
			extensionsByZone.put(extension.getParentZone(), extension);
		} else if (extension.getLevel() == ExtensionLevel.ROOM) {
			extensionsByRoom.put(extension.getParentRoom(), extension);
		}
	}

	@Override
	public IQAntExtension getRoomExtension(Room room) {
		return extensionsByRoom.get(room);
	}

	@Override
	public IQAntExtension getZoneExtension(Zone zone) {
		return extensionsByZone.get(zone);
	}

	@Override
	public int getExtensionsCount() {
		return extensionsByRoom.size() + extensionsByZone.size();
	}

	@Override
	public List<IQAntExtension> getExtensions() {
		final List<IQAntExtension> allOfThem = new ArrayList<IQAntExtension>(extensionsByRoom.values());
		allOfThem.addAll(extensionsByZone.values());
		return allOfThem;
	}

	@Override
	public void init() {
		if (!inited) {
			qant = QAntServer.getInstance();
			eventManager = qant.getEventManager();
			QAntEventType[] values;
			for (int length = (values = QAntEventType.values()).length, i = 0; i < length; ++i) {
				QAntEventType type = values[i];
				eventManager.addEventListener(type, this);
			}
			inited = true;
			QAntTracer.debug(this.getClass(), "Extension Manager started.");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleServerEvent(IQAntEvent event) throws Exception {
		QAntEventType type = event.getType();
		if (type == QAntEventType.SERVER_READY) {
			dispatchEvent(event, ExtensionLevel.GLOBAL);
		} else if (type == QAntEventType.USER_LOGIN) {
			dispatchZoneLevelEvent(event);
		} else if (type == QAntEventType.USER_JOIN_ZONE) {
			dispatchZoneLevelEvent(event);
		} else if (type == QAntEventType.USER_LOGOUT) {
			dispatchZoneLevelEvent(event);
		} else if (type == QAntEventType.USER_JOIN_ROOM) {
			dispatchZoneLevelEvent(event);
			dispatchRoomLevelEvent(event);
		} else if (type == QAntEventType.USER_LEAVE_ROOM) {
			dispatchZoneLevelEvent(event);
			dispatchRoomLevelEvent(event);
		} else if (type == QAntEventType.ROOM_ADDED) {
			dispatchZoneLevelEvent(event);
		} else if (type == QAntEventType.ROOM_REMOVED) {
			Room theRoom = (Room) event.getParameter(QAntEventParam.ROOM);
			extensionsByRoom.remove(theRoom);
			dispatchZoneLevelEvent(event);
		} else if (type == QAntEventType.USER_DISCONNECT) {
			dispatchZoneLevelEvent(event);
			dispatchRoomLevelEvent(event, (List<Room>) event.getParameter(QAntEventParam.JOINED_ROOMS));
		} else if (type == QAntEventType.USER_RECONNECTION_TRY) {
			dispatchZoneLevelEvent(event);
			QAntUser user = (QAntUser) event.getParameter(QAntEventParam.USER);
			dispatchRoomLevelEvent(event, user.getJoinedRooms());
		} else if (type == QAntEventType.USER_RECONNECTION_SUCCESS) {
			dispatchZoneLevelEvent(event);
			QAntUser user = (QAntUser) event.getParameter(QAntEventParam.USER);
			dispatchRoomLevelEvent(event, user.getJoinedRooms());
		} else if (type == QAntEventType.PUBLIC_MESSAGE) {
			dispatchZoneLevelEvent(event);
		} else if (type == QAntEventType.PRIVATE_MESSAGE) {
			dispatchZoneLevelEvent(event);
		} else if (type == QAntEventType.ROOM_VARIABLES_UPDATE) {
			dispatchZoneLevelEvent(event);
			dispatchRoomLevelEvent(event);
		}
	}

	private void dispatchRoomLevelEvent(IQAntEvent event) {
		Room room = (Room) event.getParameter(QAntEventParam.ROOM);
		dispatchRoomLevelEvent(event, room);
	}

	private void dispatchRoomLevelEvent(IQAntEvent event, Room room) {
		if (room != null) {
			Map<QAntEventType, Set<IQAntEventListener>> listenersByType = listenersByRoom.get(room);
			if (listenersByType != null) {
				Set<IQAntEventListener> listeners = listenersByType.get(event.getType());
				dispatchEvent(event, listeners);
			}
		} else {
			QAntTracer.info(this.getClass(), "Room Event was not dispatched. ROOM param is null: " + event);
		}
	}

	private void dispatchRoomLevelEvent(IQAntEvent event, List<Room> roomList) {
		if (roomList != null) {
			for (Room room : roomList) {
				Map<QAntEventType, Set<IQAntEventListener>> listenersByType = listenersByRoom.get(room);
				if (listenersByType != null) {
					Set<IQAntEventListener> listeners = listenersByType.get(event.getType());
					dispatchEvent(event, listeners);
				}
			}
		} else {
			QAntTracer.info(this.getClass(), "Multi Room Event was not dispatched. RoomList param is null: " + event);
		}
	}

	private void dispatchZoneLevelEvent(IQAntEvent event) {
		Zone zone = (Zone) event.getParameter(QAntEventParam.ZONE);
		if (zone != null) {
			Map<QAntEventType, Set<IQAntEventListener>> listenersByType = listenersByZone.get(zone);
			if (listenersByType != null) {
				Set<IQAntEventListener> listeners = listenersByType.get(event.getType());
				dispatchEvent(event, listeners);
			}
		} else {
			QAntTracer.info(this.getClass(), "Zone Event was not dispatched. ZONE param is null: " + event);
		}
	}

	@Override
	public void destroy() {
		QAntEventType[] values;
		for (int length = (values = QAntEventType.values()).length, i = 0; i < length; ++i) {
			QAntEventType type = values[i];
			eventManager.removeEventListener(type, this);
		}

		listenersByRoom.clear();
		listenersByZone.clear();
		for (IQAntExtension extension : extensionsByRoom.values()) {
			extension.destroy();
		}
		for (IQAntExtension extension : extensionsByZone.values()) {
			extension.destroy();
		}
		extensionsByRoom.clear();
		extensionsByZone.clear();
		QAntTracer.debug(this.getClass(), "Extension Manager stopped.");
	}

	@Override
	public void activateAllExtensions() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivateAllExtensions() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reloadExtension(IQAntExtension extension) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reloadRoomExtension(String extensionName, Room room) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reloadZoneExtension(String zoneName, Zone zone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addZoneEventListener(QAntEventType type, IQAntEventListener listener, Zone zone) {
		Map<QAntEventType, Set<IQAntEventListener>> listenersByType = listenersByZone.get(zone);
		if (listenersByType == null) {
			listenersByType = new ConcurrentHashMap<QAntEventType, Set<IQAntEventListener>>();
			listenersByZone.put(zone, listenersByType);
		}
		Set<IQAntEventListener> listeners = listenersByType.get(type);
		if (listeners == null) {
			listeners = new CopyOnWriteArraySet<IQAntEventListener>();
			listenersByType.put(type, listeners);
		}
		listeners.add(listener);
	}

	@Override
	public void addRoomEventListener(QAntEventType eventType, IQAntEventListener listener, Room room) {
		Map<QAntEventType, Set<IQAntEventListener>> listenersByType = listenersByRoom.get(room);
		if (listenersByType == null) {
			listenersByType = new ConcurrentHashMap<QAntEventType, Set<IQAntEventListener>>();
			this.listenersByRoom.put(room, listenersByType);
		}

		Set<IQAntEventListener> listeners = listenersByType.get(eventType);
		if (listeners == null) {
			listeners = new CopyOnWriteArraySet<IQAntEventListener>();
			listenersByType.put(eventType, listeners);
		}
		listeners.add(listener);
	}

	@Override
	public void removeZoneEventListener(QAntEventType type, IQAntEventListener listener, Zone zone) {
		Map<QAntEventType, Set<IQAntEventListener>> listenersByType = listenersByZone.get(zone);
		if (listenersByType == null) {
			listenersByType = new ConcurrentHashMap<QAntEventType, Set<IQAntEventListener>>();
			listenersByZone.put(zone, listenersByType);
		}

		Set<IQAntEventListener> listeners = listenersByType.get(type);
		if (listeners == null) {
			listeners = new CopyOnWriteArraySet<IQAntEventListener>();
			listenersByType.put(type, listeners);
		}
		listeners.add(listener);
	}

	@Override
	public void removeRoomEventListener(QAntEventType eventType, IQAntEventListener listener, Room room) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListenerFromZone(IQAntEventListener listener, Zone zone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListenerFromRoom(IQAntEventListener listener, Room room) {

	}

	@Override
	public void dispatchEvent(IQAntEvent event, ExtensionLevel level) {
		if (level == ExtensionLevel.GLOBAL) {
			dispatchGlobalEvent(event);
		} else if (level == ExtensionLevel.ZONE) {
			dispatchZoneLevelEvent(event);
		} else if (level == ExtensionLevel.ROOM) {
			dispatchRoomLevelEvent(event);
		}
	}

	private void dispatchEvent(IQAntEvent event, Collection<IQAntEventListener> listeners) {
		if (listeners != null && listeners.size() > 0) {
			for (IQAntEventListener listener : listeners) {
				try {
					listener.handleServerEvent(event);
					if (!(event instanceof QAntSystemEvent)) {
						continue;
					}

					executeEventCommand((QAntSystemEvent) event);
				} catch (Exception e) {
					QAntTracer.warn(this.getClass(), "Error during event handling: " + e + ", Listener: " + listener);
				}
			}
		}
	}

	/**
	 * Thực thi các custom sự kiện hệ thống
	 * 
	 * @param sysEvent
	 * @throws Exception
	 */
	private void executeEventCommand(QAntSystemEvent sysEvent) throws Exception {
		Class<?> commandClass = (Class<?>) sysEvent.getSysParameter(QAntEventSysParam.NEXT_COMMAND);
		IRequest request = (IRequest) sysEvent.getSysParameter(QAntEventSysParam.REQUEST_OBJ);
		if (commandClass != null && request != null) {
			IControllerCommand command = (IControllerCommand) commandClass.newInstance();
			command.execute(request);
		}
	}

	private void dispatchGlobalEvent(IQAntEvent event) {
		List<IQAntEventListener> allListeners = new ArrayList<IQAntEventListener>();
		QAntEventType type = event.getType();
		for (Map<QAntEventType, Set<IQAntEventListener>> zoneListeners : listenersByZone.values()) {
			Set<IQAntEventListener> listeners = zoneListeners.get(type);
			if (listeners != null) {
				allListeners.addAll(listeners);
			}
		}

		for (Map<QAntEventType, Set<IQAntEventListener>> roomListeners : listenersByRoom.values()) {
			Set<IQAntEventListener> listeners = roomListeners.get(type);
			if (listeners != null) {
				allListeners.addAll(listeners);
			}
		}
		dispatchEvent(event, allListeners);
	}

	@Override
	public boolean isExtensionMonitorActive() {
		return false;
	}

	@Override
	public void setExtensionMonitorActive(boolean isActive) {

	}

}
