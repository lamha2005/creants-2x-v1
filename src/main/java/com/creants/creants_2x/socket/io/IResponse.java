package com.creants.creants_2x.socket.io;

import java.util.Collection;

import io.netty.channel.Channel;

/**
 * @author LamHM
 *
 */
public interface IResponse extends IEngineMessage {

	Collection<Channel> getRecipients();


	void setRecipients(Collection<Channel> channels);


	void setRecipients(Channel channel);


	boolean isTCP();


	boolean isUDP();


	void write();


	void write(int delay);
}
