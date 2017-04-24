package com.creants.creants_2x.core.controllers.system;

import com.creants.creants_2x.core.controllers.BaseControllerCommand;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.io.IRequest;
import com.creants.creants_2x.socket.io.Response;

/**
 * @author LamHM
 *
 */
public class Login extends BaseControllerCommand {
	private static final String TOKEN = "tk";


	public Login() {
		super(SystemRequest.Login);
	}


	@Override
	public void execute(IRequest request) throws Exception {
		IQAntObject param = request.getContent();
		String token = param.getUtfString(TOKEN);
		Response response = new Response();
		response.setRecipients(request.getSender());
		response.setId(getId());

		param = QAntObject.newInstance();
		response.setContent(param);
		param.putUtfString(TOKEN, token);
		response.write();
	}


	@Override
	public boolean validate(IRequest request) throws Exception {
		IQAntObject param = request.getContent();
		if (param == null || !param.containsKey(TOKEN))
			return false;

		return true;
	}


	@Override
	public Object preProcess(IRequest request) throws Exception {
		return null;
	}

}
