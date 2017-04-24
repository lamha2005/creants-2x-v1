package com.creants.creants_2x.websocket.codec;

import java.util.List;

import com.creants.creants_2x.socket.gate.entities.QAntObject;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * @author LamHM
 *
 */
public class WebsocketDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {
	@Override
	protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame frame, List<Object> out) throws Exception {
		final ByteBuf in = frame.content();
		try {
			byte[] dst = new byte[in.capacity()];
			in.readBytes(dst);
			out.add(QAntObject.newFromBinaryData(dst));
			in.clear();
		} catch (Exception e) {
			in.clear();
			throw new RuntimeException("Invalid messsage from decoder");
		}

	}

}