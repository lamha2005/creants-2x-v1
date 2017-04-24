package com.creants.creants_2x.core.event.handler;

import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public interface IRequestHandler {
	/**
	 * Init các service, attribute cho một handler cụ thể
	 */
	public void initialize();


	/**
	 * Thực thi message request
	 * 
	 * @param user
	 *            đối tượng lưu trữ thông tin của user request
	 * @param message
	 *            message user gửi lên server
	 */
	public void perform(QAntUser user, IQAntObject message);
}
