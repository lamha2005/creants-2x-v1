package com.creants.creants_2x.core;

/**
 * @author LamHM
 *
 */
public enum GenericMessageType {
	PUBLIC_MSG("PUBLIC_MSG", 0),
	PRIVATE_MSG("PRIVATE_MSG", 1),
	MODERATOR_MSG("MODERATOR_MSG", 2),
	ADMING_MSG("ADMING_MSG", 3),
	OBJECT_MSG("OBJECT_MSG", 4),
	BUDDY_MSG("BUDDY_MSG", 5);

	private int id;


	private GenericMessageType(String s, int id) {
		this.id = id;
	}


	public int getId() {
		return this.id;
	}


	public static GenericMessageType fromId(int id) {
		GenericMessageType type = null;
		GenericMessageType[] values;
		for (int length = (values = values()).length, i = 0; i < length; ++i) {
			GenericMessageType item = values[i];
			if (item.getId() == id) {
				type = item;
				break;
			}
		}
		return type;
	}
}
