package com.creants.creants_2x.core.entities;

import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public interface Invitation {
	int getId();

	QAntUser getInviter();

	QAntUser getInvitee();

	boolean isExpired();

	int getExpiryTime();

	int getSecondsForAnswer();

	InvitationCallback getCallback();

	void setCallback(InvitationCallback callback);

	IQAntObject getParams();

	void setParams(IQAntObject params);
}
