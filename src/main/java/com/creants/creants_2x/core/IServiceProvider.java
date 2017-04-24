package com.creants.creants_2x.core;

import com.creants.creants_2x.core.api.IQAntGameApi;
import com.creants.creants_2x.core.entities.IRoomFactory;
import com.creants.creants_2x.core.entities.invitation.InvitationManager;

/**
 * @author LamHa
 *
 */
public interface IServiceProvider {
	IQAntGameApi getGameApi();

	IRoomFactory getRoomFactory();

	InvitationManager getInvitationManager();

}
