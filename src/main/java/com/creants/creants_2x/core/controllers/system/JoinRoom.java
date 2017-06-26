package com.creants.creants_2x.core.controllers.system;

import com.creants.creants_2x.core.controllers.BaseControllerCommand;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.exception.QAntRequestValidationException;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHa
 *
 */
public class JoinRoom extends BaseControllerCommand {
	public static final String KEY_ROOM = "r";
	public static final String KEY_USER_LIST = "ul";
	public static final String KEY_ROOM_NAME = "n";
	public static final String KEYI_ROOM_ID = "i";
	public static final String KEY_PASS = "p";
	public static final String KEYI_ROOM_TO_LEAVE = "rl";
	public static final String KEY_AS_SPECTATOR = "sp";


	public JoinRoom(SystemRequest request) {
		super(SystemRequest.JoinRoom);
	}


	@Override
	public boolean validate(IRequest request) throws Exception {
		IQAntObject content = request.getContent();
		if (!content.containsKey(KEY_ROOM_NAME) && !content.containsKey(KEYI_ROOM_ID)) {
			throw new QAntRequestValidationException("No Room id/name was specified");
		}
		return true;
	}


	@Override
	public void execute(IRequest request) throws Exception {
		QAntUser user = api.getUserByChannel(request.getSender());
		Zone zone = user.getZone();
		IQAntObject qanto = request.getContent();
		Room roomToJoin = null;
		if (qanto.containsKey(KEYI_ROOM_ID)) {
			roomToJoin = zone.getRoomById(qanto.getInt(KEYI_ROOM_ID));
			if (roomToJoin == null) {
				QAntTracer.warn(this.getClass(),
						"Client requested non-existent Room with ID: " + qanto.getInt(KEYI_ROOM_ID));
			}
		} else {
			roomToJoin = zone.getRoomByName(qanto.getUtfString("n"));
			if (roomToJoin == null) {
				QAntTracer.warn(this.getClass(),
						"Client requested non-existent Room with name: " + qanto.getUtfString(KEY_ROOM_NAME));
			}
		}

		Room roomToLeave = qanto.containsKey(KEYI_ROOM_TO_LEAVE) ? zone.getRoomById(qanto.getInt(KEYI_ROOM_TO_LEAVE))
				: user.getLastJoinedRoom();
		String password = qanto.getUtfString(KEY_PASS);
		boolean asSpectator = qanto.containsKey(KEY_AS_SPECTATOR) && qanto.getBool(KEY_AS_SPECTATOR);
		user.updateLastRequestTime();
		api.joinRoom(user, roomToJoin, password, asSpectator, roomToLeave);
	}

}
