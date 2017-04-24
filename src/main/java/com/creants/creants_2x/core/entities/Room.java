package com.creants.creants_2x.core.entities;

import java.util.List;

import com.creants.creants_2x.core.exception.QAntJoinRoomException;
import com.creants.creants_2x.core.extension.IQAntExtension;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.managers.IUserManager;

import io.netty.channel.Channel;

/**
 * @author LamHM
 *
 */
public interface Room {
	int getId();

	Zone getZone();

	String getGroupId();

	void setGroupId(String groupId);

	void setZone(Zone zone);;

	String getName();

	void setName(String name);

	String getPassword();

	void setPassword(String password);

	boolean isPasswordProtected();

	boolean isPublic();

	int getCapacity();

	void setCapacity(int playerSize, int spectatorSize);

	int getMaxUsers();

	void setMaxUsers(int maxUser);

	int getMaxSpectators();

	void setMaxSpectators(int maxSpectator);

	int getMaxRoomVariablesAllowed();

	void setMaxRoomVariablesAllowed(int value);

	QAntUser getOwner();

	void setOwner(QAntUser user);

	RoomSize getSize();

	IUserManager getUserManager();

	void setUserManager(IUserManager userManager);

	boolean isGame();

	boolean isHidden();

	boolean isEmpty();

	boolean isFull();

	boolean isActive();

	void setActive(boolean isActive);

	IQAntExtension getExtension();

	void setExtension(IQAntExtension extension);

	QAntUser getUserById(int id);

	QAntUser getUserByName(String name);

	QAntUser getUserByChannel(Channel channel);

	QAntUser getUserByPlayerId(int playerId);

	List<QAntUser> getUserList();

	List<QAntUser> getPlayersList();

	List<QAntUser> getSpectatorsList();

	List<Channel> getChannelList();

	void addUser(QAntUser user, boolean isSpectator) throws QAntJoinRoomException;

	void addUser(QAntUser user) throws QAntJoinRoomException;

	void removeUser(QAntUser user);

	boolean containsUser(QAntUser user);

	boolean containsUser(String name);

	String getDump();

	void destroy();

	void setGame(boolean game);

	void setHidden(boolean hidden);

}
