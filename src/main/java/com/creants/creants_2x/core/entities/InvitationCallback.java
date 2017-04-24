package com.creants.creants_2x.core.entities;

import com.creants.creants_2x.socket.gate.entities.IQAntObject;

/**
 * @author LamHa
 *
 */
public interface InvitationCallback {
	void onAccepted(Invitation invitation, IQAntObject params);

	void onRefused(Invitation invitation, IQAntObject params);

	void onExpired(Invitation invitation);
}
