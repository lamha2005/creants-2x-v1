package com.creants.creants_2x.core;

/**
 * @author LamHM
 *
 */
public interface IQAntEventDispatcher {
	void addEventListener(QAntEventType type, IQAntEventListener listener);


	boolean hasEventListener(QAntEventType type);


	void removeEventListener(QAntEventType type, IQAntEventListener eventListener);


	void dispatchEvent(IQAntEvent event);
}
