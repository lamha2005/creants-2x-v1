package com.creants.creants_2x.core.event.handler;

/**
 * @author LamHa
 *
 */
public abstract interface ISystemHandlerFactory {
	/**
	 * Tìm handler của game cụ thể định nghĩa
	 * 
	 * @param commandId
	 * @return
	 */
	public abstract AbstractRequestHandler findHandler(short commandId);

	/**
	 * Tìm handler của system
	 * 
	 * @param commandId
	 * @return
	 */
	public abstract AbstractRequestHandler findSystemHandler(short commandId);
}
