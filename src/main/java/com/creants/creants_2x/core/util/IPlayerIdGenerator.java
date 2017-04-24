package com.creants.creants_2x.core.util;

import com.creants.creants_2x.core.entities.Room;

/**
 * @author LamHM
 *
 */
public interface IPlayerIdGenerator {
	void init();


	int getPlayerSlot();


	void freePlayerSlot(int index);


	void onRoomResize();


	void setParentRoom(Room room);


	Room getParentRoom();
}
