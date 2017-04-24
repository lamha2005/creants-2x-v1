package com.creants.creants_2x.core.event.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * 
 * @author LamHa
 *
 */
public class SystemHandlerManager {
	private Map<String, AbstractRequestHandler> systemHandler;


	// TODO review EventManager SFS
	public SystemHandlerManager() {
		systemHandler = new ConcurrentHashMap<String, AbstractRequestHandler>();
		// systemHandler.put(SystemNetworkConstant.COMMAND_PING_PONG, new
		// PingPongRequestHandler());
		// systemHandler.put(SystemNetworkConstant.COMMAND_USER_CONNECT, new
		// ConnectRequestHandler());
		// systemHandler.put(SystemNetworkConstant.COMMAND_USER_DISCONNECT, new
		// DisconnectRequestHandler());
		// systemHandler.put(SystemNetworkConstant.COMMAND_USER_LOGIN, new
		// LoginRequestHandler());
		// systemHandler.put(SystemNetworkConstant.COMMAND_USER_LOGOUT, new
		// LogoutRequestHandler());
		// systemHandler.put(SystemNetworkConstant.COMMAND_USER_JOIN_GAME, new
		// JoinGameRequestHandler());
		// systemHandler.put(SystemNetworkConstant.COMMAND_GET_GAME_LIST, new
		// GameListRequestHandler());
		// systemHandler.put(SystemNetworkConstant.COMMAND_USER_JOIN_ROOM, new
		// JoinRoomRequestHandler());
		// systemHandler.put(SystemNetworkConstant.COMMAND_USER_LEAVE_ROOM, new
		// LeaveRoomRequestHandler());
	}


	public AbstractRequestHandler getHandler(String commandId) {
		return systemHandler.get(commandId);
	}


	public void dispatchEvent(QAntUser user, IQAntObject message) {
		AbstractRequestHandler requestHandler = systemHandler.get(message.getUtfString("command_id"));
		requestHandler.perform(user, message);
	}
}
