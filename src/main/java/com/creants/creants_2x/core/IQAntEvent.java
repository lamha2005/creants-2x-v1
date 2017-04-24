package com.creants.creants_2x.core;

/**
 * @author LamHM
 *
 */
public interface IQAntEvent {
	QAntEventType getType();


	Object getParameter(IQAntEventParam id);
}
