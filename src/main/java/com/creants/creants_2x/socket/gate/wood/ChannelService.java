package com.creants.creants_2x.socket.gate.wood;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.creants.creants_2x.socket.gate.IChannelService;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author LamHa
 *
 */
public class ChannelService implements IChannelService {
	private static ChannelService instance;
	private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static final AttributeKey<Long> SESSION_ID = AttributeKey.valueOf("dispatcher.sessionId");
	private static final AttributeKey<QAntUser> USER = AttributeKey.valueOf("dispatcher.user");
	private Map<Long, Channel> channelKeyMap;


	public static ChannelService getInstance() {
		if (instance == null) {
			instance = new ChannelService();
		}
		return instance;
	}


	private ChannelService() {
		channelKeyMap = new ConcurrentHashMap<Long, Channel>();
	}


	public QAntUser connect(long sessionId, Channel channel) {
		channel.attr(SESSION_ID).set(sessionId);
		QAntUser user = new QAntUser();

		user.setSessionId(sessionId);
		user.setClientIp(channel.localAddress().toString().substring(1));
		channel.attr(USER).set(user);

		channels.add(channel);
		channelKeyMap.put(sessionId, channel);
		return user;
	}


	@Override
	public void disconnect(long sessionId) {
		Channel channel = getChannel(sessionId);
		channel.disconnect();
	}


	/**
	 * Set đối tượng device info cho Channel
	 */
	public void setDeviceInfo() {

	}


	public Channel getChannel(long session) {
		return channelKeyMap.get(session);
	}


	/**
	 * Remove by Object
	 * 
	 * @param channel
	 */
	public void disconnect(QAntUser user) {
		channelKeyMap.remove(user.getSessionId());
	}


	public long getSessionId(Channel channel) {
		return channel.attr(SESSION_ID).get();
	}


	public QAntUser getUser(Channel channel) {
		return channel.attr(USER).get();
	}

}
