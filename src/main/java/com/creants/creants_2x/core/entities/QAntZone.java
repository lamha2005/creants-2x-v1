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
import com.creants.creants_2x.core.setting.CreateRoomSettings;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.managers.IUserManager;

import io.netty.channel.Channel;

/**
 * @author LamHa
 *
 */
public class QAntZone implements Zone {
	private IZoneManager zoneManager;
	// private final IRoomManager roomManager;
	// private final IUserManager userManager;
	private volatile IQAntExtension extension;
	private boolean customLogin;
	private volatile int maxAllowedRooms;
	private volatile int maxAllowedUsers;
	private volatile int userReconnectionSeconds;
	private int maxUserIdleTime;
	private int maxFailedLogins;
	private final String name;

	private QAntServer qant;

	public QAntZone() {
		this.customLogin = false;
		this.userReconnectionSeconds = 0;
		this.maxUserIdleTime = 0;
		this.maxFailedLogins = 3;
		this.name = "";
		this.qant = QAntServer.getInstance();
		// (this.roomManager = new QAntRoomManager()).setOwnerZone(this);
		// (this.userManager = new QAntUserManager()).setOwnerZone(this);
	}

	@Override
	public List<Room> getRoomListFromGroup(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IUserManager getUserManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setActive(boolean active) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxAllowedUsers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxAllowedUsers(int max) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaxAllowedRooms() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxAllowedRooms(int max) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaxUserIdleTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxUserIdleTime(int max) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCustomLogin() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isForceLogout() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setForceLogout(boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCustomLogin(boolean isCustome) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isGuestUserAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGuestUserAllowed(boolean allowGuest) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getUserCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalRoomCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getGameRoomCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Room createRoom(CreateRoomSettings roomSetting) throws QAntCreateRoomException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Room createRoom(CreateRoomSettings roomSetting, QAntUser owner) throws QAntCreateRoomException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRoomManager getRoomManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IZoneManager getZoneManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setZoneManager(IZoneManager zoneManager) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Room> getRoomList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Room getRoomById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Room getRoomByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRoom(Room room) throws QAntTooManyRoomsException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRoom(Room room) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRoom(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRoom(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkAndRemove(Room room) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeRoomName(Room room, String name) throws QAntRoomException {
		// TODO Auto-generated method stub

	}

	@Override
	public QAntUser getUserById(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QAntUser getUserByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QAntUser getUserByChannel(Channel channel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Channel> getChannelList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<QAntUser> getUserList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAllUsers() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUser(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUser(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUser(Channel channel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUser(QAntUser user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUserFromRoom(QAntUser user, Room room) {
		// TODO Auto-generated method stub

	}

	@Override
	public IQAntExtension getExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExtension(IQAntExtension extension) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaxFailedLogins() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxFailedLogins(int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDump() {
		// TODO Auto-generated method stub
		return null;
	}

}
