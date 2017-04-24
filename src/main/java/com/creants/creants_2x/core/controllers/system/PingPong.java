package com.creants.creants_2x.core.controllers.system;

import com.creants.creants_2x.core.controllers.BaseControllerCommand;
import com.creants.creants_2x.core.controllers.SystemRequest;
import com.creants.creants_2x.socket.io.IRequest;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @author LamHM
 *
 */
public class PingPong extends BaseControllerCommand {
	private static final AttributeKey<Long> KEY_LAST_PING_TIME = AttributeKey.valueOf("key_lastPingTime");
	private static final int MIN_PING_TIME = 900;


	public PingPong() {
		super(SystemRequest.PingPong);
	}


	@Override
	public void execute(final IRequest request) throws Exception {
		request.getSender().attr(KEY_LAST_PING_TIME).set(System.currentTimeMillis());
		api.getResponseAPI().sendPingPongResponse(request.getSender());
	}


	@Override
	public boolean validate(IRequest request) {
		boolean isOk = true;
		Channel sender = request.getSender();
		Long lastPing = sender.attr(KEY_LAST_PING_TIME).get();
		long now = System.currentTimeMillis();
		if (lastPing != null && now - lastPing < MIN_PING_TIME) {
			isOk = false;
		}

		return isOk;
	}

}
