package com.creants.creants_2x.core.controllers.system;

import com.creants.creants_2x.core.controllers.BaseControllerCommand;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.core.service.WebService;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.io.IRequest;
import com.creants.creants_2x.socket.io.Response;

import net.sf.json.JSONObject;

/**
 * @author LamHM
 *
 */
public class Login extends BaseControllerCommand {
	private static final String TOKEN = "tk";
	private static final byte SYSTEM_CONTROLLER = 0;


	public Login() {
		super(SystemRequest.Login);
	}


	@Override
	public void execute(IRequest request) throws Exception {
		IQAntObject params = request.getContent();
		String token = params.getUtfString(TOKEN);
		Response response = new Response();
		response.setTargetController(SYSTEM_CONTROLLER);
		response.setId(getId());
		response.setRecipients(request.getSender());
		
		String verify = WebService.getInstance().verify(token);
		JSONObject jo = JSONObject.fromObject(verify);
		JSONObject userInfo = jo.getJSONObject("data");
		params = QAntObject.newInstance();
		params.putUtfString(TOKEN, token);
		params.putUtfString("fn", userInfo.getString("fullName"));
		params.putUtfString("avt", userInfo.getString("avatar"));
		params.putLong("mn", userInfo.getLong("money"));
		params.putLong("uid", userInfo.getLong("userId"));
		
		
		response.setContent(params);
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
