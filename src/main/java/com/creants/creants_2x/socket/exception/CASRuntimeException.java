package com.creants.creants_2x.socket.exception;

/**
 * @author LamHM
 *
 */
public class CASRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CASRuntimeException() {
	}

	public CASRuntimeException(final String message) {
		super(message);
	}

	public CASRuntimeException(final Throwable t) {
		super(t);
	}
}
