package com.creants.creants_2x.core.managers;

import java.util.List;

import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.exception.QAntCreateRoomException;
import com.creants.creants_2x.core.exception.QAntRoomException;
import com.creants.creants_2x.core.setting.CreateRoomSettings;
import com.creants.creants_2x.core.util.IPlayerIdGenerator;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHM
 *
 */
public interface IRoomManager {
	Room createRoom(CreateRoomSettings params) throws QAntCreateRoomException;

	Room createRoom(CreateRoomSettings params, QAntUser owner) throws QAntCreateRoomException;

	Class<? extends IPlayerIdGenerator> getDefaultRoomPlayerIdGenerator();

	void setDefaultRoomPlayerIdGeneratorClass(Class<? extends IPlayerIdGenerator> customIdGeneratorClass);

	void addGroup(String groupId);

	void addRoom(Room room);

	boolean containsGroup(String groupId);

	List<String> getGroups();

	Room getRoomById(int id);

	Room getRoomByName(String name);

	List<Room> getRoomList();

	List<Room> getRoomListFromGroup(String groupId);

	int getGameRoomCount();

	int getTotalRoomCount();

	void removeGroup(String groupId);

	void removeRoom(int roomId);

	void removeRoom(String name);

	void removeRoom(Room room);

	boolean containsRoom(int id, String groupId);

	boolean containsRoom(int id);

	boolean containsRoom(Room room, String groupId);

	boolean containsRoom(Room room);

	boolean containsRoom(String name, String groupId);

	boolean containsRoom(String name);

	Zone getOwnerZone();

	void setOwnerZone(Zone zone);

	void removeUser(QAntUser user);

	void removeUser(QAntUser user, Room room);

	void checkAndRemove(Room room);

	void changeRoomName(Room room, String newName) throws QAntRoomException;

	void changeRoomPasswordState(Room room, String password);

	void changeRoomCapacity(Room room, int newMaxUsers, int newMaxSpect);
}
