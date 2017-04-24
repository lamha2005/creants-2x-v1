package com.creants.creants_2x.core.entities.invitation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.IQAntEvent;
import com.creants.creants_2x.core.IQAntEventListener;
import com.creants.creants_2x.core.QAntEventParam;
import com.creants.creants_2x.core.entities.Invitation;
import com.creants.creants_2x.core.entities.InvitationCallback;
import com.creants.creants_2x.core.exception.QAntErrorCode;
import com.creants.creants_2x.core.exception.QAntErrorData;
import com.creants.creants_2x.core.exception.QAntInvitationException;
import com.creants.creants_2x.core.exception.QAntRuntimeException;
import com.creants.creants_2x.core.service.IService;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public class QAntInvitationManager implements IService, InvitationManager {
	protected String serviceName;
	protected final QAntServer qant;
	private ScheduledFuture<?> cleanerTask;
	protected final Map<Integer, Invitation> invitationsById;
	protected final Map<QAntUser, List<Invitation>> invitationsByOwner;
	protected int maxInvitationsPerUser;

	public QAntInvitationManager() {
		this.maxInvitationsPerUser = 16;
		this.serviceName = "QAntInvitationManager";
		this.qant = QAntServer.getInstance();
		this.invitationsById = new ConcurrentHashMap<Integer, Invitation>();
		this.invitationsByOwner = new ConcurrentHashMap<QAntUser, List<Invitation>>();
	}

	public void init(final Object o) {
	}

	public void destroy(final Object o) {
		invitationsById.clear();
		invitationsByOwner.clear();
		if (cleanerTask != null) {
			cleanerTask.cancel(true);
		}
	}

	public String getName() {
		return this.serviceName;
	}

	public void setName(final String name) {
		throw new UnsupportedOperationException("Method not supported.");
	}

	public void handleMessage(final Object o) {
		throw new UnsupportedOperationException("Method not supported.");
	}

	public Invitation findById(final int id) {
		return this.invitationsById.get(id);
	}

	public int getMaxInvitationsPerUser() {
		return this.maxInvitationsPerUser;
	}

	public void setMaxInvitationsPerUser(final int value) {
		this.maxInvitationsPerUser = value;
	}

	public void startInvitation(final Invitation invitation, final InvitationCallback callBack) {
		final List<Invitation> userInvitationList = this.prepareStartInvitation(invitation, callBack);
		if (userInvitationList.size() < this.maxInvitationsPerUser) {
			this.invitationsById.put(invitation.getId(), invitation);
			synchronized (userInvitationList) {
				userInvitationList.add(invitation);
			}
			// monitorexit(userInvitationList)
			invitation.setCallback(callBack);
			QAntTracer.debug(this.getClass(), "Invitation: " + invitation + " started.");
			return;
		}
		throw new QAntRuntimeException("The user: " + invitation.getInviter()
				+ " is already running the max allowed number of invitations = " + this.maxInvitationsPerUser);
	}

	protected List<Invitation> prepareStartInvitation(Invitation invitation, InvitationCallback callBack) {
		if (invitation == null) {
			throw new NullPointerException("Invitation object is null. Please provide a valid object.");
		}
		if (callBack == null) {
			throw new NullPointerException("Callback object is null. Please provide a valid object.");
		}
		if (invitation.getInviter() == null || invitation.getInvitee() == null) {
			throw new IllegalArgumentException("Both Inviter and Invitee must be non-null User objects.");
		}

		QAntUser inviter = invitation.getInviter();
		List<Invitation> userInvitationList = invitationsByOwner.get(inviter);
		synchronized (invitationsByOwner) {
			if (userInvitationList == null) {
				userInvitationList = new LinkedList<Invitation>();
				invitationsByOwner.put(inviter, userInvitationList);
			}
		}

		return userInvitationList;
	}

	public void suppressInvitation(final Invitation invitation) {
		throw new UnsupportedOperationException("This feature will be available in future implementations.");
	}

	public void onInvitationResult(int invitationId, InvitationResponse result, IQAntObject params)
			throws QAntInvitationException {

		Invitation invitation = findById(invitationId);
		if (invitation == null && result != InvitationResponse.EXPIRED) {
			throw new QAntInvitationException(
					String.format("Invitation result discarded. Invitation is not managed. ID: %s, Result: %s",
							invitationId, result),
					new QAntErrorData(QAntErrorCode.INVITATION_NOT_VALID));
		}
		onInvitationResult(invitation, result, params);
	}

	public void onInvitationResult(Invitation invitation, InvitationResponse result, IQAntObject params)
			throws QAntInvitationException {
		String errorMsg = null;
		if (!invitationsById.containsKey(invitation.getId())) {
			errorMsg = "Invitation is not managed (maybe removed?)";
		}
		if (invitation.getCallback() == null) {
			errorMsg = "Invitation no longer valid.";
		}
		if (invitation.isExpired()) {
			errorMsg = "Invitation is expired.";
		}
		if (errorMsg != null) {
			throw new QAntInvitationException(errorMsg, new QAntErrorData(QAntErrorCode.INVITATION_NOT_VALID));
		}
		if (result == InvitationResponse.ACCEPT) {
			handleAcceptedInvitation(invitation, params);
		} else {
			handleRefusedInvitation(invitation, params);
		}
	}

	private void cleanExpiredInvitations() {
		final Iterator<Invitation> iter = this.invitationsById.values().iterator();
		while (iter.hasNext()) {
			final Invitation invitation = iter.next();
			if (invitation.isExpired()) {
				iter.remove();
				this.removeInvitation(invitation);
				if (invitation.getCallback() == null) {
					continue;
				}
				invitation.getCallback().onExpired(invitation);
			}
		}
	}

	protected void handleAcceptedInvitation(Invitation invitation, IQAntObject params) {
		removeInvitation(invitation);
		InvitationCallback callback = invitation.getCallback();
		if (callback != null) {
			callback.onAccepted(invitation, params);
		}
	}

	protected void handleRefusedInvitation(Invitation invitation, IQAntObject params) {
		removeInvitation(invitation);
		InvitationCallback callback = invitation.getCallback();
		if (callback != null) {
			callback.onRefused(invitation, params);
		}
	}

	private void handleInviterDisconnected(QAntUser inviter) {
		List<Invitation> invitationList = invitationsByOwner.remove(inviter);
		if (invitationList != null) {
			for (Invitation invitation : invitationList) {
				invitationsById.remove(invitation.getId());
			}
			QAntTracer.debug(this.getClass(),
					"Removed " + invitationList.size() + " invitations for disconnected user: " + inviter);
		}
	}

	protected void removeInvitation(final Invitation invitation) {
		this.invitationsById.remove(invitation.getId());
		final List<Invitation> invitationList = this.invitationsByOwner.get(invitation.getInviter());
		if (invitationList != null) {
			synchronized (invitationList) {
				invitationList.remove(invitation);
			}
		}
	}

	private final class UserExitEventHandler implements IQAntEventListener {
		@Override
		public void handleServerEvent(IQAntEvent event) throws Exception {
			QAntInvitationManager.this.handleInviterDisconnected((QAntUser) event.getParameter(QAntEventParam.USER));
		}
	}
}
