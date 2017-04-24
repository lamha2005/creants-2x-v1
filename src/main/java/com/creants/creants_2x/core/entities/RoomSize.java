package com.creants.creants_2x.core.entities;

/**
 * @author LamHM
 *
 */
public class RoomSize {
	private int userCount;
	private int spectatorCount;


	public RoomSize(int userCount, int spectatorCount) {
		this.userCount = userCount;
		this.spectatorCount = spectatorCount;
	}


	public int getUserCount() {
		return userCount;
	}


	public int getSpectatorCount() {
		return spectatorCount;
	}


	public int getTotalUsers() {
		return getUserCount() + getSpectatorCount();
	}


	@Override
	public String toString() {
		return String.format("{ u: %s, s: %s }", userCount, spectatorCount);
	}
}
