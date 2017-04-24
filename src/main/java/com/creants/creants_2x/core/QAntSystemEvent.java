package com.creants.creants_2x.core;

import java.util.Map;

/**
 * @author LamHa
 *
 */
public class QAntSystemEvent extends QAntEvent {
	private final Map<IQAntEventParam, Object> sysParams;

	public QAntSystemEvent(QAntEventType type, Map<IQAntEventParam, Object> params,
			Map<IQAntEventParam, Object> sysParams) {
		super(type, params);
		this.sysParams = sysParams;
	}

	public Object getSysParameter(IQAntEventParam key) {
		return sysParams.get(key);
	}

	public void setSysParameter(IQAntEventParam key, Object value) {
		if (sysParams != null) {
			sysParams.put(key, value);
		}
	}
}
