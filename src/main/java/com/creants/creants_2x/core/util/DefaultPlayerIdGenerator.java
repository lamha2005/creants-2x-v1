package com.creants.creants_2x.core.util;

import java.util.Arrays;

import com.creants.creants_2x.core.entities.Room;

/**
 * @author LamHa
 *
 */
public class DefaultPlayerIdGenerator implements IPlayerIdGenerator {
	private Room parentRoom;
	private volatile Boolean[] playerSlots;

	public DefaultPlayerIdGenerator() {
	}

	@Override
	public void init() {
		Arrays.fill(playerSlots = new Boolean[parentRoom.getMaxUsers() + 1], Boolean.FALSE);
	}

	@Override
	public int getPlayerSlot() {
		int playerId = 0;
		synchronized (playerSlots) {
			for (int ii = 1; ii < playerSlots.length; ++ii) {
				if (!playerSlots[ii]) {
					playerId = ii;
					playerSlots[ii] = Boolean.TRUE;
					break;
				}
			}
		}

		if (playerId < 1) {
			QAntTracer.warn(this.getClass(), "No player slot found in " + this.parentRoom);
		}
		return playerId;
	}

	@Override
	public void freePlayerSlot(int playerId) {
		if (playerId == -1) {
			return;
		}
		if (playerId >= playerSlots.length) {
			return;
		}

		synchronized (playerSlots) {
			playerSlots[playerId] = Boolean.FALSE;
		}
	}

	@Override
	public void onRoomResize() {
		Boolean[] newPlayerSlots = new Boolean[parentRoom.getMaxUsers() + 1];
		synchronized (playerSlots) {
			for (int i = 1; i < newPlayerSlots.length; ++i) {
				if (i < playerSlots.length) {
					newPlayerSlots[i] = playerSlots[i];
				} else {
					newPlayerSlots[i] = Boolean.FALSE;
				}
			}
		}

		this.playerSlots = newPlayerSlots;
	}

	@Override
	public Room getParentRoom() {
		return parentRoom;
	}

	@Override
	public void setParentRoom(Room room) {
		parentRoom = room;
	}
}
