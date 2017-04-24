package com.creants.creants_2x.core.util;

import com.creants.creants_2x.core.event.SystemNetworkConstant;
import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public class DefaultMessageFactory {
	private static final byte PROTOCOL_VERSION = 1;


	public static QAntObject createMessage(String commandId) {
		QAntObject message = new QAntObject();
		message.putUtfString(SystemNetworkConstant.KEYS_COMMAND_ID, commandId);
		message.putByte(SystemNetworkConstant.KEYB_PROTOCOL_VERSION, PROTOCOL_VERSION);
		return message;
	}


	/**
	 * Tạo message lỗi.<br>
	 * Khi nào sử dụng createErrorMessage?<br>
	 * Khi client cần bắt những lỗi chung chung để hiện dialog.<br>
	 * Đối với các lỗi logic trong game thì nên trả về mã code lỗi theo command
	 * mà client request để client xử lý theo logic.
	 * 
	 * @param code
	 *            mã code lỗi
	 * @param errorMessage
	 *            thông tin lỗi
	 */
	public static QAntObject createErrorMessage(String errorCmdId, short code, String errorMessage) {
		QAntObject message = new QAntObject();
		message.putUtfString(SystemNetworkConstant.KEYS_COMMAND_ID, SystemNetworkConstant.COMMAND_ERROR);
		message.putByte(SystemNetworkConstant.KEYB_PROTOCOL_VERSION, PROTOCOL_VERSION);
		message.putUtfString(SystemNetworkConstant.KEYS_ERROR_COMMAND_ID, errorCmdId);
		message.putShort(SystemNetworkConstant.KEYR_ERROR, code);
		message.putUtfString(SystemNetworkConstant.KEYS_MESSAGE, errorMessage);
		return message;
	}


	public static QAntObject responseMessage(String commandId) {
		return createMessage(commandId);
	}


	/**
	 * Tạo message connect
	 * 
	 * @param sessionId
	 * @return
	 */
	public static QAntObject createConnectMessage(long sessionId) {
		QAntObject message = new QAntObject();
		message.putUtfString(SystemNetworkConstant.KEYS_COMMAND_ID, SystemNetworkConstant.COMMAND_USER_CONNECT);
		message.putByte(SystemNetworkConstant.KEYB_PROTOCOL_VERSION, PROTOCOL_VERSION);
		return message;
	}


	/**
	 * Tạo message disconnect
	 * 
	 * @param userId
	 * @return
	 */
	public static QAntObject createDisconnectMessage(QAntUser user) {
		QAntObject message = new QAntObject();
		message.putUtfString(SystemNetworkConstant.KEYS_COMMAND_ID, SystemNetworkConstant.COMMAND_USER_CONNECT);
		message.putByte(SystemNetworkConstant.KEYB_PROTOCOL_VERSION, PROTOCOL_VERSION);
		return message;
	}


	/**
	 * Tạo message trong game
	 * 
	 * @return
	 */
	public static QAntObject createMessageInGame() {
		QAntObject message = new QAntObject();
		message.putUtfString(SystemNetworkConstant.KEYS_COMMAND_ID, SystemNetworkConstant.COMMAND_USER_CONNECT);
		message.putByte(SystemNetworkConstant.KEYB_PROTOCOL_VERSION, PROTOCOL_VERSION);
		return message;
	}

}
