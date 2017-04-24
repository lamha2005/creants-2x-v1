package com.creants.creants_2x.core.controllers;

/**
 * @author LamHa
 *
 */
public enum SystemRequest {
	Handshake("Handshake", 0, (short) 0),
	Login("Login", 1, (short) 1),
	Logout("Logout", 2, (short) 2),
	GetRoomList("GetRoomList", 3, (short) 3),
	JoinRoom("JoinRoom", 4, (short) 4),
	AutoJoin("AutoJoin", 5, (short) 5),
	CreateRoom("CreateRoom", 6, (short) 6),
	GenericMessage("GenericMessage", 7, (short) 7),
	ChangeRoomName("ChangeRoomName", 8, (short) 8),
	ChangeRoomPassword("ChangeRoomPassword", 9, (short) 9),
	ObjectMessage("ObjectMessage", 10, (short) 10),
	SetRoomVariables("SetRoomVariables", 11, (short) 11),
	SetUserVariables("SetUserVariables", 12, (short) 12),
	CallExtension("CallExtension", 13, (short) 13),
	LeaveRoom("LeaveRoom", 14, (short) 14),
	SubscribeRoomGroup("SubscribeRoomGroup", 15, (short) 15),
	UnsubscribeRoomGroup("UnsubscribeRoomGroup", 16, (short) 16),
	SpectatorToPlayer("SpectatorToPlayer", 17, (short) 17),
	PlayerToSpectator("PlayerToSpectator", 18, (short) 18),
	ChangeRoomCapacity("ChangeRoomCapacity", 19, (short) 19),
	PublicMessage("PublicMessage", 20, (short) 20),
	PrivateMessage("PrivateMessage", 21, (short) 21),
	ModeratorMessage("ModeratorMessage", 22, (short) 22),
	AdminMessage("AdminMessage", 23, (short) 23),
	KickUser("KickUser", 24, (short) 24),
	BanUser("BanUser", 25, (short) 25),
	ManualDisconnection("ManualDisconnection", 26, (short) 26),
	FindRooms("FindRooms", 27, (short) 27),
	FindUsers("FindUsers", 28, (short) 28),
	PingPong("PingPong", 29, (short) 29),
	SetUserPosition("SetUserPosition", 30, (short) 30),
	InitBuddyList("InitBuddyList", 31, (short) 200),
	AddBuddy("AddBuddy", 32, (short) 201),
	BlockBuddy("BlockBuddy", 33, (short) 202),
	RemoveBuddy("RemoveBuddy", 34, (short) 203),
	SetBuddyVariables("SetBuddyVariables", 35, (short) 204),
	GoOnline("GoOnline", 36, (short) 205),
	BuddyMessage("BuddyMessage", 37, (short) 206),
	InviteUser("InviteUser", 38, (short) 300),
	InvitationReply("InvitationReply", 39, (short) 301),
	CreateQAntGame("CreateQAntGame", 40, (short) 302),
	QuickJoinGame("QuickJoinGame", 41, (short) 303),
	OnEnterRoom("OnEnterRoom", 42, (short) 1000),
	OnRoomCountChange("OnRoomCountChange", 43, (short) 1001),
	OnUserLost("OnUserLost", 44, (short) 1002),
	OnRoomLost("OnRoomLost", 45, (short) 1003),
	OnUserExitRoom("OnUserExitRoom", 46, (short) 1004),
	OnClientDisconnection("OnClientDisconnection", 47, (short) 1005),
	OnReconnectionFailure("OnReconnectionFailure", 48, (short) 1006),
	OnMMOItemVariablesUpdate("OnMMOItemVariablesUpdate", 49, (short) 1007);
	private short id;


	public static SystemRequest fromId(short id) {
		SystemRequest req = null;
		SystemRequest[] values;
		for (int length = (values = values()).length, i = 0; i < length; ++i) {
			final SystemRequest item = values[i];
			if (item.getId() == id) {
				req = item;
				break;
			}
		}
		return req;
	}


	private SystemRequest(String s, int n, short id) {
		this.id = id;
	}


	public short getId() {
		return id;
	}
}
