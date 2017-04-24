package com.creants.creants_2x.socket.gate;

import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public interface IChannelService {
	void disconnect(long sessionId);

	void disconnect(QAntUser user);
}
