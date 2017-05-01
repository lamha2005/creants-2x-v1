package com.creants.creants_2x.core.controllers.system;

import com.creants.creants_2x.core.controllers.BaseControllerCommand;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHa
 *
 */
public class BanUser extends BaseControllerCommand {

	public BanUser(SystemRequest request) {
		super(request);
	}

	@Override
	public void execute(IRequest request) throws Exception {

	}

	@Override
	public boolean validate(IRequest request) throws Exception {
		return true;
	}

	@Override
	public Object preProcess(IRequest request) throws Exception {
		return null;
	}

}
