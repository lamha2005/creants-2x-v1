package com.creants.creants_2x.core.exception;

/**
 * @author LamHM
 *
 */
public class QAntException extends Exception {
	private static final long serialVersionUID = 1L;
	QAntErrorData errorData;


	public QAntException() {
		this.errorData = null;
	}


	public QAntException(String message) {
		super(message);
		this.errorData = null;
	}


	public QAntException(String message, QAntErrorData data) {
		super(message);
		this.errorData = data;
	}


	public QAntException(Throwable t) {
		super(t);
		errorData = null;
	}


	public QAntErrorData getErrorData() {
		return errorData;
	}
}
