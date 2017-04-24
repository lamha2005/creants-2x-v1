package com.creants.creants_2x.core.event.handler;

import java.util.List;

import com.creants.creants_2x.core.api.IQAntApi;
import com.creants.creants_2x.core.util.DefaultMessageFactory;
import com.creants.creants_2x.socket.gate.IChannelService;
import com.creants.creants_2x.socket.gate.IMessageWriter;
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
	private IMessageWriter messageWriter;
	protected IQAntApi coreApi;


	public AbstractRequestHandler() {
		initialize();
	}


	protected void writeMessage(QAntUser user, QAntObject message) {
		messageWriter.writeMessage(user, message);
	}


	protected void writeMessage(List<QAntUser> users, QAntObject message) {
		messageWriter.writeMessage(users, message);
	}


	protected void writeErrorMessage(QAntUser user, String errorCmdId, short errorCode, String errorMessage) {
		messageWriter.writeMessage(user, DefaultMessageFactory.createErrorMessage(errorCmdId, errorCode, errorMessage));
	}


	public void setMessageWriter(IMessageWriter messageWriter) {
		this.messageWriter = messageWriter;
	}


	public IChannelService getChannelService() {
		return channelService;
	}


	public void setCoreApi(IQAntApi coreApi) {
		this.coreApi = coreApi;
	}

}
