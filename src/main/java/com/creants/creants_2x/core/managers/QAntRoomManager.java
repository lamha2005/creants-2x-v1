package com.creants.creants_2x.core.managers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.BaseCoreService;
import com.creants.creants_2x.core.config.ZoneSettings;
import com.creants.creants_2x.core.entities.IRoomFactory;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.exception.QAntCreateRoomException;
import com.creants.creants_2x.core.exception.QAntErrorCode;
import com.creants.creants_2x.core.exception.QAntErrorData;
import com.creants.creants_2x.core.exception.QAntExtensionException;
import com.creants.creants_2x.core.exception.QAntRoomException;
import com.creants.creants_2x.core.exception.QAntRuntimeException;
import com.creants.creants_2x.core.extension.ExtensionLevel;
import com.creants.creants_2x.core.extension.ExtensionType;
import com.creants.creants_2x.core.extension.IQAntExtension;
import com.creants.creants_2x.core.setting.CreateRoomSettings;
import com.creants.creants_2x.core.util.DefaultPlayerIdGenerator;
import com.creants.creants_2x.core.util.IPlayerIdGenerator;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public final class QAntRoomManager extends BaseCoreService implements IRoomManager {
	private final Map<Integer, Room> roomsById;
	private final Map<String, Room> roomsByName;
	private final Map<String, List<Room>> roomsByGroup;
	private final List<String> groups;
	private final AtomicInteger gameRoomCounter;
	private final IRoomFactory roomFactory;
	private QAntServer qant;
	private Zone ownerZone;
	private Class<? extends IPlayerIdGenerator> playerIdGeneratorClass;

	public QAntRoomManager() {
		this.playerIdGeneratorClass = DefaultPlayerIdGenerator.class;
		this.qant = QAntServer.getInstance();
		this.roomsById = new ConcurrentHashMap<Integer, Room>();
		this.roomsByName = new ConcurrentHashMap<String, Room>();
		this.roomsByGroup = new ConcurrentHashMap<String, List<Room>>();
		this.groups = new LinkedList<String>();
		this.gameRoomCounter = new AtomicInteger();
		this.roomFactory = qant.getServiceProvider().getRoomFactory();
	}

	@Override
	public Room createRoom(CreateRoomSettings params) throws QAntCreateRoomException {
		return createRoom(params, null);
	}

	@Override
	public Room createRoom(CreateRoomSettings params, QAntUser owner) throws QAntCreateRoomException {
		String roomName = params.getName();
		try {
			validateRoomName(roomName);
		} catch (QAntRoomException roomExc) {
			throw new QAntCreateRoomException(roomExc.getMessage(), roomExc.getErrorData());
		}

		Room newRoom = roomFactory.createNewRoom(params);
		newRoom.setZone(ownerZone);
		newRoom.setGroupId(params.getGroupId());
		newRoom.setPassword(params.getPassword());
		newRoom.setHidden(params.isHidden());
		newRoom.setMaxUsers(params.getMaxUsers());
		if (params.isGame()) {
			newRoom.setMaxSpectators(params.getMaxSpectators());
		} else {
			newRoom.setMaxSpectators(0);
		}

		newRoom.setOwner(owner);
		if (roomsById.size() >= ownerZone.getMaxAllowedRooms()) {
			throw new QAntCreateRoomException("Zone is full. Can't add any more rooms.",
					new QAntErrorData(QAntErrorCode.CREATE_ROOM_ZONE_FULL));
		}

		addRoom(newRoom);
		newRoom.setActive(true);
		if (params.getExtension() != null && params.getExtension().getId() != null
				&& params.getExtension().getId().length() > 0) {
			try {
				createRoomExtension(newRoom, params.getExtension());
			} catch (QAntExtensionException e) {
				QAntTracer.warn(this.getClass(), "Failure while creating room extension." + "/"
						+ "If the CreateRoom request was sent from client make sure that the extension name matches the name of an existing extension");
			}
		}
		if (newRoom.isGame()) {
			gameRoomCounter.incrementAndGet();
		}
		QAntTracer.info(this.getClass(), String.format("Room created: %s, %s, type = %s", newRoom.getZone().toString(),
				newRoom.toString(), newRoom.getClass().getSimpleName()));
		return newRoom;
	}

	private void createRoomExtension(Room room, CreateRoomSettings.RoomExtensionSettings params)
			throws QAntExtensionException {
		if (params == null) {
			return;
		}
		ExtensionType extType = ExtensionType.JAVA;
		ExtensionLevel extLevel = ExtensionLevel.ROOM;
		String className = params.getClassName();

		ZoneSettings.ExtensionSettings extSettings = new ZoneSettings.ExtensionSettings();
		extSettings.name = params.getId();
		extSettings.file = className;
		extSettings.propertiesFile = params.getPropertiesFile();
		extSettings.reloadMode = "AUTO";
		extSettings.type = extType.toString();
		qant.getExtensionManager().createExtension(extSettings, extLevel, room.getZone(), room);
	}

	@Override
	public Class<? extends IPlayerIdGenerator> getDefaultRoomPlayerIdGenerator() {
		return this.playerIdGeneratorClass;
	}

	@Override
	public void setDefaultRoomPlayerIdGeneratorClass(final Class<? extends IPlayerIdGenerator> customIdGeneratorClass) {
		this.playerIdGeneratorClass = customIdGeneratorClass;
	}

	@Override
	public void addGroup(final String groupId) {
		synchronized (groups) {
			groups.add(groupId);
		}
	}

	@Override
	public void addRoom(Room room) {
		roomsById.put(room.getId(), room);
		roomsByName.put(room.getName(), room);
		synchronized (groups) {
			if (!groups.contains(room.getGroupId())) {
				groups.add(room.getGroupId());
			}
		}
		addRoomToGroup(room);
	}

	@Override
	public boolean containsGroup(String groupId) {
		boolean flag = false;
		synchronized (this.groups) {
			flag = this.groups.contains(groupId);
		}
		// monitorexit(this.groups)
		return flag;
	}

	@Override
	public List<String> getGroups() {
		List<String> groupsCopy = null;
		synchronized (groups) {
			groupsCopy = new LinkedList<String>(groups);
		}
		return groupsCopy;
	}

	@Override
	public Room getRoomById(int id) {
		return this.roomsById.get(id);
	}

	@Override
	public Room getRoomByName(final String name) {
		return this.roomsByName.get(name);
	}

	@Override
	public List<Room> getRoomList() {
		return new LinkedList<Room>(this.roomsByName.values());
	}

	@Override
	public List<Room> getRoomListFromGroup(final String groupId) {
		final List<Room> roomList = this.roomsByGroup.get(groupId);
		List<Room> copyOfRoomList = null;
		if (roomList != null) {
			synchronized (roomList) {
				copyOfRoomList = new LinkedList<Room>(roomList);
				// monitorexit(roomList)
				return copyOfRoomList;
			}
		}
		copyOfRoomList = new LinkedList<Room>();
		return copyOfRoomList;
	}

	@Override
	public int getGameRoomCount() {
		return this.gameRoomCounter.get();
	}

	@Override
	public int getTotalRoomCount() {
		return roomsById.size();
	}

	@Override
	public void removeGroup(String groupId) {
		synchronized (groups) {
			groups.remove(groupId);
		}
	}

	@Override
	public void removeRoom(int roomId) {
		Room room = roomsById.get(roomId);
		if (room == null) {
			QAntTracer.warn(this.getClass(), "Can't remove requested room. ID = " + roomId + ". Room was not found.");
		} else {
			removeRoom(room);
		}
	}

	@Override
	public void removeRoom(String name) {
		Room room = roomsByName.get(name);
		if (room == null) {
			QAntTracer.warn(this.getClass(), "Can't remove requested room. Name = " + name + ". Room was not found.");
		} else {
			removeRoom(room);
		}
	}

	public void removeRoom(Room room) {
		try {
			IQAntExtension roomExtension = room.getExtension();
			if (roomExtension != null) {
				qant.getExtensionManager().destroyExtension(roomExtension);
			}
		} finally {
			room.destroy();
			boolean wasRemoved = roomsById.remove(room.getId()) != null;
			roomsByName.remove(room.getName());
			removeRoomFromGroup(room);
			if (wasRemoved && room.isGame()) {
				gameRoomCounter.decrementAndGet();
			}
			QAntTracer.info(this.getClass(),
					String.format("Room removed: %s, %s", room.getZone().toString(), room.toString()));
		}
		room.destroy();
		boolean wasRemoved = roomsById.remove(room.getId()) != null;
		roomsByName.remove(room.getName());
		removeRoomFromGroup(room);
		if (wasRemoved && room.isGame()) {
			gameRoomCounter.decrementAndGet();
		}
		QAntTracer.info(this.getClass(),
				String.format("Room removed: %s, %s", room.getZone().toString(), room.toString()));
	}

	@Override
	public boolean containsRoom(int id, String groupId) {
		Room room = roomsById.get(id);
		return isRoomContainedInGroup(room, groupId);
	}

	@Override
	public boolean containsRoom(int id) {
		return roomsById.containsKey(id);
	}

	@Override
	public boolean containsRoom(Room room, String groupId) {
		return isRoomContainedInGroup(room, groupId);
	}

	@Override
	public boolean containsRoom(Room room) {
		return roomsById.containsValue(room);
	}

	@Override
	public boolean containsRoom(String name, String groupId) {
		Room room = roomsByName.get(name);
		return isRoomContainedInGroup(room, groupId);
	}

	@Override
	public boolean containsRoom(String name) {
		return roomsByName.containsKey(name);
	}

	@Override
	public Zone getOwnerZone() {
		return ownerZone;
	}

	@Override
	public void setOwnerZone(final Zone zone) {
		ownerZone = zone;
	}

	@Override
	public void removeUser(QAntUser user) {
		for (Room room : user.getJoinedRooms()) {
			removeUser(user, room);
		}
	}

	@Override
	public void removeUser(QAntUser user, Room room) {
		try {
			if (!room.containsUser(user)) {
				throw new QAntRuntimeException("Can't remove user: " + user + ", from: " + room);
			}
			room.removeUser(user);
			QAntTracer.debug(this.getClass(), "User: " + user.getName() + " removed from Room: " + room.getName());
		} finally {
			handleAutoRemove(room);
		}

		handleAutoRemove(room);
	}

	@Override
	public void checkAndRemove(Room room) {
		handleAutoRemove(room);
	}

	@Override
	public void changeRoomName(Room room, String newName) throws QAntRoomException {
		if (room == null) {
			throw new IllegalArgumentException("Can't change name. Room is Null!");
		}
		if (!containsRoom(room)) {
			throw new IllegalArgumentException(room + " is not managed by this Zone: " + ownerZone);
		}
		validateRoomName(newName);
		String oldName = room.getName();
		room.setName(newName);
		roomsByName.put(newName, room);
		roomsByName.remove(oldName);
	}

	@Override
	public void changeRoomPasswordState(Room room, String password) {
		if (room == null) {
			throw new IllegalArgumentException("Can't change password. Room is Null!");
		}
		if (!this.containsRoom(room)) {
			throw new IllegalArgumentException(room + " is not managed by this Zone: " + this.ownerZone);
		}
		room.setPassword(password);
	}

	@Override
	public void changeRoomCapacity(Room room, int newMaxUsers, int newMaxSpect) {
		if (room == null) {
			throw new IllegalArgumentException("Can't change password. Room is Null!");
		}
		if (!this.containsRoom(room)) {
			throw new IllegalArgumentException(room + " is not managed by this Zone: " + this.ownerZone);
		}
		if (newMaxUsers > 0) {
			room.setMaxUsers(newMaxUsers);
		}
		if (newMaxSpect >= 0) {
			room.setMaxSpectators(newMaxSpect);
		}
	}

	private void handleAutoRemove(Room room) {
	}

	private void removeWhenEmpty(Room room) {
		if (room.isEmpty()) {
			qant.getAPIManager().getQAntApi().removeRoom(room);
		}
	}

	private void removeWhenEmptyAndCreatorIsGone(Room room) {
		QAntUser owner = room.getOwner();
		if (owner != null && !owner.isConnected()) {
			qant.getAPIManager().getQAntApi().removeRoom(room);
		}
	}

	private boolean isRoomContainedInGroup(Room room, String groupId) {
		boolean flag = false;
		if (room != null && room.getGroupId().equals(groupId) && containsGroup(groupId)) {
			flag = true;
		}
		return flag;
	}

	private void addRoomToGroup(Room room) {
		String groupId = room.getGroupId();
		List<Room> roomList = roomsByGroup.get(groupId);
		if (roomList == null) {
			roomList = new LinkedList<Room>();
			roomsByGroup.put(groupId, roomList);
		}
		synchronized (roomList) {
			roomList.add(room);
		}
	}

	private void removeRoomFromGroup(Room room) {
		final List<Room> roomList = roomsByGroup.get(room.getGroupId());
		if (roomList != null) {
			synchronized (roomList) {
				roomList.remove(room);
				return;
			}
		}
		QAntTracer.info(this.getClass(), "Cannot remove room: " + room.getName() + " from it's group: "
				+ room.getGroupId() + ". The group was not found.");
	}

	private void validateRoomName(String roomName) throws QAntRoomException {
		if (containsRoom(roomName)) {
			QAntErrorData errorData = new QAntErrorData(QAntErrorCode.ROOM_DUPLICATE_NAME);
			errorData.addParameter(roomName);
			String message = String.format("A room with the same name already exists: %s", roomName);
			throw new QAntRoomException(message, errorData);
		}

		int nameLen = roomName.length();
		int minLen = 5;
		int maxLen = 50;
		if (nameLen < minLen || nameLen > maxLen) {
			QAntErrorData errorData2 = new QAntErrorData(QAntErrorCode.ROOM_NAME_BAD_SIZE);
			errorData2.addParameter(String.valueOf(minLen));
			errorData2.addParameter(String.valueOf(maxLen));
			errorData2.addParameter(String.valueOf(nameLen));
			throw new QAntRoomException(
					String.format("Room name length is out of valid range. Min: %s, Max: %s, Found: %s (%s)", minLen,
							maxLen, nameLen, roomName),
					errorData2);
		}
	}
}
