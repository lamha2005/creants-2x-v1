package com.creants.creants_2x.core.entities.invitation;

/**
 * @author LamHa
 *
 */
public enum InvitationResponse {
	ACCEPT("ACCEPT", 0, 0), REFUSE("REFUSE", 1, 1), EXPIRED("EXPIRED", 2, 255);

	private int id;

	private InvitationResponse(String name, int value, int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public static InvitationResponse fromId(int id) {
		if (id == 0) {
			return InvitationResponse.ACCEPT;
		}
		if (id == 1) {
			return InvitationResponse.REFUSE;
		}

		return InvitationResponse.EXPIRED;
	}
}
