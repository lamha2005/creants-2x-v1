package com.creants.creants_2x.core.api.response;

import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.core.entities.Invitation;
import com.creants.creants_2x.core.entities.invitation.InvitationResponse;
import com.creants.creants_2x.core.util.UsersUtil;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.io.IResponse;
import com.creants.creants_2x.socket.io.Response;

/**
 * @author LamHa
 *
 */
public class QAntGameResponseApi implements IQAntGameResponseApi {

	@Override
	public void notifyInivitation(Invitation invitation) {
		IQAntObject resObj = QAntObject.newInstance();
		QAntUser inviter = invitation.getInviter();
		QAntUser invitee = invitation.getInvitee();
		if (UsersUtil.usersSeeEachOthers(inviter, invitee)) {
			resObj.putInt("ui", inviter.getUserId());
		} else {
			resObj.putQAntArray("u", inviter.toQAntArray());
		}
		resObj.putShort("t", (short) invitation.getSecondsForAnswer());
		resObj.putInt("ii", invitation.getId());
		if (invitation.getParams() != null) {
			resObj.putQAntObject("p", invitation.getParams());
		}

		IResponse response = new Response();
		response.setId(SystemRequest.InviteUser.getId());
		response.setContent(resObj);
		response.setRecipients(invitee.getChannel());
		response.write();
	}

	@Override
	public void notifyInvitationResponse(Invitation invitation, InvitationResponse invitationResponse,
			IQAntObject params) {
		IQAntObject resObj = QAntObject.newInstance();
		QAntUser inviter = invitation.getInviter();
		QAntUser invitee = invitation.getInvitee();
		if (UsersUtil.usersSeeEachOthers(inviter, invitee)) {
			resObj.putInt("ui", invitee.getUserId());
		} else {
			resObj.putQAntArray("u", invitee.toQAntArray());
		}
		resObj.putByte("ri", (byte) invitationResponse.getId());
		if (params != null) {
			resObj.putQAntObject("p", params);
		}
		IResponse response = new Response();
		response.setId(SystemRequest.InvitationReply.getId());
		response.setContent(resObj);
		response.setRecipients(inviter.getChannel());
		response.write();
	}
}
