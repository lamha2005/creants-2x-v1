package com.creants.creants_2x.websocket.codec;

import com.creants.creants_2x.socket.gate.entities.QAntObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author LamHM
 *
 */
public class WebsocketEncoder extends MessageToByteEncoder<QAntObject> {

	@Override
	protected void encode(ChannelHandlerContext ctx, QAntObject message, ByteBuf out) throws Exception {
		try {
			ByteBuf data = Unpooled.copiedBuffer(message.toBinary());

			// build thành Websocket Data Frame Buffer
			int dataLen = data.readableBytes();
			out.ensureWritable(dataLen + 2);
			// Encode type.
			out.writeByte((byte) 0x82);

			if (data.capacity() <= 125) {
				out.writeByte(data.capacity());
			} else {
				out.writeByte((byte) 126);
				out.writeShort(data.capacity());
			}

			out.writeBytes(data);
		} catch (Exception e) {
			throw new RuntimeException("Invalid messsage");
		}

	}

}