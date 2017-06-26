package com.creants.creants_2x.core.controllers.system;

import com.creants.creants_2x.core.controllers.BaseControllerCommand;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHM
 *
 */
public class LeaveRoom extends BaseControllerCommand {

	public static final String KEY_ROOM_ID = "r";


	public LeaveRoom(SystemRequest request) {
		super(SystemRequest.LeaveRoom);
	}


	@Override
	public boolean validate(IRequest request) throws Exception {
		return true;
	}


	@Override
	public void execute(IRequest request) throws Exception {
		QAntUser user = api.getUserByChannel(request.getSender());
		Zone zone = user.getZone();
		IQAntObject reqObj = request.getContent();
		int roomId = -1;
		if (reqObj.containsKey(KEY_ROOM_ID)) {
			roomId = reqObj.getInt(KEY_ROOM_ID);
		}
		Room theRoom = (roomId >= 0) ? zone.getRoomById(roomId) : null;
		api.leaveRoom(user, theRoom, true, true);
	}

}
