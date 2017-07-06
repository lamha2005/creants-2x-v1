package com.creants.creants_2x.core.controllers.system;

import com.creants.creants_2x.core.controllers.BaseControllerCommand;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHM
 *
 */
public class Logout extends BaseControllerCommand {
	public static final String KEY_ZONE_NAME = "zn";


	public Logout() {
		super(SystemRequest.Logout);
	}


	@Override
	public boolean validate(IRequest request) throws Exception {
		return true;
	}


	@Override
	public void execute(IRequest request) throws Exception {
		QAntUser sender = api.getUserByChannel(request.getSender());
		if (sender == null) {
			throw new IllegalArgumentException("Logout failure. Session is not logged in: " + request.getSender());
		}
		api.logout(sender);
	}

}
