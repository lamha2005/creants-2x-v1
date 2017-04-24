package com.creants.creants_2x.socket.gate.entities;

/**
 * @author Lamhm
 *
 */
public class QAntDataWrapper {
	private QAntDataType typeId;
	private Object object;


	public QAntDataWrapper(QAntDataType typeId, Object object) {
		this.typeId = typeId;
		this.object = object;
	}


	public QAntDataType getTypeId() {
		return this.typeId;
	}


	public Object getObject() {
		return this.object;
	}
}
