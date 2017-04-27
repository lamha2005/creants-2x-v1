package com.creants.creants_2x.core.event.handler;

import java.util.List;

import com.creants.creants_2x.core.api.IQAntApi;
import com.creants.creants_2x.socket.gate.IChannelService;
import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * Lớp trừu tượng của một RequestHandler
 * 
 * @author LamHa
 *
 */
public abstract class AbstractRequestHandler implements IRequestHandler {
	protected IChannelService channelService;
	protected IQAntApi coreApi;


	public AbstractRequestHandler() {
		initialize();
	}


	protected void writeMessage(QAntUser user, QAntObject message) {
	}


	protected void writeMessage(List<QAntUser> users, QAntObject message) {
	}


	protected void writeErrorMessage(QAntUser user, String errorCmdId, short errorCode, String errorMessage) {
	}


	public IChannelService getChannelService() {
		return channelService;
	}


	public void setCoreApi(IQAntApi coreApi) {
		this.coreApi = coreApi;
	}

}
