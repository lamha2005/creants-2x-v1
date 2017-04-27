package com.creants.creants_2x.socket.channels;

import java.io.IOException;
import java.util.List;

import com.creants.creants_2x.core.service.IService;

import io.netty.channel.Channel;

/**
 * @author LamHM
 *
 */
public interface IChannelManager extends IService {
	void addChannel(Channel channel);


	void removeChannel(Channel channel);


	Channel removeChannel(int id);


	boolean containsChannel(Channel channel);


	void shutDownLocalChannels();


	List<Channel> getAllChannels();


	Channel getChannelById(int id);


	int getHighestCCS();


	List<Channel> getAllLocalChannels();


	Channel getLocalChannelById(int id);


	int getLocalChannelCount();


	void onSocketDisconnected(Channel channel) throws IOException;


	Channel reconnectSession(Channel channel, String name) throws IOException;
}
