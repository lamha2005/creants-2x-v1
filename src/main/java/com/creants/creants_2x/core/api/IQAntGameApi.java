package com.creants.creants_2x.core.api;

import java.util.Collection;
import java.util.List;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.api.response.IQAntGameResponseApi;
import com.creants.creants_2x.core.entities.Invitation;
import com.creants.creants_2x.core.entities.InvitationCallback;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.entities.invitation.InvitationResponse;
import com.creants.creants_2x.core.entities.match.MatchExpression;
import com.creants.creants_2x.core.exception.QAntCreateRoomException;
import com.creants.creants_2x.core.exception.QAntJoinRoomException;
import com.creants.creants_2x.core.game.CreateQAntGameSettings;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public interface IQAntGameApi {
	IQAntGameResponseApi getResponseAPI();

	Room createGame(Zone zone, CreateQAntGameSettings setting, QAntUser user) throws QAntCreateRoomException;

	Room createGame(Zone zone, CreateQAntGameSettings setting, QAntUser user, boolean fireToClient,
			boolean fireToServer) throws QAntCreateRoomException;

	Room quickJoinGame(QAntUser user, MatchExpression expr, Zone zone, String roomName) throws QAntJoinRoomException;

	Room quickJoinGame(QAntUser user, MatchExpression expr, Zone zone, String roomName, Room room)
			throws QAntJoinRoomException;

	Room quickJoinGame(QAntUser user, MatchExpression expr, Collection<Room> rooms, Room room)
			throws QAntJoinRoomException;

	void sendInvitation(Invitation invite, InvitationCallback callback);

	void sendInvitation(QAntUser user, List<QAntUser> users, int roomId, InvitationCallback callback,
			IQAntObject params);

	void replyToInvitation(QAntUser user, int roomId, InvitationResponse invResponse, IQAntObject params,
			boolean isAccept);

	void sendJoinRoomInvitation(Room room, QAntUser user, List<QAntUser> users, int roomId, boolean fireToServer,
			boolean fireToClient, IQAntObject params);

	void sendJoinRoomInvitation(Room room, QAntUser user, List<QAntUser> users, int roomId, boolean fireToServer,
			boolean fireToClient);

	void sendJoinRoomInvitation(Room room, QAntUser user, List<QAntUser> users, int roomId);

	void setQAnt(QAntServer server);
}
