package com.creants.creants_2x.core.controllers.system;

import java.util.HashMap;
import java.util.Map;

import com.creants.creants_2x.core.IQAntEventParam;
import com.creants.creants_2x.core.QAntEventParam;
import com.creants.creants_2x.core.QAntEventSysParam;
import com.creants.creants_2x.core.QAntEventType;
import com.creants.creants_2x.core.QAntSystemEvent;
import com.creants.creants_2x.core.controllers.BaseControllerCommand;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.exception.QAntRequestValidationException;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHM
 *
 */
public class Login extends BaseControllerCommand {
	private static final String TOKEN = "tk";
	private static final String ZONE_NAME = "zn";
	private static final String REQUEST_LOGIN_DATA_OUT = "$FS_REQUEST_LOGIN_DATA_OUT";

	public Login() {
		super(SystemRequest.Login);
	}

	@Override
	public void execute(IRequest request) throws Exception {
		System.out.println("************** execute system login");
		IQAntObject reqObj = request.getContent();
		String token = reqObj.getUtfString(TOKEN);
		String zoneName = reqObj.getUtfString(ZONE_NAME);
		if (zoneName == null)
			zoneName = "MuFantasy";

		IQAntObject params = (IQAntObject) request.getAttribute(REQUEST_LOGIN_DATA_OUT);
		api.login(request.getSender(), token, zoneName, params);
	}

	@Override
	public boolean validate(IRequest request) throws Exception {
		IQAntObject params = request.getContent();
		if (params == null || !params.containsKey(TOKEN))
			return false;

		String zoneName = params.getUtfString(ZONE_NAME);
		if (zoneName == null)
			zoneName = "MuFantasy";
		Zone zone = qant.getZoneManager().getZoneByName(zoneName);
		return customLogin(params, request, zone);
	}

	protected boolean customLogin(IQAntObject param, IRequest request, Zone zone)
			throws QAntRequestValidationException {

		boolean res = true;
		if (zone != null && zone.isCustomLogin()) {
			if (zone.getExtension() == null) {
				throw new QAntRequestValidationException(
						"Custom login is ON but no Extension is active for this zone: " + zone.getName());
			}

			Map<IQAntEventParam, Object> sysParams = new HashMap<IQAntEventParam, Object>();
			sysParams.put(QAntEventSysParam.NEXT_COMMAND, Login.class);
			sysParams.put(QAntEventSysParam.REQUEST_OBJ, request);

			Map<IQAntEventParam, Object> userParams = new HashMap<IQAntEventParam, Object>();
			userParams.put(QAntEventParam.ZONE, zone);
			userParams.put(QAntEventParam.SESSION, request.getSender());
			userParams.put(QAntEventParam.LOGIN_IN_DATA, param.getQAntObject("p"));

			IQAntObject paramsOut = QAntObject.newInstance();
			request.setAttribute(REQUEST_LOGIN_DATA_OUT, paramsOut);
			userParams.put(QAntEventParam.LOGIN_OUT_DATA, paramsOut);

			qant.getEventManager().dispatchEvent(new QAntSystemEvent(QAntEventType.USER_LOGIN, userParams, sysParams));
			res = false;
		}
		return res;
	}

	@Override
	public Object preProcess(IRequest request) throws Exception {
		return null;
	}

}
