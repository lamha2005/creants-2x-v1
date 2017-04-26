package com.creants.creants_2x.core.extension;

import java.util.List;

import com.creants.creants_2x.core.api.IQAntApi;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHM
 *
 */
public abstract class BaseServerEventHandler implements IServerEventHandler {
	private QAntExtension parentExtension;


	@Override
	public QAntExtension getParentExtension() {
		return this.parentExtension;
	}


	@Override
	public void setParentExtension(QAntExtension ext) {
		this.parentExtension = ext;
	}


	protected IQAntApi getApi() {
		return this.parentExtension.qantApi;
	}


	protected void send(String cmdName, IQAntObject params, List<QAntUser> recipients) {
		this.parentExtension.send(cmdName, params, recipients);
	}


	protected void send(String cmdName, IQAntObject params, QAntUser recipient) {
		this.parentExtension.send(cmdName, params, recipient);
	}

}
