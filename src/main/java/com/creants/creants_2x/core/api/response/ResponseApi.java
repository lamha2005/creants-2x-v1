package com.creants.creants_2x.core.api.response;

import java.util.ArrayList;
import java.util.List;

import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.exception.QAntErrorData;
import com.creants.creants_2x.core.exception.QAntException;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.io.IResponse;
import com.creants.creants_2x.socket.io.Response;

import io.netty.channel.Channel;

/**
 * @author LamHM
 *
 */
public class ResponseApi implements IResponseApi {

	public void notifyRoomAdded(Room room) {
	}


	@Override
	public void notifyRequestError(QAntException error, QAntUser recipient, SystemRequest requestType) {
		notifyRequestError(error.getErrorData(), recipient, requestType);
	}


	@Override
	public void notifyRequestError(QAntErrorData errData, QAntUser recipient, SystemRequest requestType) {
		if (recipient != null) {
			IQAntObject resObj = QAntObject.newInstance();
			IResponse response = (IResponse) new Response();
			response.setId(requestType.getId());
			response.setContent(resObj);
			response.setRecipients(recipient.getChannel());
			resObj.putShort("ec", errData.getCode().getId());
			resObj.putUtfStringArray("ep", errData.getParams());
			response.write();
		} else {
			QAntTracer.warn(this.getClass(), "Can't send error notification to client. Attempting to send: "
					+ errData.getCode() + " in response to: " + requestType + " Recipient is NULL!");
		}
	}


	@Override
	public void notifyJoinRoomSuccess(QAntUser recipient, Room joinedRoom) {
		IQAntObject resObj = QAntObject.newInstance();
		IResponse response = (IResponse) new Response();
		response.setId((short) SystemRequest.JoinRoom.getId());
		response.setContent(resObj);
		response.setRecipients(recipient.getChannel());
		response.write();
	}


	@Override
	public void sendExtResponse(String cmdName, IQAntObject params, List<Channel> Recipient, Room room) {
		IQAntObject resObj = QAntObject.newInstance();
		resObj.putUtfString("c", cmdName);
		resObj.putQAntObject("p", (params != null) ? params : new QAntObject());
		if (room != null) {
			resObj.putInt("r", room.getId());
		}

		IResponse response = (IResponse) new Response();
		response.setId(SystemRequest.CallExtension.getId());
		response.setContent(resObj);
		response.setRecipients(Recipient);
		response.write();
	}


	@Override
	public void sendPingPongResponse(Channel recipient) {
		IResponse response = (IResponse) new Response();
		response.setId(SystemRequest.PingPong.getId());
		response.setContent(new QAntObject());
		response.setRecipients(recipient);
		response.write();
	}


	@Override
	public void notifyRoomRemoved(Room room) {

	}


	@Override
	public void notifyUserExitRoom(QAntUser user, Room room, boolean sendToEveryOne) {
		List<Channel> recipients = new ArrayList<Channel>();
		if (sendToEveryOne) {
			recipients.addAll(room.getChannelList());
		}
		IQAntObject resObj = QAntObject.newInstance();
		IResponse response = (IResponse) new Response();
		response.setId(SystemRequest.OnUserExitRoom.getId());
		response.setContent(resObj);
		response.setRecipients(recipients);
		resObj.putInt("u", user.getUserId());
		resObj.putInt("r", room.getId());
		response.write();
	}


	@Override
	public void notifyUserLost(QAntUser user, List<Room> rooms) {
		// TODO Auto-generated method stub

	}


	@Override
	public void notifyUserEnterRoom(QAntUser user, Room room) {
		// TODO Auto-generated method stub

	}


	@Override
	public void notifyLogout(Channel channel, String zoneName) {
		// TODO Auto-generated method stub

	}

}
