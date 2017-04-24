package com.creants.creants_2x.core.api.response;

import com.creants.creants_2x.core.entities.Invitation;
import com.creants.creants_2x.core.entities.invitation.InvitationResponse;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;

/**
 * @author LamHa
 *
 */
public interface IQAntGameResponseApi {
	void notifyInivitation(Invitation invitation);

	void notifyInvitationResponse(Invitation invitation, InvitationResponse invResponse, IQAntObject params);
}
