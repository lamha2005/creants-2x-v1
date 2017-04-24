package com.creants.creants_2x.core.entities;

import com.creants.creants_2x.core.game.CreateQAntGameSettings;
import com.creants.creants_2x.core.game.QAntGame;
import com.creants.creants_2x.core.setting.CreateRoomSettings;

/**
 * @author LamHa
 *
 */
public class DefaultRoomFactory implements IRoomFactory {
	@Override
	public Room createNewRoom(CreateRoomSettings settings) {
		Room newRoom;
		if (settings instanceof CreateQAntGameSettings) {
			newRoom = new QAntGame(settings.getName());
			settings.setGame(true);
		} else {
			newRoom = new QAntRoom(settings.getName());
		}
		return newRoom;
	}
}
