package com.creants.creants_2x.socket.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @author LamHM
 *
 */
public class Response extends AbstractEngineMessage implements IResponse {
	private Collection<Channel> channels;


	@Override
	public Collection<Channel> getRecipients() {
		return channels;
	}


	@Override
	public void setRecipients(Collection<Channel> channels) {
		this.channels = channels;
	}


	@Override
	public void setRecipients(Channel channel) {
		List<Channel> recipients = new ArrayList<Channel>();
		recipients.add(channel);
		this.setRecipients(recipients);
	}


	@Override
	public boolean isTCP() {
		return true;
	}


	@Override
	public boolean isUDP() {
		return false;
	}


	@Override
	public void write() {
		final IQAntObject message = getContent();
		message.putShort("c", getId());
		for (Channel channel : channels) {
			ChannelFuture future = channel.writeAndFlush(message);
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					short cmdId = message.getShort("c");
					QAntTracer.debug(this.getClass(), "- Send command:" + cmdId);
				}
			});
		}
	}


	@Override
	public void write(int delay) {

	}

}
