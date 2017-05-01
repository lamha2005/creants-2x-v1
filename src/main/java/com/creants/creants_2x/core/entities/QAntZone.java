package com.creants.creants_2x.core.entities;

import java.util.Collection;
import java.util.List;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.exception.QAntCreateRoomException;
import com.creants.creants_2x.core.exception.QAntRoomException;
import com.creants.creants_2x.core.exception.QAntTooManyRoomsException;
import com.creants.creants_2x.core.extension.IQAntExtension;
import com.creants.creants_2x.core.managers.IRoomManager;
import com.creants.creants_2x.core.managers.IZoneManager;
import com.creants.creants_2x.core.managers.QAntRoomManager;
import com.creants.creants_2x.core.setting.CreateRoomSettings;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.managers.IUserManager;
import com.creants.creants_2x.socket.managers.UserManager;

import io.netty.channel.Channel;

/**
 * @author LamHa
 *
 */
public class QAntZone implements Zone {
	private IZoneManager zoneManager;
	private final IRoomManager roomManager;
	private final IUserManager userManager;
	private volatile IQAntExtension extension;
	private boolean customLogin;
	private volatile int maxAllowedRooms;
	private volatile int maxAllowedUsers;
	private volatile int userReconnectionSeconds;
	private int maxUserIdleTime;
	private int maxFailedLogins;
	private final String name;
	private Integer id;
	private QAntServer qant;
	private boolean active;
	private String guestUserNamePrefix;


	public QAntZone(String name) {
		this.customLogin = true;
		this.userReconnectionSeconds = 0;
		this.maxUserIdleTime = 0;
		this.maxFailedLogins = 3;
		this.active = true;
		this.maxAllowedUsers = Integer.MAX_VALUE;
		this.maxAllowedRooms = Integer.MAX_VALUE;
		this.id = null;
		this.name = name;
		this.qant = QAntServer.getInstance();
		(this.roomManager = new QAntRoomManager()).setOwnerZone(this);
		(this.userManager = new UserManager()).setOwnerZone(this);
		this.roomManager.addGroup("default");
	}


	public int getUserReconnectionSeconds() {
		return userReconnectionSeconds;
	}


	public void setUserReconnectionSeconds(int userReconnectionSeconds) {
		this.userReconnectionSeconds = userReconnectionSeconds;
	}


	public String getGuestUserNamePrefix() {
		return guestUserNamePrefix;
	}


	@Override
	public List<Room> getRoomListFromGroup(String groupId) {
		return roomManager.getRoomListFromGroup(groupId);
	}


	@Override
	public void login(QAntUser user) {
		user.setZone(this);
		userManager.addUser(user);
		qant.getUserManager().addUser(user);
	}


	@Override
	public IUserManager getUserManager() {
		return userManager;
	}


	@Override
	public boolean isActive() {
		return active;
	}


	@Override
	public void setActive(boolean active) {
		this.active = active;
	}


	@Override
	public void setId(int id) {
		this.id = id;
	}


	@Override
	public String getName() {
		return name;
	}


	@Override
	public int getId() {
		return id;
	}


	@Override
	public int getMaxAllowedUsers() {
		return maxAllowedUsers;
	}


	@Override
	public void setMaxAllowedUsers(int max) {
		this.maxAllowedUsers = max;
	}


	@Override
	public int getMaxAllowedRooms() {
		return maxAllowedRooms;
	}


	@Override
	public void setMaxAllowedRooms(int max) {
		maxAllowedRooms = max;
	}


	@Override
	public int getMaxUserIdleTime() {
		return maxUserIdleTime;
	}


	@Override
	public void setMaxUserIdleTime(int max) {
		maxUserIdleTime = max;
	}


	@Override
	public boolean isCustomLogin() {
		return customLogin;
	}


	@Override
	public boolean isForceLogout() {
		return false;
	}


	@Override
	public void setForceLogout(boolean value) {

	}


	@Override
	public void setCustomLogin(boolean isCustome) {
		customLogin = isCustome;
	}


	@Override
	public boolean isGuestUserAllowed() {
		return true;
	}


	@Override
	public void setGuestUserAllowed(boolean allowGuest) {

	}


	@Override
	public int getUserCount() {
		return userManager.getUserCount();
	}


	@Override
	public int getTotalRoomCount() {
		return roomManager.getTotalRoomCount();
	}


	@Override
	public int getGameRoomCount() {
		return roomManager.getGameRoomCount();
	}


	@Override
	public Room createRoom(CreateRoomSettings params) throws QAntCreateRoomException {
		return roomManager.createRoom(params);
	}


	@Override
	public Room createRoom(CreateRoomSettings params, QAntUser user) throws QAntCreateRoomException {
		return roomManager.createRoom(params, user);
	}


	@Override
	public IRoomManager getRoomManager() {
		return roomManager;
	}


	@Override
	public IZoneManager getZoneManager() {
		return zoneManager;
	}


	@Override
	public void setZoneManager(IZoneManager zoneManager) {
		this.zoneManager = zoneManager;
	}


	@Override
	public List<Room> getRoomList() {
		return roomManager.getRoomList();
	}


	@Override
	public Room getRoomById(int id) {
		return roomManager.getRoomById(id);
	}


	@Override
	public Room getRoomByName(String name) {
		return roomManager.getRoomByName(name);
	}


	@Override
	public void addRoom(Room room) throws QAntTooManyRoomsException {
		roomManager.addRoom(room);
	}


	@Override
	public void removeRoom(Room room) {
		roomManager.removeRoom(room);
	}


	@Override
	public void removeRoom(int id) {
		roomManager.removeRoom(id);
	}


	@Override
	public void removeRoom(String name) {
		roomManager.removeRoom(name);
	}


	@Override
	public void checkAndRemove(Room room) {
		roomManager.checkAndRemove(room);
	}


	@Override
	public void changeRoomName(Room room, String name) throws QAntRoomException {
		roomManager.changeRoomName(room, name);
	}


	@Override
	public QAntUser getUserById(int userId) {
		return userManager.getUserById(userId);
	}


	@Override
	public QAntUser getUserByName(String name) {
		return userManager.getUserByName(name);
	}


	@Override
	public QAntUser getUserByChannel(Channel channel) {
		return userManager.getUserByChannel(channel);
	}


	@Override
	public Collection<Channel> getChannelList() {
		return userManager.getAllChannels();
	}


	@Override
	public Collection<QAntUser> getUserList() {
		return userManager.getAllUsers();
	}


	@Override
	public void removeAllUsers() {
		for (QAntUser user : userManager.getAllUsers()) {
			qant.getAPIManager().getQAntApi().disconnectUser(user);
		}
	}


	@Override
	public void removeUser(int userId) {
		QAntUser user = userManager.getUserById(userId);
		if (user == null) {
			QAntTracer.info(this.getClass(),
					"Can't remove user with Id: " + userId + ". User doesn't exist in Zone: " + this.name);
		} else {
			removeUser(user);
		}
	}


	@Override
	public void removeUser(String name) {
		QAntUser user = userManager.getUserByName(name);
		if (user == null) {
			QAntTracer.info(this.getClass(),
					"Can't remove user with name: " + name + ". User doesn't exist in Zone: " + this.name);
		} else {
			removeUser(user);
		}

	}


	@Override
	public void removeUser(Channel channel) {
		QAntUser user = userManager.getUserByChannel(channel);
		if (user == null) {
			QAntTracer.info(this.getClass(),
					"Can't remove user with channel: " + channel + ". User doesn't exist in Zone: " + this.name);
		} else {
			removeUser(user);
		}

	}


	@Override
	public void removeUser(QAntUser user) {
		userManager.disconnectUser(user);
		roomManager.removeUser(user);
	}


	@Override
	public void removeUserFromRoom(QAntUser user, Room room) {
		roomManager.removeUser(user, room);
	}


	@Override
	public IQAntExtension getExtension() {
		return extension;
	}


	@Override
	public void setExtension(IQAntExtension extension) {
		this.extension = extension;
	}


	@Override
	public int getMaxFailedLogins() {
		return maxFailedLogins;
	}


	@Override
	public void setMaxFailedLogins(int count) {
		this.setMaxFailedLogins(count);
	}


	@Override
	public String getDump() {
		throw new UnsupportedOperationException("Sorry, not implemented yet!");
	}


	@Override
	public void setGuestUserNamePrefix(String prefixName) {
		this.guestUserNamePrefix = prefixName;
	}

}
