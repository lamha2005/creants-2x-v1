package com.creants.creants_2x.core.controllers.system;

import com.creants.creants_2x.core.controllers.BaseControllerCommand;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHa
 *
 */
public class JoinRoom extends BaseControllerCommand {
	public static final String KEY_ROOM = "r";
	public static final String KEY_USER_LIST = "ul";
	public static final String KEY_ROOM_NAME = "n";
	public static final String KEY_ROOM_ID = "i";
	public static final String KEY_PASS = "p";
	public static final String KEY_ROOM_TO_LEAVE = "rl";
	public static final String KEY_AS_SPECTATOR = "sp";

	public JoinRoom(SystemRequest request) {
		super(SystemRequest.JoinRoom);
	}

	@Override
	public void execute(IRequest request) throws Exception {
		
	}

	@Override
	public boolean validate(IRequest request) throws Exception {
		return true;
	}

}
