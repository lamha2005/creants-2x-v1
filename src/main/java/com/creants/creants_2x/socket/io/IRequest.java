package com.creants.creants_2x.socket.io;

import io.netty.channel.Channel;

/**
 * @author LamHa
 *
 */
public interface IRequest extends IEngineMessage {
	Channel getSender();


	void setSender(Channel sender);


	long getTimeStamp();


	void setTimeStamp(long requestTime);


	boolean isTcp();


	boolean isUdp();
}
