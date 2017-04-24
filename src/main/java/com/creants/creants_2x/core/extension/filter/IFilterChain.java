package com.creants.creants_2x.core.extension.filter;

import com.creants.creants_2x.core.IQAntEvent;
import com.creants.creants_2x.core.exception.QAntException;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public interface IFilterChain {
	void addFilter(String name, QAntExtensionFilter filter);

	void remove(String name);

	FilterAction runRequestInChain(String cmdName, QAntUser user, IQAntObject params);

	FilterAction runEventInChain(IQAntEvent event) throws QAntException;

	int size();

	void destroy();
}
