package com.creants.creants_2x.core.event;

/**
 * Class định nghĩa các Event của hệ thống. Các Event của hệ thống sẽ được xử lý
 * bởi
 * 
 * @author LamHa
 *
 */
public class SystemNetworkConstant {
	public static final byte PROTOCOL_VERSION = 1;

	/************************ SERVICE ID *********************/
	public static final String COMMAND_ERROR = "_error";
	public static final String COMMAND_USER_CONNECT = "_userConnect";
	public static final String COMMAND_USER_DISCONNECT = "_userDisconnect";
	public static final String COMMAND_USER_JOIN_GAME = "_userJoinGame";
	public static final String COMMAND_USER_LOGIN = "_userLogin";
	public static final String COMMAND_USER_LOGOUT = "cas_cmd_user_logout";
	public static final String COMMAND_GET_GAME_LIST = "cas_cmd_get_game_list";
	public static final String COMMAND_USER_JOIN_ROOM = "cas_cmd_user_join_room";
	public static final String COMMAND_USER_LEAVE_ROOM = "cas_cmd_user_leave_room";
	public static final String COMMAND_USER_CREATE_ROOM = "cas_cmd_user_create_room";

	public static final String COMMAND_PING_PONG = "cas_cmd_ping_pong";
	public static final String COMMAND_MONEY_CHANGE = "cas_cmd_money_change";
	public static final String COMMAND_UNKNOW = "cas_cmd_unknow";

	/************************ KEY ****************************/
	public static final String KEYR_ERROR = "_errorCode";
	public static final String KEYS_COMMAND_ID = "_commandId";
	public static final String KEYS_TOKEN = "_token";
	public static final String KEYS_ERROR_COMMAND_ID = "cas_error_command_id";
	public static final String KEYB_PROTOCOL_VERSION = "cas_protocol_version";
	public static final String KEYS_MESSAGE = "cas_message";

	public static final short KEYS_USERNAME = 0x01;
	public static final short KEYS_PASSWORD = 0x02;
	public static final short KEYL_MONEY = 0x03;
	public static final short KEYS_AVATAR = 0x04;
	public static final short KEYS_LANGUAGE = 0x05;
	public static final short KEYS_GAME_NAME = 0x06;
	public static final short KEYB_GAME_ID = 0x07;
	public static final short KEYS_FULL_NAME = 0x08;
	public static final short KEYS_DEVICE_NAME = 0x09;
	public static final short KEYS_DEVICE_OS = 0x0A;
	public static final short KEYS_OS_VERSION = 0x0B;
	public static final short KEYBL_IS_JAIBREAK = 0x0C;
	public static final short KEYS_GAME_VERSION = 0x0E;
	public static final short KEYS_DEVICE_UNIQUE_ID = 0x0F;
	public static final short KEYS_PHONE_NUMBER = 0x10;
	public static final short KEYR_SCREEN_WIDTH = 0x11;
	public static final short KEYR_SCREEN_HEIGHT = 0x12;
	public static final short KEYBL_IS_FIRST_LOGIN = 0x13;
	public static final short KEYS_JSON_DATA = 0x15;
	public static final short KEYB_STATUS = 0x16;
	public static final short KEYI_USER_ID = 0x17;
	public static final short KEYR_ACTION_IN_GAME = 0x18;

	public static final short KEYI_ROOM_ID = 0x19;
	public static final short KEYS_ROOM_NAME = 0x1A;
	public static final short KEYR_COMMAND_ID = 0x1C;
	public static final short KEYB_GAME_STATE = 0x1E;

}
