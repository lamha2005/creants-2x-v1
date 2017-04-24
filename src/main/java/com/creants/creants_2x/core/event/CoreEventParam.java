package com.creants.creants_2x.core.event;

/**
 * Các tham số kèm theo event
 * 
 * @author LamHa
 *
 */
public enum CoreEventParam implements ICoreEventParam {
	GAME, ROOM, USER, LOGIN_NAME, LOGIN_PASSWORD, LOGIN_IN_DATA, LOGIN_OUT_DATA, JOINED_ROOMS, PLAYER_ID, PLAYER_IDS_BY_ROOM, SESSION, DISCONNECTION_REASON, VARIABLES, VARIABLES_MAP, RECIPIENT, MESSAGE, OBJECT, UPLOAD_FILE_LIST, UPLOAD_HTTP_PARAMS;
}
