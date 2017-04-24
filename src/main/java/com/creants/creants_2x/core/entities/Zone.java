package com.creants.creants_2x.core.entities;

import java.util.Collection;
import java.util.List;

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
 * @author LamHM
 *
 */
public interface Zone {
	IUserManager getUserManager();

	boolean isActive();

	void setActive(boolean active);

	void setId(int id);

	String getName();

	int getId();

	List<Room> getRoomListFromGroup(String groupId);

	int getMaxAllowedUsers();

	void setMaxAllowedUsers(int max);

	int getMaxAllowedRooms();

	void setMaxAllowedRooms(int max);

	int getMaxUserIdleTime();

	void setMaxUserIdleTime(int max);

	boolean isCustomLogin();

	boolean isForceLogout();

	void setForceLogout(boolean value);

	void setCustomLogin(boolean isCustome);

	boolean isGuestUserAllowed();

	void setGuestUserAllowed(boolean allowGuest);

	int getUserCount();

	int getTotalRoomCount();

	int getGameRoomCount();

	Room createRoom(CreateRoomSettings roomSetting) throws QAntCreateRoomException;

	Room createRoom(CreateRoomSettings roomSetting, QAntUser owner) throws QAntCreateRoomException;

	IRoomManager getRoomManager();

	IZoneManager getZoneManager();

	void setZoneManager(IZoneManager zoneManager);

	List<Room> getRoomList();

	Room getRoomById(int id);

	Room getRoomByName(String name);

	void addRoom(Room room) throws QAntTooManyRoomsException;

	void removeRoom(Room room);

	void removeRoom(int id);

	void removeRoom(String name);

	void checkAndRemove(Room room);

	void changeRoomName(Room room, String name) throws QAntRoomException;

	QAntUser getUserById(int userId);

	QAntUser getUserByName(String name);

	QAntUser getUserByChannel(Channel channel);

	Collection<Channel> getChannelList();

	Collection<QAntUser> getUserList();

	void removeAllUsers();

	void removeUser(int id);

	void removeUser(String name);

	void removeUser(Channel channel);

	void removeUser(QAntUser user);

	void removeUserFromRoom(QAntUser user, Room room);

	IQAntExtension getExtension();

	void setExtension(IQAntExtension extension);

	int getMaxFailedLogins();

	void setMaxFailedLogins(int count);

	String getDump();
}
