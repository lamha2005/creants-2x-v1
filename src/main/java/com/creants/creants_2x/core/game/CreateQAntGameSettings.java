package com.creants.creants_2x.core.game;

import java.util.List;

import com.creants.creants_2x.core.entities.QAntRoom;
import com.creants.creants_2x.core.entities.match.MatchExpression;
import com.creants.creants_2x.core.setting.CreateRoomSettings;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public class CreateQAntGameSettings extends CreateRoomSettings {
	private boolean gamePublic;
	private int minPlayersToStartGame;
	private List<QAntUser> invitedPlayers;
	private List<QAntRoom> searchableRooms;
	private boolean leaveLastJoinedRoom;
	private MatchExpression playerMatchExpression;
	private MatchExpression spectatorMatchExpression;
	private int invitationExpiryTime;
	private boolean notifyGameStartedViaRoomVariable;
	private IQAntObject invitationParams;

	public static CreateQAntGameSettings newFromRoomSettings(CreateRoomSettings rSettings) {
		CreateQAntGameSettings gameSettings = new CreateQAntGameSettings();
		gameSettings.setCustomPlayerIdGeneratorClass(rSettings.getCustomPlayerIdGeneratorClass());
		gameSettings.setExtension(rSettings.getExtension());
		gameSettings.setGroupId(rSettings.getGroupId());
		gameSettings.setHidden(rSettings.isHidden());
		gameSettings.setMaxSpectators(rSettings.getMaxSpectators());
		gameSettings.setMaxUsers(rSettings.getMaxUsers());
		gameSettings.setMaxVariablesAllowed(rSettings.getMaxVariablesAllowed());
		gameSettings.setName(rSettings.getName());
		return gameSettings;
	}

	public CreateQAntGameSettings() {
		this.leaveLastJoinedRoom = true;
		this.invitationExpiryTime = 10;
		this.notifyGameStartedViaRoomVariable = false;
		this.setGame(true);
	}

	public boolean isGamePublic() {
		return this.gamePublic;
	}

	public void setGamePublic(final boolean isGamePublic) {
		this.gamePublic = isGamePublic;
	}

	public int getMinPlayersToStartGame() {
		return this.minPlayersToStartGame;
	}

	public void setMinPlayersToStartGame(int minPlayersToStartGame) {
		this.minPlayersToStartGame = minPlayersToStartGame;
	}

	public List<QAntUser> getInvitedPlayers() {
		return this.invitedPlayers;
	}

	public void setInvitedPlayers(List<QAntUser> invitedPlayers) {
		this.invitedPlayers = invitedPlayers;
	}

	public List<QAntRoom> getSearchableRooms() {
		return this.searchableRooms;
	}

	public void setSearchableRooms(final List<QAntRoom> searchableRooms) {
		this.searchableRooms = searchableRooms;
	}

	public boolean isLeaveLastJoinedRoom() {
		return this.leaveLastJoinedRoom;
	}

	public void setLeaveLastJoinedRoom(boolean leaveLastJoinedRoom) {
		this.leaveLastJoinedRoom = leaveLastJoinedRoom;
	}

	public MatchExpression getPlayerMatchExpression() {
		return this.playerMatchExpression;
	}

	public void setPlayerMatchExpression(MatchExpression playerMatchExpression) {
		this.playerMatchExpression = playerMatchExpression;
	}

	public MatchExpression getSpectatorMatchExpression() {
		return this.spectatorMatchExpression;
	}

	public void setSpectatorMatchExpression(MatchExpression spectatorMatchExpression) {
		this.spectatorMatchExpression = spectatorMatchExpression;
	}

	public int getInvitationExpiryTime() {
		return this.invitationExpiryTime;
	}

	public void setInvitationExpiryTime(int invitationExpiryTime) {
		this.invitationExpiryTime = invitationExpiryTime;
	}

	public boolean isNotifyGameStartedViaRoomVariable() {
		return this.notifyGameStartedViaRoomVariable;
	}

	public void setNotifyGameStartedViaRoomVariable(boolean notifyGameStartedWithRoomVariable) {
		this.notifyGameStartedViaRoomVariable = notifyGameStartedWithRoomVariable;
	}

	public IQAntObject getInvitationParams() {
		return this.invitationParams;
	}

	public void setInvitationParams(IQAntObject invitationParams) {
		this.invitationParams = invitationParams;
	}

	@Override
	public String toString() {
		final String dump = String.valueOf(super.toString()) + " <<< QAntGame" + " Properties >>>" + "\n"
				+ "=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--" + "\n"
				+ "isGamePublic: " + this.isGamePublic() + "\n" + "minPlayersToStartGame: "
				+ this.getMinPlayersToStartGame() + "\n" + "invitedPlayers: " + this.getInvitedPlayers() + "\n"
				+ "playerMatchExp: " + this.getPlayerMatchExpression() + "\n" + "spectMatchExp: "
				+ this.getSpectatorMatchExpression() + "\n" + "invExpiryTime: " + this.getInvitationExpiryTime() + "\n"
				+ "notifyGameStarted: " + this.isNotifyGameStartedViaRoomVariable() + "\n" + "invitation params: "
				+ this.getInvitationParams().getDump() + "\n"
				+ "=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--\n";
		return dump;
	}
}
