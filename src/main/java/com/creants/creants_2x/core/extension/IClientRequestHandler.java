package com.creants.creants_2x.core.extension;

import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public interface IClientRequestHandler {
	void handleClientRequest(QAntUser user, IQAntObject params);

	void setParentExtension(QAntExtension extension);

	QAntExtension getParentExtension();
}
