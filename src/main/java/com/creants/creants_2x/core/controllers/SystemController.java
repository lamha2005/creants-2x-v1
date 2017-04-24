package com.creants.creants_2x.core.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.io.IRequest;
import com.creants.creants_2x.core.controllers.SystemRequest;

/**
 * @author LamHM
 *
 */
public class SystemController extends AbstractController {
	private static final Map<Object, String> commandMap;
	private static final String commandPackage = "com.creants.creants_2x.core.controllers.system.";
	private Map<Object, IControllerCommand> commandCache;
	static {
		(commandMap = new HashMap<Object, String>()).put(SystemRequest.Handshake.getId(), commandPackage + "Handshake");
		SystemController.commandMap.put(SystemRequest.Login.getId(), commandPackage + "Login");
		SystemController.commandMap.put(SystemRequest.Logout.getId(), commandPackage + "Logout");
		SystemController.commandMap.put(SystemRequest.JoinRoom.getId(), commandPackage + "JoinRoom");
		SystemController.commandMap.put(SystemRequest.AutoJoin.getId(), commandPackage + "AutoJoin");
		SystemController.commandMap.put(SystemRequest.CreateRoom.getId(), commandPackage + "CreateRoom");
		SystemController.commandMap.put(SystemRequest.LeaveRoom.getId(), commandPackage + "LeaveRoom");
		SystemController.commandMap.put(SystemRequest.KickUser.getId(), commandPackage + "KickUser");
		SystemController.commandMap.put(SystemRequest.BanUser.getId(), commandPackage + "BanUser");
		SystemController.commandMap.put(SystemRequest.FindRooms.getId(), commandPackage + "FindRooms");
		SystemController.commandMap.put(SystemRequest.FindUsers.getId(), commandPackage + "FindUsers");
		SystemController.commandMap.put(SystemRequest.PingPong.getId(), commandPackage + "PingPong");
		SystemController.commandMap.put(SystemRequest.InviteUser.getId(), commandPackage + "game.InviteUser");
		SystemController.commandMap.put(SystemRequest.InvitationReply.getId(), commandPackage + "game.InvitationReply");
		SystemController.commandMap.put(SystemRequest.QuickJoinGame.getId(), commandPackage + "game.QuickJoinGame");
	}


	public void init(final Object o) {
		super.init(o);
		commandCache = new ConcurrentHashMap<Object, IControllerCommand>();
	}


	@Override
	public void processRequest(IRequest request) throws Exception {
		QAntTracer.debug(this.getClass(), "{IN}: " + SystemRequest.fromId(request.getId()).toString());
		IControllerCommand command = null;
		short reqId = request.getId();
		command = commandCache.get(reqId);
		if (command == null) {
			command = getCommand(reqId);
		}

		if (command != null && command.validate(request)) {
			try {
				command.execute(request);
			} catch (Exception re) {
				String msg = re.getMessage();
				if (msg != null) {
					QAntTracer.warn(this.getClass(), msg);
				}
			}
		}
	}


	private IControllerCommand getCommand(short reqId) {
		IControllerCommand command = null;
		String className = commandMap.get(reqId);
		if (className != null) {
			try {
				Class<?> clazz = Class.forName(className);
				command = (IControllerCommand) clazz.newInstance();
				commandCache.put(reqId, command);
			} catch (Exception err) {
				QAntTracer.error(this.getClass(),
						"Could not dynamically instantiate class: " + className + ", Error: " + err,
						QAntTracer.getTraceMessage(err));
			}
		} else {
			QAntTracer.error(this.getClass(), "Cannot find a controller command for request ID: " + reqId);
		}

		return command;
	}

}
