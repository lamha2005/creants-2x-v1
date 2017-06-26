package com.creants.creants_2x.core.controllers.system;

import com.creants.creants_2x.core.controllers.BaseControllerCommand;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.exception.QAntRequestValidationException;
import com.creants.creants_2x.core.setting.CreateRoomSettings;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHa
 *
 */
public class CreateRoom extends BaseControllerCommand {
	public static final String KEY_ROOM = "r";
	public static final String KEY_NAME = "n";
	public static final String KEY_GROUP_ID = "g";
	public static final String KEY_PASSWORD = "p";
	public static final String KEY_ISGAME = "ig";
	public static final String KEY_MAX_USERS = "mu";
	public static final String KEY_MAXSPECTATORS = "ms";
	public static final String KEY_MAXVARS = "mv";
	public static final String KEY_ROOMVARS = "rv";
	public static final String KEY_PERMISSIONS = "pm";
	public static final String KEY_EVENTS = "ev";
	public static final String KEY_EXTID = "xn";
	public static final String KEY_EXTCLASS = "xc";
	public static final String KEY_EXTPROP = "xp";
	public static final String KEY_AUTO_JOIN = "aj";
	public static final String KEY_ROOM_TO_LEAVE = "rl";
	public static final String KEY_ALLOW_JOIN_INVITATION_BY_OWNER = "aji";
	public static final String KEY_MMO_DEFAULT_AOI = "maoi";
	public static final String KEY_MMO_MAP_LOW_LIMIT = "mllm";
	public static final String KEY_MMO_MAP_HIGH_LIMIT = "mlhm";
	public static final String KEY_MMO_USER_MAX_LIMBO_SECONDS = "muls";
	public static final String KEY_MMO_PROXIMITY_UPDATE_MILLIS = "mpum";
	public static final String KEY_MMO_SEND_ENTRY_POINT = "msep";


	public CreateRoom() {
		super(SystemRequest.CreateRoom);
	}


	@Override
	public boolean validate(IRequest request) throws Exception {
		IQAntObject content = request.getContent();
		if (content.isNull(KEY_NAME)) {
			throw new QAntRequestValidationException("Room name is missing");
		}
		if (content.isNull(KEY_MAX_USERS)) {
			throw new QAntRequestValidationException("MaxUsers param is missing");
		}
		if (content.isNull(KEY_AUTO_JOIN)) {
			throw new QAntRequestValidationException("AutoJoin param is missing");
		}

		return true;
	}


	@Override
	public void execute(IRequest request) throws Exception {
		QAntUser user = api.getUserByChannel(request.getSender());
		IQAntObject qanto = request.getContent();

		CreateRoomSettings params = (CreateRoomSettings) preProcess(request);
		Room roomToLeave = null;
		if (qanto.containsKey(KEY_ROOM_TO_LEAVE)) {
			roomToLeave = user.getZone().getRoomById(qanto.getInt(KEY_ROOM_TO_LEAVE));
		}

		Room newRoom = api.createRoom(user.getZone(), params, user, qanto.getBool(KEY_AUTO_JOIN), roomToLeave);
		QAntTracer.debug(this.getClass(), newRoom.getDump());
	}


	@Override
	public Object preProcess(IRequest request) throws Exception {
		IQAntObject qanto = request.getContent();
		String name = qanto.getUtfString(KEY_NAME);
		String groupId = qanto.getUtfString(KEY_GROUP_ID);
		String pass = qanto.getUtfString(KEY_PASSWORD);
		boolean isGame = !qanto.isNull(KEY_ISGAME) && qanto.getBool(KEY_ISGAME);
		int maxUsers = qanto.getShort(KEY_MAX_USERS);

		CreateRoomSettings params = new CreateRoomSettings();
		params.setName(name);
		params.setGroupId(groupId);
		params.setPassword(pass);
		params.setGame(isGame);
		params.setMaxUsers(maxUsers);

		QAntUser user = api.getUserByChannel(request.getSender());
		user.updateLastRequestTime();
		return params;
	}

}
