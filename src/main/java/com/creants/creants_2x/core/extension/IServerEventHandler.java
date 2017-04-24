package com.creants.creants_2x.core.extension;

import com.creants.creants_2x.core.IQAntEvent;
import com.creants.creants_2x.core.exception.QAntException;

/**
 * @author LamHa
 *
 */
public interface IServerEventHandler {
	void handleServerEvent(IQAntEvent event) throws QAntException;

	void setParentExtension(QAntExtension p0);

	QAntExtension getParentExtension();
}
