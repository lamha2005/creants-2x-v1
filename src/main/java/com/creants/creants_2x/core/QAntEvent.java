package com.creants.creants_2x.core;

import java.util.Map;

/**
 * @author LamHM
 *
 */
public class QAntEvent implements IQAntEvent {
	private final QAntEventType type;
	private final Map<IQAntEventParam, Object> params;


	public QAntEvent(QAntEventType type) {
		this(type, null);
	}


	public QAntEvent(QAntEventType type, Map<IQAntEventParam, Object> params) {
		this.type = type;
		this.params = params;
	}


	@Override
	public QAntEventType getType() {
		return type;
	}


	@Override
	public Object getParameter(IQAntEventParam id) {
		Object param = null;
		if (params != null) {
			param = params.get(id);
		}

		return param;
	}


	@Override
	public String toString() {
		return String.format("{ %s, Params: %s }", type, (params != null) ? params.keySet() : "none");
	}
}
