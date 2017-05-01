package com.creants.creants_2x.core.controllers;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.api.IQAntApi;
import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHM
 *
 */
public abstract class BaseControllerCommand implements IControllerCommand {
	protected final QAntServer qant;
	protected final IQAntApi api;
	private short id;


	public BaseControllerCommand(SystemRequest request) {
		System.out.println("*************** DO REQUEST CREATE");
		qant = QAntServer.getInstance();
		api = qant.getAPIManager().getQAntApi();
		id = (short) request.getId();
		System.out.println("--------------------> " + id);
	}


	public short getId() {
		return this.id;
	}


	@Override
	public Object preProcess(IRequest request) throws Exception {
		return null;
	}

}
