package com.creants.creants_2x.socket.channels;

import com.creants.creants_2x.core.service.IService;

import io.netty.channel.Channel;

/**
 * @author LamHM
 *
 */
public interface IChannelManager extends IService {
	void addChannel(Channel channel);


	void removeChannel(Channel channel);


	Channel removeChannel(int channelId);


	Channel removeChannel(String name);


	boolean containsChannel(Channel channel);
}
