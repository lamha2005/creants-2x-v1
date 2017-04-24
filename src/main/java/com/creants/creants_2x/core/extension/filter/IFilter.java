package com.creants.creants_2x.core.extension.filter;

import com.creants.creants_2x.core.IQAntEvent;
import com.creants.creants_2x.core.exception.QAntException;
import com.creants.creants_2x.core.extension.QAntExtension;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public interface IFilter {
	void init(QAntExtension extension);

	void destroy();

	FilterAction handleClientRequest(String cmdName, QAntUser user, IQAntObject params) throws QAntException;

	FilterAction handleServerEvent(IQAntEvent event) throws QAntException;
}
