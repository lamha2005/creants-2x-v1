package com.creants.creants_2x.core.controllers.system;

import com.creants.creants_2x.core.controllers.BaseControllerCommand;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.core.entities.Room;
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

	public CreateRoom() {
		super(SystemRequest.CreateRoom);
	}

	@Override
	public void execute(IRequest request) throws Exception {
		QAntUser user = api.getUserByChannel(request.getSender());
		IQAntObject qanto = request.getContent();

		CreateRoomSettings params = (CreateRoomSettings) preProcess(request);
		Room roomToLeave = null;
		if (qanto.containsKey("r2l")) {
			roomToLeave = user.getZone().getRoomById(qanto.getInt("r2l"));
		}
		Room newRoom = api.createRoom(user.getZone(), params, user, qanto.getBool("aj"), roomToLeave);

		QAntTracer.debug(this.getClass(), newRoom.getDump());
	}

	@Override
	public boolean validate(IRequest request) throws Exception {
		return true;
	}

	@Override
	public Object preProcess(IRequest request) throws Exception {
		IQAntObject qanto = request.getContent();
		String name = qanto.getUtfString("n");
		String groupId = qanto.getUtfString("g");
		String pass = qanto.getUtfString("p");
		boolean isGame = !qanto.isNull("ig") && qanto.getBool("ig") != null && qanto.getBool("ig");
		int maxUsers = qanto.getShort("mu");

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
