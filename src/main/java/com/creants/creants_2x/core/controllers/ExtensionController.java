package com.creants.creants_2x.core.controllers;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.exception.QAntExtensionException;
import com.creants.creants_2x.core.extension.IQAntExtension;
import com.creants.creants_2x.core.managers.IExtensionManager;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHM
 *
 */
public class ExtensionController extends AbstractController {
	public static final String KEY_EXT_CMD = "c";
	public static final String KEY_EXT_PARAMS = "p";
	public static final String KEY_ROOMID = "r";
	private final QAntServer qant;
	private IExtensionManager extensionManager;

	public ExtensionController() {
		qant = QAntServer.getInstance();
	}

	public void init(Object o) {
		QAntTracer.info(this.getClass(), "- init ExtensionController");
		super.init(o);
		this.extensionManager = qant.getExtensionManager();
	}

	public void destroy(Object o) {
		super.destroy(o);
		extensionManager = null;
	}

	@Override
	public void processRequest(IRequest request) throws Exception {
		QAntTracer.debug(this.getClass(), request.toString());
		long t1 = System.nanoTime();
		QAntUser sender = qant.getUserManager().getUserByChannel(request.getSender());
		if (sender == null) {
			throw new QAntExtensionException("Extension Request refused. Sender is not a User: " + request.getSender());
		}
		IQAntObject reqObj = (IQAntObject) request.getContent();
		QAntTracer.debug(this.getClass(), reqObj.getDump());

		String cmd = reqObj.getUtfString(KEY_EXT_CMD);
		if (cmd == null || cmd.length() == 0) {
			QAntTracer.warn(this.getClass(), "Extension Request refused. Missing CMD. " + sender);
			return;
		}

		int roomId = -1;
		if (reqObj.containsKey(KEY_ROOMID)) {
			roomId = reqObj.getInt(KEY_ROOMID);
		}
		IQAntObject params = reqObj.getQAntObject(KEY_EXT_PARAMS);
		Zone zone = sender.getZone();
		IQAntExtension extension = null;
		if (roomId > -1) {
			Room room = zone.getRoomById(roomId);
			if (room != null) {
				if (!room.containsUser(sender)) {
					throw new QAntExtensionException(
							"User cannot invoke Room extension if he's not joined in that Room. " + sender + ", "
									+ room);
				}
				extension = extensionManager.getRoomExtension(room);
			}
		} else {
			extension = extensionManager.getZoneExtension(zone);
		}

		if (extension == null) {
			throw new QAntExtensionException(String.format("No extensions can be invoked: %s, RoomId: %s",
					zone.toString(), (roomId == -1) ? "None" : roomId));
		}

		sender.updateLastRequestTime();
		try {
			extension.handleClientRequest(cmd, sender, params);
		} catch (Exception e) {
			QAntTracer.error(this.getClass(), "Error while handling client request in extension: "
					+ extension.toString() + "/" + "Extension Cmd: " + cmd);
		}

		long t2 = System.nanoTime();
		QAntTracer.debug(this.getClass(), "Extension call executed in: " + (t2 - t1) / 1000000.0);
	}

}
