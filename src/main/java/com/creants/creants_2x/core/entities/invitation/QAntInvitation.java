package com.creants.creants_2x.core.entities.invitation;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.entities.Invitation;
import com.creants.creants_2x.core.entities.InvitationCallback;
import com.creants.creants_2x.socket.gate.entities.IQAntArray;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public class QAntInvitation implements Invitation {
	private final int id;
	private final QAntUser inviter;
	private final QAntUser invitee;
	private final long expiryTime;
	private final int secondsForAnswer;
	private IQAntObject params;
	private InvitationCallback callback;

	public static Invitation fromQAntArray(IQAntArray qanta) {
		return new QAntInvitation(QAntServer.getInstance().getUserManager().getUserById(qanta.getInt(0)),
				QAntServer.getInstance().getUserManager().getUserById(qanta.getInt(1)), qanta.getShort(2),
				(qanta.size() == 4) ? qanta.getQAntObject(3) : null);
	}

	public QAntInvitation(QAntUser inviter, QAntUser invitee, int secondsForAnswer) {
		this(inviter, invitee, secondsForAnswer, null);
	}

	public QAntInvitation(QAntUser inviter, QAntUser invitee, int secondsForAnswer, IQAntObject params) {
		this.id = this.nextUniqueId();
		this.inviter = inviter;
		this.invitee = invitee;
		this.params = params;
		this.secondsForAnswer = secondsForAnswer;
		this.expiryTime = System.currentTimeMillis() + 1000 * secondsForAnswer;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public IQAntObject getParams() {
		return this.params;
	}

	@Override
	public void setParams(IQAntObject params) {
		this.params = params;
	}

	@Override
	public QAntUser getInviter() {
		return this.inviter;
	}

	@Override
	public QAntUser getInvitee() {
		return this.invitee;
	}

	@Override
	public int getExpiryTime() {
		return (int) this.expiryTime / 1000;
	}

	@Override
	public boolean isExpired() {
		return System.currentTimeMillis() > this.expiryTime;
	}

	@Override
	public int getSecondsForAnswer() {
		return this.secondsForAnswer;
	}

	@Override
	public InvitationCallback getCallback() {
		return this.callback;
	}

	@Override
	public void setCallback(InvitationCallback callback) {
		this.callback = callback;
	}

	@Override
	public String toString() {
		return String.format("{ Invitation: %s, From: %s To: %s }", this.id, this.inviter.getName(),
				this.invitee.getName());
	}

	private int nextUniqueId() {
		// return
		// QAntServer.getInstance().getInvitationManager().getIDGenerator().generateID();
		return -1;
	}
}
