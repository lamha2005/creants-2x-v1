package com.creants.creants_2x.core.entities.invitation;

import com.creants.creants_2x.core.entities.Invitation;
import com.creants.creants_2x.core.entities.InvitationCallback;
import com.creants.creants_2x.core.exception.QAntInvitationException;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;

/**
 * @author LamHa
 *
 */
public interface InvitationManager {
	Invitation findById(int id);

	void startInvitation(Invitation invitation, InvitationCallback callback);

	void suppressInvitation(Invitation invitation);

	void onInvitationResult(Invitation invitation, InvitationResponse invResponse, IQAntObject params)
			throws QAntInvitationException;

	void onInvitationResult(int id, InvitationResponse invResponse, IQAntObject params) throws QAntInvitationException;

	int getMaxInvitationsPerUser();

	void setMaxInvitationsPerUser(int value);
}
