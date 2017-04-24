package com.creants.creants_2x.core.api;

import java.util.Collection;
import java.util.List;

import com.creants.creants_2x.core.api.response.IResponseApi;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.entities.match.MatchExpression;
import com.creants.creants_2x.core.exception.QAntCreateRoomException;
import com.creants.creants_2x.core.exception.QAntJoinRoomException;
import com.creants.creants_2x.core.setting.CreateRoomSettings;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

import io.netty.channel.Channel;

/**
 * @author LamHa
 *
 */
public interface IQAntApi {
	IResponseApi getResponseAPI();

	/**
	 * User thực hiện logout
	 * 
	 * @param user
	 */
	void logout(QAntUser user);

	/**
	 * Thực hiện login theo token
	 * 
	 * @param user
	 */

	QAntUser login(Channel channel, String token, IQAntObject param);

	QAntUser login(Channel channel, String token, IQAntObject param, boolean forceLogout);

	/**
	 * Kích người chơi khỏi bàn
	 * 
	 * @param owner
	 *            chủ bàn
	 * @param kickedUser
	 *            user bị kick
	 * @param paramString
	 * @param paramInt
	 */
	void kickUser(QAntUser owner, QAntUser kickedUser, String paramString, int paramInt);

	void disconnectUser(QAntUser user);

	void disconnect(Channel channel);

	QAntUser getUserById(int userId);

	QAntUser getUserByName(String name);

	QAntUser getUserByChannel(Channel channel);

	Room createRoom(Zone zone, CreateRoomSettings roomSetting, QAntUser user) throws QAntCreateRoomException;

	Room createRoom(Zone zone, CreateRoomSettings roomSetting, QAntUser user, boolean joinIt, Room roomToLeave)
			throws QAntCreateRoomException;

	Room createRoom(Zone zone, CreateRoomSettings roomSetting, QAntUser user, boolean joinIt, Room roomToLeave,
			boolean fireClientEvent, boolean fireServerEvent) throws QAntCreateRoomException;

	void joinRoom(QAntUser user, Room room) throws QAntJoinRoomException;

	void joinRoom(QAntUser user, Room room, String password, boolean asSpectator, Room roomToLeave)
			throws QAntJoinRoomException;

	void joinRoom(QAntUser user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave,
			boolean fireClientEvent, boolean fireServerEvent) throws QAntJoinRoomException;

	void leaveRoom(QAntUser user, Room room);

	void leaveRoom(QAntUser user, Room room, boolean fireClientEvent, boolean fireServerEvent);

	void removeRoom(Room room);

	void removeRoom(Room room, boolean fireClientEvent, boolean fireServerEvent);

	List<QAntUser> findUsers(Collection<QAntUser> users, MatchExpression expr, int id);

	List<Room> findRooms(Collection<Room> room, MatchExpression expr, int id);

	void sendPublicMessage(Room room, QAntUser user, String message, IQAntObject param);

	void sendPrivateMessage(QAntUser sender, QAntUser receiver, String message, IQAntObject param);

	void sendExtensionResponse(String cmdName, IQAntObject message, List<QAntUser> recipients, Room room);

	void sendExtensionResponse(String cmdName, IQAntObject message, QAntUser recipient, Room room);
}
