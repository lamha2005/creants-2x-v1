package com.creants.creants_2x.socket.io;

import com.creants.creants_2x.socket.gate.entities.IQAntObject;

/**
 * @author LamHa
 *
 */
public interface IEngineMessage {
	short getId();

	void setId(short id);

	IQAntObject getContent();

	void setContent(IQAntObject qAntObject);

	Object getAttribute(String attr);

	void setAttribute(String attrKey, Object attrObj);
}
