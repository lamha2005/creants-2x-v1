package com.creants.creants_2x.socket.gate.wood;

import com.creants.creants_2x.socket.gate.IMessageWriter;
import com.creants.creants_2x.socket.gate.MessageHandler;

/**
 * @author LamHa
 *
 */
public class MessageWriter implements IMessageWriter {
	private MessageHandler messageHandler;


	public MessageWriter(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}


	public MessageHandler getMessageHandler() {
		return messageHandler;
	}


	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

}
