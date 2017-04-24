package com.creants.creants_2x.core;

import com.creants.creants_2x.core.api.IQAntGameApi;
import com.creants.creants_2x.core.entities.IRoomFactory;
import com.creants.creants_2x.core.entities.invitation.InvitationManager;
import com.creants.creants_2x.core.util.QAntTracer;

/**
 * @author LamHa
 *
 */
public class ServiceProvider extends ObjectFactory implements IServiceProvider {
	private static final String DEFAULT_GAME_API = "com.creants.creants_2x.core.api.QAntGameApi";
	private static final String DEFAULT_ROOM_FACTORY = "com.creants.creants_2x.core.entities.DefaultRoomFactory";

	public ServiceProvider() {
	}

	@Override
	public IQAntGameApi getGameApi() {
		return (IQAntGameApi) this.makeInstance(DEFAULT_GAME_API);
	}

	@Override
	public IRoomFactory getRoomFactory() {
		return (IRoomFactory) this.makeInstance(DEFAULT_ROOM_FACTORY);
	}

	private Object makeInstance(final String serviceClass) {
		Object serviceImpl = null;
		try {
			serviceImpl = this.loadClass(serviceClass);
		} catch (Exception exc) {
			this.logServiceFail(serviceClass, exc);
		}
		return serviceImpl;
	}

	@Override
	public InvitationManager getInvitationManager() {
		return (InvitationManager) makeInstance(
				"com.creants.creants_2x.core.entities.invitation.QAntInvitationManager");
	}

	private void logServiceFail(final String serviceName, final Exception exc) {
		String msg = "Failed loading service: " + serviceName;
		exc.printStackTrace();

		QAntTracer.error(this.getClass(), "logServiceFail! msg: " + msg);
	}
}
