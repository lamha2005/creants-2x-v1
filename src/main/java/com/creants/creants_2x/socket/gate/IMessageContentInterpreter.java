package com.creants.creants_2x.socket.gate;

/**
 * Hỗ trợ trace thông tin message
 * 
 * @author LamHa
 *
 */
public abstract interface IMessageContentInterpreter {
	public abstract String interpretCommand(Short serviceId);

	public abstract String interpretKey(Short key);

	public abstract String interpetValue(Short key, byte[] value);

	public abstract String interpetActionInGameValue(Short key, byte[] value);

	public abstract void addInToInterpreter(Class<?>... clazz);
}
