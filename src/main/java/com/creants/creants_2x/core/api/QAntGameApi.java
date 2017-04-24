package com.creants.creants_2x.core.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.IQAntEventParam;
import com.creants.creants_2x.core.QAntEvent;
import com.creants.creants_2x.core.QAntEventParam;
import com.creants.creants_2x.core.QAntEventType;
import com.creants.creants_2x.core.api.response.IQAntGameResponseApi;
import com.creants.creants_2x.core.api.response.QAntGameResponseApi;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.core.entities.Invitation;
import com.creants.creants_2x.core.entities.InvitationCallback;
import com.creants.creants_2x.core.entities.QAntRoom;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.entities.invitation.InvitationResponse;
import com.creants.creants_2x.core.entities.invitation.QAntInvitation;
import com.creants.creants_2x.core.entities.match.BoolMatch;
import com.creants.creants_2x.core.entities.match.MatchExpression;
import com.creants.creants_2x.core.entities.match.MatchingUtils;
import com.creants.creants_2x.core.exception.QAntCreateRoomException;
import com.creants.creants_2x.core.exception.QAntErrorCode;
import com.creants.creants_2x.core.exception.QAntErrorData;
import com.creants.creants_2x.core.exception.QAntInvitationException;
import com.creants.creants_2x.core.exception.QAntJoinRoomException;
import com.creants.creants_2x.core.exception.QAntQuickJoinGameException;
import com.creants.creants_2x.core.game.CreateQAntGameSettings;
import com.creants.creants_2x.core.game.QAntGame;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public class QAntGameApi implements IQAntGameApi {
	private static final int GAME_PASSWORD_LEN = 16;
	protected QAntServer qant;
	protected IQAntApi qantApi;
	protected final IQAntGameResponseApi responseApi;

	public QAntGameApi() {
		this(null);
	}

	public QAntGameApi(QAntServer qant) {
		this.qant = qant;
		this.responseApi = new QAntGameResponseApi();
	}

	@Override
	public void setQAnt(QAntServer qant) {
		if (this.qant != null) {
			throw new IllegalStateException();
		}
		this.qant = qant;
		this.qantApi = qant.getAPIManager().getQAntApi();
	}

	@Override
	public IQAntGameResponseApi getResponseAPI() {
		return this.responseApi;
	}

	@Override
	public Room createGame(Zone zone, CreateQAntGameSettings settings, QAntUser owner) throws QAntCreateRoomException {
		return createGame(zone, settings, owner, true, true);
	}

	@Override
	public Room createGame(Zone zone, CreateQAntGameSettings settings, QAntUser owner, boolean fireClientEvent,
			boolean fireServerEvent) throws QAntCreateRoomException {
		Room roomToLeave = null;
		QAntGame theGame = null;
		if (settings.isLeaveLastJoinedRoom()) {
			roomToLeave = ((owner != null) ? owner.getLastJoinedRoom() : null);
		}
		if (!settings.isGamePublic()) {
			settings.setPassword(this.generateGamePassword());
		}
		theGame = (QAntGame) qantApi.createRoom(zone, settings, owner, true, roomToLeave, fireClientEvent,
				fireServerEvent);
		theGame.setPlayerMatchExpression(settings.getPlayerMatchExpression());
		theGame.setSpectatorMatchExpression(settings.getSpectatorMatchExpression());
		theGame.setLeaveLastRoomOnJoin(settings.isLeaveLastJoinedRoom());
		theGame.setMinPlayersToStartGame(settings.getMinPlayersToStartGame());
		theGame.setNotifyGameStarted(settings.isNotifyGameStartedViaRoomVariable());
		if (!theGame.isPublic()) {
			List<QAntUser> invitedPlayers = settings.getInvitedPlayers();
			if (invitedPlayers != null) {
				populateInvitations(owner, invitedPlayers, settings.getMinPlayersToStartGame(),
						settings.getSearchableRooms(), settings.getPlayerMatchExpression());
				IQAntObject inivitationParams = settings.getInvitationParams();
				inviteFriendsInGame(theGame, invitedPlayers, settings.isLeaveLastJoinedRoom(),
						settings.getInvitationExpiryTime(), inivitationParams);
				QAntTracer.info(this.getClass(),
						String.format("Game started: %s -- Invited people: %s", theGame, invitedPlayers));
			}
		}

		return theGame;
	}

	@Override
	public Room quickJoinGame(QAntUser player, MatchExpression expression, Zone zone, String groupId)
			throws QAntJoinRoomException {
		return quickJoinGame(player, expression, zone, groupId, null);
	}

	@Override
	public Room quickJoinGame(QAntUser player, MatchExpression expression, Zone zone, String groupId,
			final Room roomToLeave) throws QAntJoinRoomException {
		return this.quickJoinGame(player, expression, zone.getRoomListFromGroup(groupId), roomToLeave);
	}

	@Override
	public Room quickJoinGame(QAntUser player, MatchExpression expression, Collection<Room> searchableRooms,
			Room roomToLeave) throws QAntJoinRoomException {
		return quickJoinGame(player, expression, searchableRooms, roomToLeave, false);
	}

	@Override
	public void sendInvitation(Invitation invitation, InvitationCallback callBackHandler) {
		// qant.getInvitationManager().startInvitation(invitation,
		// callBackHandler);
		responseApi.notifyInivitation(invitation);
	}

	@Override
	public void sendInvitation(QAntUser inviter, List<QAntUser> invitees, int expirySeconds,
			InvitationCallback callBackHandler, IQAntObject params) {
		for (QAntUser invitee : invitees) {
			if (invitee != null) {
				Invitation invitation = new QAntInvitation(inviter, invitee, expirySeconds);
				invitation.setParams(params);
				qant.getInvitationManager().startInvitation(invitation, callBackHandler);
				responseApi.notifyInivitation(invitation);
			}
		}
	}

	@Override
	public void replyToInvitation(QAntUser invitedUser, int invitationId, InvitationResponse reply, IQAntObject params,
			boolean fireClientEvent) {
		try {
			qant.getInvitationManager().onInvitationResult(invitationId, reply, params);
		} catch (QAntInvitationException e) {
			QAntTracer.warn(this.getClass(), "Invitation Reply failure: " + e.getMessage());
			if (fireClientEvent) {
				qantApi.getResponseAPI().notifyRequestError(e.getErrorData(), invitedUser,
						SystemRequest.InvitationReply);
			}
		}
	}

	@Override
	public void sendJoinRoomInvitation(Room target, QAntUser inviter, List<QAntUser> invitees, int expirySeconds) {
		sendJoinRoomInvitation(target, inviter, invitees, expirySeconds, false, false, null);
	}

	@Override
	public void sendJoinRoomInvitation(Room target, QAntUser inviter, List<QAntUser> invitees, int expirySeconds,
			boolean asSpect, boolean leaveLastJoinedRoom) {
		sendJoinRoomInvitation(target, inviter, invitees, expirySeconds, asSpect, leaveLastJoinedRoom, null);
	}

	@Override
	public void sendJoinRoomInvitation(Room target, QAntUser inviter, List<QAntUser> invitees, int expirySeconds,
			boolean asSpect, boolean leaveLastJoinedRoom, IQAntObject params) {
		// boolean isServer = inviter == null;
		// boolean allowed = isServer ||
		// isJoinRoomInvitationSenderAllowed(inviter, target);
		// if (!allowed) {
		// throw new QAntRuntimeException(
		// "User: " + inviter.getName() + " is not allowed to join-invite people
		// in " + target.toString());
		// }
		// this.sendInvitation(inviter, invitees, expirySeconds, new
		// JoinRoomInvitationCallback(target), params);
	}

	private Room quickJoinGame(QAntUser player, MatchExpression expression, Collection<Room> searchableRooms,
			Room roomToLeave, boolean asSpectator) throws QAntJoinRoomException {
		if (searchableRooms == null || searchableRooms.size() < 1) {
			throw new IllegalArgumentException("No Room provided for searching.");
		}

		MatchExpression basicFilter = new MatchExpression("${ISG}", BoolMatch.EQUALS, true)
				.and("${ISP}", BoolMatch.EQUALS, false).and("${HFP}", BoolMatch.EQUALS, true);
		Collection<Room> possibleRooms = qantApi.findRooms(searchableRooms, basicFilter, 0);
		possibleRooms = qantApi.findRooms(possibleRooms, expression, 0);
		StringBuilder debugSb = new StringBuilder("Rooms available for QuickJoin:\n");
		for (Room item : possibleRooms) {
			debugSb.append(String.format("%s => %s%n", item.getName(), ((QAntGame) item).getPlayerMatchExpression()));
		}
		QAntTracer.debug(this.getClass(), debugSb.toString());

		Room theRoom = null;
		for (Room candidateRoom : possibleRooms) {
			if (!(candidateRoom instanceof QAntGame)) {
				theRoom = candidateRoom;
				break;
			}
			MatchExpression roomCriteria = ((QAntGame) candidateRoom).getPlayerMatchExpression();
			if (MatchingUtils.getInstance().matchUser(player, roomCriteria)) {
				theRoom = candidateRoom;
				break;
			}
		}
		if (theRoom != null) {
			qantApi.joinRoom(player, theRoom, null, asSpectator, roomToLeave);
			return theRoom;
		}
		String message = String.format("No Game Room was found for %s", player);
		QAntErrorData errData = new QAntErrorData(QAntErrorCode.JOIN_GAME_NOT_FOUND);
		throw new QAntQuickJoinGameException(message, errData);
	}

	private void populateInvitations(QAntUser inviter, List<QAntUser> invitedPlayers, int minPlayersToStart,
			List<QAntRoom> searchableRooms, MatchExpression exp) {

		if (invitedPlayers.size() < minPlayersToStart) {
			int peopleToSearch = minPlayersToStart - invitedPlayers.size();
			Set<QAntUser> setOfPlayers = new HashSet<QAntUser>(invitedPlayers);
			Collection<QAntUser> searchableUserList = null;
			if (searchableRooms.size() == 1) {
				searchableUserList = searchableRooms.get(0).getUserList();
			} else {
				searchableUserList = new HashSet<QAntUser>();
				for (Room room : searchableRooms) {
					searchableUserList.addAll(room.getUserList());
				}
			}
			List<QAntUser> srcResult = qantApi.findUsers(searchableUserList, exp, peopleToSearch);
			if (srcResult.size() > 0) {
				QAntTracer.debug(this.getClass(),
						"Players matching the game were found and added to the invitation: " + srcResult);
			} else {
				QAntTracer.debug(this.getClass(), "No other Players matching the game were found.");
			}
			setOfPlayers.addAll(srcResult);
			setOfPlayers.remove(inviter);
			invitedPlayers.clear();
			invitedPlayers.addAll(setOfPlayers);
		}
	}

	private void inviteFriendsInGame(QAntGame theGame, List<QAntUser> friends, boolean leaveLastJoinedRoom,
			int expiryTime, IQAntObject params) {

		QAntUser owner = theGame.getOwner();
		if (friends.size() == 0) {
			Map<IQAntEventParam, Object> evtParams = new HashMap<IQAntEventParam, Object>();
			evtParams.put(QAntEventParam.ZONE, theGame.getZone());
			evtParams.put(QAntEventParam.ROOM, theGame);
			qant.getEventManager().dispatchEvent(new QAntEvent(QAntEventType.GAME_INVITATION_FAILURE, evtParams));
			return;
		}

		// InvitationCallback callback = new QAntGameInvitationCallback(theGame,
		// friends.size(), leaveLastJoinedRoom);
		// if (owner != null) {
		// owner.getSession().setSystemProperty("InvitationProcessRunning",
		// (Object) theGame.getName());
		// }
		// final User inviter = (owner == null) ? UsersUtil.getServerAdmin() :
		// owner;
		// for (final User friend : friends) {
		// final Invitation invitation = new SFSInvitation(inviter, friend,
		// expiryTime, params);
		// this.qant.getInvitationManager().startInvitation(invitation,
		// callback);
		// this.responseApi.notifyInivitation(invitation);
		// }
	}

	private String generateGamePassword() {
		return String.valueOf(RandomStringUtils.randomAlphabetic(16)) + System.currentTimeMillis();
	}
}
