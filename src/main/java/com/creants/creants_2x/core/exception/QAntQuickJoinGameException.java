package com.creants.creants_2x.core.exception;

/**
 * @author LamHa
 *
 */
public class QAntQuickJoinGameException extends QAntJoinRoomException {

	public QAntQuickJoinGameException(String message) {
		super(message);
	}

	public QAntQuickJoinGameException(String message, QAntErrorData errData) {
		super(message, errData);
	}

	private static final long serialVersionUID = 1L;

}
