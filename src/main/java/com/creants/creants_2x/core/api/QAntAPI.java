package com.creants.creants_2x.core.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.GenericMessageType;
import com.creants.creants_2x.core.IQAntEventParam;
import com.creants.creants_2x.core.QAntEvent;
import com.creants.creants_2x.core.QAntEventParam;
import com.creants.creants_2x.core.QAntEventType;
import com.creants.creants_2x.core.api.response.IResponseApi;
import com.creants.creants_2x.core.api.response.ResponseApi;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.entities.match.MatchExpression;
import com.creants.creants_2x.core.entities.match.MatchingUtils;
import com.creants.creants_2x.core.exception.QAntCreateRoomException;
import com.creants.creants_2x.core.exception.QAntErrorCode;
import com.creants.creants_2x.core.exception.QAntErrorData;
import com.creants.creants_2x.core.exception.QAntJoinRoomException;
import com.creants.creants_2x.core.exception.QAntRuntimeException;
import com.creants.creants_2x.core.service.WebService;
import com.creants.creants_2x.core.setting.CreateRoomSettings;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntArray;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.io.IResponse;
import com.creants.creants_2x.socket.io.Response;
import com.creants.creants_2x.socket.managers.IUserManager;

import io.netty.channel.Channel;
import net.sf.json.JSONObject;

/**
 * @author LamHa
 *
 */
public class QAntAPI implements IQAntApi {
	private static final byte SYSTEM_CONTROLLER = 0;
	private static final String NAME_BY_SERVER = "$FS_NAME_BY_SERVER";
	protected final QAntServer qant;
	protected final IUserManager globalUserManager;
	protected final IResponseApi responseAPI;
	private final MatchingUtils matcher;


	public QAntAPI(QAntServer qant) {
		this.qant = qant;
		globalUserManager = qant.getUserManager();
		responseAPI = new ResponseApi();
		matcher = MatchingUtils.getInstance();
	}


	@Override
	public IResponseApi getResponseAPI() {
		return responseAPI;
	}


	@Override
	public void logout(QAntUser user) {
		if (user == null) {
			QAntTracer.warn(this.getClass(), "Cannot logout null user.");
			return;
		}

		Zone zone = user.getZone();
		List<Room> joinedRooms = user.getJoinedRooms();
		Map<Room, Integer> playerIds = user.getPlayerIds();
		user.setConnected(false);
		zone.removeUser(user);
		globalUserManager.removeUser(user);
		responseAPI.notifyUserLost(user, joinedRooms);
		for (Room r : user.getCreatedRooms()) {
			if (r != null && !joinedRooms.contains(r)) {
				zone.checkAndRemove(r);
			}
		}

		responseAPI.notifyLogout(user.getChannel(), zone.getName());
		Map<IQAntEventParam, Object> evtParams = new HashMap<IQAntEventParam, Object>();
		evtParams.put(QAntEventParam.ZONE, zone);
		evtParams.put(QAntEventParam.USER, user);
		evtParams.put(QAntEventParam.JOINED_ROOMS, joinedRooms);
		evtParams.put(QAntEventParam.PLAYER_IDS_BY_ROOM, playerIds);
		qant.getEventManager().dispatchEvent(new QAntEvent(QAntEventType.USER_LOGOUT, evtParams));

		QAntTracer.info(this.getClass(), String.format("User logout: %s, %s, SessionLen: %s", user.getZone().toString(),
				user.toString(), System.currentTimeMillis() - user.getLoginTime()));
	}


	@Override
	public QAntUser login(Channel sender, String token, String zoneName, IQAntObject params) {
		return login(sender, token, zoneName, params, false);
	}


	@Override
	public QAntUser login(Channel sender, String token, String zoneName, IQAntObject outParams, boolean forceLogout) {
		if (!qant.getChannelManager().containsChannel(sender)) {
			QAntTracer.warn(this.getClass(), "Login failed: " + token + " , channel is already expired!");
			return null;
		}

		IQAntObject resObj = QAntObject.newInstance();
		IResponse response = new Response();
		response.setId(SystemRequest.Login.getId());
		response.setTargetController(SYSTEM_CONTROLLER);
		response.setContent(resObj);
		response.setRecipients(sender);

		Zone zone = qant.getZoneManager().getZoneByName(zoneName);
		if (zone == null) {
			sendError(zoneName, response, QAntErrorCode.LOGIN_BAD_ZONENAME);
			QAntTracer.info(this.getClass(),
					"Bad login request, Zone: " + zoneName + " does not exist. Requested by: " + sender);
			return null;
		}

		boolean dc = qant.getUserManager().getUserByChannel(sender) != null;
		if (dc) {
			sendError(zoneName, response, QAntErrorCode.LOGIN_ALREADY_LOGGED);
			QAntTracer.info(this.getClass(), "Bad login request, Zone: " + zoneName + ", reason: "
					+ QAntErrorCode.LOGIN_ALREADY_LOGGED.getName() + ". Requested by: " + sender);
			return null;
		}

		QAntUser user = new QAntUser(sender);
		user.updateLastRequestTime();
		user.setConnected(true);
		String verify = null;
		try {
			verify = WebService.getInstance().verify(token);
		} catch (Exception e) {
			sendError(zoneName, response, QAntErrorCode.GRAPH_API_FAIL);
			QAntTracer.info(this.getClass(), "Bad login request, Zone: " + zoneName + ", reason:"
					+ QAntErrorCode.GRAPH_API_FAIL.getName() + ". Requested by: " + sender);
			return null;
		}

		JSONObject jo = JSONObject.fromObject(verify);
		int code = jo.getInt("code");
		if (code != 1) {
			sendError(zoneName, response, QAntErrorCode.TOKEN_EXPIRED);
			QAntTracer.debug(this.getClass(), "Bad login request, Zone: " + zoneName + ", reason:"
					+ QAntErrorCode.TOKEN_EXPIRED.getName() + ". Requested by: " + sender + "/token: " + token);
			return null;
		}
		JSONObject userInfo = jo.getJSONObject("data");
		long userId = userInfo.getLong("userId");
		user.setCreantsUserId(userId);

		Boolean nameByServer = outParams.getBool(NAME_BY_SERVER);
		if (nameByServer) {
			user.setName(zoneName + "#" + userId);
		} else {
			if (outParams != null) {
				String newUserName = outParams.getUtfString("$FS_NEW_LOGIN_NAME");
				if (newUserName != null) {
					user.setName(newUserName);
				}
			}

			if (user.getName() == null) {
				user.setName("user_" + userId);
			}
		}

		zone.login(user);

		resObj.putUtfString("tk", token);
		resObj.putUtfString("fn", userInfo.getString("fullName"));
		resObj.putUtfString("avt", userInfo.getString("avatar"));
		resObj.putUtfString("un", user.getName());
		resObj.putLong("mn", userInfo.getLong("money"));
		resObj.putLong("uid", userId);
		resObj.putQAntObject("p", outParams);
		response.write();
		user.setAvatar(userInfo.getString("avatar"));

		Map<IQAntEventParam, Object> evtParams = new HashMap<IQAntEventParam, Object>();
		evtParams.put(QAntEventParam.ZONE, zone);
		evtParams.put(QAntEventParam.USER, user);
		qant.getEventManager().dispatchEvent(new QAntEvent(QAntEventType.USER_JOIN_ZONE, evtParams));
		return user;
	}


	private void sendError(String zoneName, IResponse response, QAntErrorCode errCode) {
		IQAntObject resObj = QAntObject.newInstance();
		resObj.putShort("ec", errCode.getId());
		resObj.putUtfString("ep", zoneName);
		resObj.putUtfString("rs", errCode.getName());
		response.write();
	}


	@Override
	public void kickUser(QAntUser owner, QAntUser kickedUser, String paramString, int paramInt) {

	}


	@Override
	public void disconnectUser(QAntUser user) {
		if (user == null) {
			throw new QAntRuntimeException("Cannot disconnect user, User object is null.");
		}

		Channel channel = user.getChannel();
		Zone zone = user.getZone();
		List<Room> joinedRooms = user.getJoinedRooms();
		Map<Room, Integer> playerIds = user.getPlayerIds();

		if (channel.isOpen()) {
			channel.close();
		}

		user.setConnected(false);
		if (zone != null) {
			zone.removeUser(user);
		}

		globalUserManager.removeUser(user);
		responseAPI.notifyUserLost(user, joinedRooms);

		Map<IQAntEventParam, Object> evtParams = new HashMap<IQAntEventParam, Object>();
		evtParams.put(QAntEventParam.ZONE, zone);
		evtParams.put(QAntEventParam.USER, user);
		evtParams.put(QAntEventParam.JOINED_ROOMS, joinedRooms);
		evtParams.put(QAntEventParam.PLAYER_IDS_BY_ROOM, playerIds);
		qant.getEventManager().dispatchEvent(new QAntEvent(QAntEventType.USER_DISCONNECT, evtParams));
		List<Room> createdRooms = user.getCreatedRooms();
		if (createdRooms != null && createdRooms.size() > 0) {
			for (Room r : createdRooms) {
				if (r != null && !joinedRooms.contains(r)) {
					zone.checkAndRemove(r);
				}
			}
		}

		QAntTracer.info(this.getClass(), String.format("User disconnected: %s, %s, SessionLen: %s",
				user.getZone().toString(), user.toString(), System.currentTimeMillis() - user.getLoginTime()));

	}


	@Override
	public void disconnect(Channel channel) {
		if (channel == null) {
			QAntTracer.warn(this.getClass(), "Unexpected, cannot disconnect session. Session object is null.");
			return;
		}

		QAntUser lostUser = globalUserManager.getUserByChannel(channel);
		if (lostUser != null) {
			disconnectUser(lostUser);
		} else if (channel.isOpen()) {
			channel.deregister();
			channel.disconnect();
			channel.close();
		}

	}


	@Override
	public QAntUser getUserById(int userId) {
		return globalUserManager.getUserById(userId);
	}


	@Override
	public QAntUser getUserByName(String name) {
		return globalUserManager.getUserByName(name);
	}


	@Override
	public QAntUser getUserByChannel(Channel channel) {
		return globalUserManager.getUserByChannel(channel);
	}


	@Override
	public Room createRoom(Zone zone, CreateRoomSettings roomSetting, QAntUser owner) throws QAntCreateRoomException {
		return createRoom(zone, roomSetting, owner, false, null, true, true);
	}


	@Override
	public Room createRoom(Zone zone, CreateRoomSettings roomSetting, QAntUser owner, boolean joinIt, Room roomToLeave)
			throws QAntCreateRoomException {
		return createRoom(zone, roomSetting, owner, joinIt, roomToLeave, true, true);
	}


	@Override
	public Room createRoom(Zone zone, CreateRoomSettings roomSetting, QAntUser owner, boolean joinIt, Room roomToLeave,
			boolean fireClientEvent, boolean fireServerEvent) throws QAntCreateRoomException {
		Room theRoom = null;
		try {
			String groupId = roomSetting.getGroupId();
			if (groupId == null || groupId.length() == 0) {
				roomSetting.setGroupId("default");
			}

			theRoom = zone.createRoom(roomSetting, owner);
			if (owner != null) {
				owner.addCreatedRoom(theRoom);
				owner.updateLastRequestTime();
			}

			if (fireClientEvent) {
				responseAPI.notifyRoomAdded(theRoom);
			}

			if (fireServerEvent) {
				Map<IQAntEventParam, Object> eventParams = new HashMap<IQAntEventParam, Object>();
				eventParams.put(QAntEventParam.ZONE, zone);
				eventParams.put(QAntEventParam.ROOM, theRoom);
				qant.getEventManager().dispatchEvent(new QAntEvent(QAntEventType.ROOM_ADDED, eventParams));
			}

		} catch (QAntCreateRoomException err) {
			if (fireClientEvent) {
				responseAPI.notifyRequestError(err, owner, SystemRequest.CreateRoom);
			}

			throw new QAntCreateRoomException(
					String.format("Room creation error. %s, %s, %s", err.getMessage(), zone, owner));
		}

		if (theRoom != null && owner != null && joinIt) {
			try {
				joinRoom(owner, theRoom, theRoom.getPassword(), false, roomToLeave, true, true);
			} catch (QAntJoinRoomException e) {
				QAntTracer.warn(this.getClass(),
						"Unable to join the just created Room: " + theRoom + ", reason: " + e.getMessage());
			}
		}

		return theRoom;
	}


	@Override
	public void joinRoom(QAntUser user, Room room) throws QAntJoinRoomException {
		joinRoom(user, room, "", false, user.getLastJoinedRoom());
	}


	@Override
	public void joinRoom(QAntUser user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave)
			throws QAntJoinRoomException {

		joinRoom(user, roomToJoin, password, asSpectator, roomToLeave, true, true);
	}


	@Override
	public void joinRoom(QAntUser user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave,
			boolean fireClientEvent, boolean fireServerEvent) throws QAntJoinRoomException {

		try {
			if (user.isJoining()) {
				throw new RuntimeException("Join request discarded. User is already in a join transaction: " + user);
			}

			user.setJoining(true);
			if (roomToJoin == null) {
				throw new QAntJoinRoomException("Requested room doesn't exist",
						new QAntErrorData(QAntErrorCode.JOIN_BAD_ROOM));
			}
			if (!roomToJoin.isActive()) {
				String message = String.format("Room is currently locked, %s", roomToJoin);
				QAntErrorData errData = new QAntErrorData(QAntErrorCode.JOIN_ROOM_LOCKED);
				errData.addParameter(roomToJoin.getName());
				throw new QAntJoinRoomException(message, errData);
			}

			boolean doorIsOpen = true;
			if (roomToJoin.isPasswordProtected()) {
				doorIsOpen = roomToJoin.getPassword().equals(password);
			}

			if (!doorIsOpen) {
				QAntErrorData data = new QAntErrorData(QAntErrorCode.JOIN_BAD_PASSWORD);
				data.addParameter(roomToJoin.getName());
				throw new QAntJoinRoomException(String.format("Room password is wrong, %s", roomToJoin), data);
			}

			roomToJoin.addUser(user, asSpectator);
			user.updateLastRequestTime();
			if (fireClientEvent) {
				responseAPI.notifyJoinRoomSuccess(user, roomToJoin);
				// TODO Báo cho các user khác user này join room, kèm theo các
				// tham
				// số là gì, nên customize lại trong extension
				// responseAPI.notifyUserEnterRoom(user, roomToJoin);

				// TODO báo trong group đó có bao nhiêu người chơi
				// responseAPI.notifyUserCountChange(user.getZone(),
				// roomToJoin);
			}

			if (fireServerEvent) {
				Map<IQAntEventParam, Object> evtParams = new HashMap<IQAntEventParam, Object>();
				evtParams.put(QAntEventParam.ZONE, user.getZone());
				evtParams.put(QAntEventParam.ROOM, roomToJoin);
				evtParams.put(QAntEventParam.USER, user);
				qant.getEventManager().dispatchEvent(new QAntEvent(QAntEventType.USER_JOIN_ROOM, evtParams));
			}

			if (roomToLeave != null) {
				leaveRoom(user, roomToLeave);
			}
		} catch (QAntJoinRoomException err) {
			if (fireClientEvent) {
				responseAPI.notifyRequestError(err, user, SystemRequest.JoinRoom);
			}

			throw new QAntJoinRoomException(String.format("Join Error - %s", err.getMessage()));
		} finally {
			user.setJoining(false);
		}
		user.setJoining(false);
	}


	@Override
	public void leaveRoom(QAntUser user, Room room) {
		leaveRoom(user, room, true, true);
	}


	@Override
	public void leaveRoom(QAntUser user, Room room, boolean fireClientEvent, boolean fireServerEvent) {
		if (room == null) {
			room = user.getLastJoinedRoom();
			if (room == null) {
				QAntTracer.warn(this.getClass(), "LeaveRoom failed: user is not joined in any room. " + user);
				return;
			}
		}

		if (!room.containsUser(user)) {
			return;
		}

		Zone zone = user.getZone();
		int playerId = user.getPlayerId(room);
		user.updateLastRequestTime();
		if (fireClientEvent) {
			// responseAPI.notifyUserExitRoom(user, room,
			// room.isFlagSet(SFSRoomSettings.USER_EXIT_EVENT));
		}

		zone.removeUserFromRoom(user, room);
		boolean roomWasRemoved = zone.getRoomById(room.getId()) == null;
		if (!roomWasRemoved && room.isActive()) {
			// TODO báo tất cả user trong group room đã được xóa
			// responseAPI.notifyUserCountChange(user.getZone(), room);
		}

		if (fireServerEvent) {
			Map<IQAntEventParam, Object> evtParams = new HashMap<IQAntEventParam, Object>();
			evtParams.put(QAntEventParam.ZONE, user.getZone());
			evtParams.put(QAntEventParam.ROOM, room);
			evtParams.put(QAntEventParam.USER, user);
			evtParams.put(QAntEventParam.PLAYER_ID, playerId);
			qant.getEventManager().dispatchEvent(new QAntEvent(QAntEventType.USER_LEAVE_ROOM, evtParams));
		}
	}


	@Override
	public void removeRoom(Room room) {
		removeRoom(room, true, true);
	}


	@Override
	public void removeRoom(Room room, boolean fireClientEvent, boolean fireServerEvent) {
		room.getZone().removeRoom(room);
		if (room.getOwner() != null) {
			room.getOwner().removeCreatedRoom(room);
		}

		if (fireClientEvent) {
			// responseAPI.notifyRoomRemoved(room);
		}

		if (fireServerEvent) {
			Map<IQAntEventParam, Object> evtParams = new HashMap<IQAntEventParam, Object>();
			evtParams.put(QAntEventParam.ZONE, room.getZone());
			evtParams.put(QAntEventParam.ROOM, room);
			qant.getEventManager().dispatchEvent(new QAntEvent(QAntEventType.ROOM_REMOVED, evtParams));
		}
	}


	@Override
	public void sendPublicMessage(Room targetRoom, QAntUser sender, String message, IQAntObject param) {
		if (targetRoom == null) {
			throw new IllegalArgumentException("The target Room is null");
		}

		if (!sender.isJoinedInRoom(targetRoom)) {
			throw new IllegalStateException("Sender " + sender + " is not joined the target room " + targetRoom);
		}

		if (message.length() == 0) {
			QAntTracer.warn(this.getClass(), "Empty public message request (len == 0) discarded, sender: " + sender);
			return;
		}

		Map<IQAntEventParam, Object> evtParams = new HashMap<IQAntEventParam, Object>();
		evtParams.put(QAntEventParam.ZONE, sender.getZone());
		evtParams.put(QAntEventParam.ROOM, targetRoom);
		evtParams.put(QAntEventParam.USER, sender);
		evtParams.put(QAntEventParam.MESSAGE, message);
		evtParams.put(QAntEventParam.OBJECT, param);
		qant.getEventManager().dispatchEvent(new QAntEvent(QAntEventType.PUBLIC_MESSAGE, evtParams));

		List<Channel> recipients = getPublicMessageRecipientList(sender, targetRoom);
		if (recipients != null && !recipients.isEmpty()) {
			sendGenericMessage(GenericMessageType.PUBLIC_MSG, sender, targetRoom.getId(), message, param, recipients,
					null);
		}
	}


	private List<Channel> getPublicMessageRecipientList(QAntUser sender, Room targetRoom) {
		return targetRoom.getChannelList();
	}


	@Override
	public void sendPrivateMessage(QAntUser sender, QAntUser recipient, String message, IQAntObject param) {
		if (sender == null) {
			throw new IllegalArgumentException("PM sender is null.");
		}
		if (recipient == null) {
			throw new IllegalArgumentException("PM recipient is null");
		}
		if (sender == recipient) {
			throw new IllegalStateException("PM sender and receiver are the same. Why?");
		}
		if (message.length() == 0) {
			QAntTracer.info(this.getClass(), "Empty private message request (len == 0) discarded");
			return;
		}

		Zone zone = sender.getZone();
		Map<IQAntEventParam, Object> evtParams = new HashMap<IQAntEventParam, Object>();
		evtParams.put(QAntEventParam.ZONE, zone);
		evtParams.put(QAntEventParam.USER, sender);
		evtParams.put(QAntEventParam.RECIPIENT, recipient);
		evtParams.put(QAntEventParam.MESSAGE, message);
		evtParams.put(QAntEventParam.OBJECT, param);
		qant.getEventManager().dispatchEvent(new QAntEvent(QAntEventType.PRIVATE_MESSAGE, evtParams));

		final List<Channel> messageRecipients = new ArrayList<Channel>();
		messageRecipients.add(recipient.getChannel());
		messageRecipients.add(sender.getChannel());
		sendGenericMessage(GenericMessageType.PRIVATE_MSG, sender, -1, message, param, messageRecipients, null);
	}


	/**
	 * @param type
	 * @param sender
	 * @param targetRoomId
	 * @param message
	 * @param params
	 * @param recipients
	 * @param senderData
	 *            thông tin của user như name, avatar...
	 */
	private void sendGenericMessage(GenericMessageType type, QAntUser sender, int targetRoomId, String message,
			IQAntObject params, Collection<Channel> recipients, IQAntArray senderData) {

		if (sender != null) {
			sender.updateLastRequestTime();
		}

		IQAntObject resObj = QAntObject.newInstance();
		IResponse response = (IResponse) new Response();
		response.setId(SystemRequest.GenericMessage.getId());
		response.setContent(resObj);
		response.setRecipients(recipients);
		resObj.putByte("t", (byte) type.getId());
		resObj.putInt("r", targetRoomId);
		resObj.putInt("u", sender.getUserId());
		if (message != null) {
			resObj.putUtfString("m", message);
		}

		if (params != null) {
			resObj.putQAntObject("p", params);
		}
		if (senderData != null) {
			resObj.putQAntArray("sd", senderData);
		}

		response.write();
	}


	@Override
	public void sendExtensionResponse(String cmdName, IQAntObject params, List<QAntUser> recipients, Room room) {
		final List<Channel> channels = new LinkedList<Channel>();
		for (QAntUser user : recipients) {
			channels.add(user.getChannel());
		}

		responseAPI.sendExtResponse(cmdName, params, channels, room);
	}


	@Override
	public void sendExtensionResponse(String cmdName, IQAntObject params, QAntUser recipient, Room room) {
		List<Channel> msgRecipients = new LinkedList<Channel>();
		msgRecipients.add(recipient.getChannel());

		responseAPI.sendExtResponse(cmdName, params, msgRecipients, room);
	}


	@Override
	public List<Room> findRooms(Collection<Room> roomList, MatchExpression expression, int limit) {
		return matcher.matchRooms(roomList, expression, limit);
	}


	@Override
	public List<QAntUser> findUsers(Collection<QAntUser> userList, MatchExpression expression, int limit) {
		return this.matcher.matchUsers(userList, expression, limit);
	}

}