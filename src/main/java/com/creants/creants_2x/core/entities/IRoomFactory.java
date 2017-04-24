package com.creants.creants_2x.core.entities;

import com.creants.creants_2x.core.setting.CreateRoomSettings;

/**
 * @author LamHa
 *
 */
public interface IRoomFactory {
	Room createNewRoom(CreateRoomSettings setting);
}
