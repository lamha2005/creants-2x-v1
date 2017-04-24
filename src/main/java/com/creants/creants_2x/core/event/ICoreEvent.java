package com.creants.creants_2x.core.event;

/**
 * @author LamHa
 *
 */
public abstract interface ICoreEvent {
	/**
	 * Get commandId
	 * 
	 * @return
	 */
	public abstract short getEventId();

	/**
	 * Lấy các tham số kèm theo event
	 * 
	 * @param coreEventParame
	 *            Tham số cần lấy giá trị
	 * @return
	 */
	public abstract Object getParameter(ICoreEventParam coreEventParame);
}
