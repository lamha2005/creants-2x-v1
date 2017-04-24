package com.creants.creants_2x.core.game;

import com.creants.creants_2x.core.entities.QAntRoom;
import com.creants.creants_2x.core.entities.match.MatchExpression;
import com.creants.creants_2x.core.exception.QAntJoinRoomException;
import com.creants.creants_2x.core.exception.QAntRoomException;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHM
 *
 */
public class QAntGame extends QAntRoom {

	private MatchExpression playerMatchExpression;
	private MatchExpression spectatorMatchExpression;
	private int minPlayersToStartGame;
	private boolean leaveLastRoomOnJoin;
	private boolean notifyGameStarted;
	private boolean gameStateChanged;

	public QAntGame(String name) {
		super(name);
		this.leaveLastRoomOnJoin = true;
		this.notifyGameStarted = false;
		this.gameStateChanged = false;
	}

	public MatchExpression getPlayerMatchExpression() {
		return this.playerMatchExpression;
	}

	public int getMinPlayersToStartGame() {
		return this.minPlayersToStartGame;
	}

	public void setPlayerMatchExpression(final MatchExpression exp) {
		if (this.playerMatchExpression != null) {
			throw new IllegalStateException("UserMatchExpression can't be modified at runtime");
		}
		this.playerMatchExpression = exp;
	}

	public MatchExpression getSpectatorMatchExpression() {
		return this.spectatorMatchExpression;
	}

	public void setSpectatorMatchExpression(final MatchExpression spectatorMatchExpression) {
		if (this.spectatorMatchExpression != null) {
			throw new IllegalStateException("UserMatchExpression can't be modified at runtime");
		}
		this.spectatorMatchExpression = spectatorMatchExpression;
	}

	public void setMinPlayersToStartGame(final int min) {
		this.minPlayersToStartGame = min;
	}

	public boolean isLeaveLastRoomOnJoin() {
		return this.leaveLastRoomOnJoin;
	}

	public void setLeaveLastRoomOnJoin(final boolean leaveLastRoomOnJoin) {
		this.leaveLastRoomOnJoin = leaveLastRoomOnJoin;
	}

	public boolean isNotifyGameStarted() {
		return this.notifyGameStarted;
	}

	public void setNotifyGameStarted(final boolean notifyGameStarted) {
		this.notifyGameStarted = notifyGameStarted;
	}

	public boolean isGameStarted() {
		return this.getSize().getUserCount() >= this.minPlayersToStartGame;
	}

	public boolean isGameStateChanged() {
		return this.gameStateChanged;
	}

	@Override
	public void addUser(QAntUser user, final boolean asSpectator) throws QAntJoinRoomException {
		boolean oldGameStarted = isGameStarted();
		super.addUser(user, asSpectator);
		boolean newGameStarted = isGameStarted();
		notifyGameStartedUpdate(this.gameStateChanged = (newGameStarted ^ oldGameStarted), newGameStarted);
	}

	@Override
	public void removeUser(QAntUser user) {
		boolean oldGameStarted = isGameStarted();
		super.removeUser(user);
		boolean newGameStarted = isGameStarted();
		notifyGameStartedUpdate(gameStateChanged = (newGameStarted ^ oldGameStarted), newGameStarted);
	}

	@Override
	public String toString() {
		return String.format("[ QAntGame: %s, Id: %s, Group: %s, public: %s, minPlayers: %s ]", this.getName(),
				this.getId(), this.getGroupId(), this.isPublic(), this.minPlayersToStartGame);
	}

	public void switchPlayerToSpectator(QAntUser user) throws QAntRoomException {
		boolean oldGameStarted = this.isGameStarted();
		// super.switchPlayerToSpectator(user);
		boolean newGameStarted = this.isGameStarted();
		notifyGameStartedUpdate(this.gameStateChanged = (newGameStarted ^ oldGameStarted), newGameStarted);
	}

	public void switchSpectatorToPlayer(QAntUser user) throws QAntRoomException {
		boolean oldGameStarted = isGameStarted();
		// super.switchSpectatorToPlayer(user);
		boolean newGameStarted = isGameStarted();
		notifyGameStartedUpdate(this.gameStateChanged = (newGameStarted ^ oldGameStarted), newGameStarted);
	}

	private void notifyGameStartedUpdate(boolean gameStateChanged, boolean gameStarted) {
	}

}
