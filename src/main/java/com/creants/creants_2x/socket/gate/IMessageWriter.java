package com.creants.creants_2x.socket.gate;

import java.util.List;

import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public interface IMessageWriter {
	/**
	 * P2P Send message đến user
	 * 
	 * @param user
	 * @param message
	 */
	public void writeMessage(QAntUser user, QAntObject message);


	/**
	 * P2G Send message đến danh sách user
	 * 
	 * @param user
	 * @param message
	 */
	public void writeMessage(List<QAntUser> user, QAntObject message);

}
