package com.creants.creants_2x.socket.codec;

import java.util.List;

import com.creants.creants_2x.socket.gate.entities.QAntObject;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * @author LamHa
 *
 */
public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		try {
			System.out.println("********************* DECODE ***************");
			QAntObject message = QAntObject.newFromBinaryData(in.array());
			// khi được add vào list sẽ remove
			out.add(message);
			in.clear();
		} catch (Exception e) {
			in.clear();
			throw new RuntimeException("Invalid messsage from decoder");
		}

	}

}
