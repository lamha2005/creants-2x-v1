package com.creants.creants_2x.socket.gate;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.creants.creants_2x.core.controllers.DefaultControllerManager;
import com.creants.creants_2x.core.controllers.IController;
import com.creants.creants_2x.core.controllers.IControllerManager;
import com.creants.creants_2x.core.event.SystemNetworkConstant;
import com.creants.creants_2x.core.util.DefaultMessageFactory;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.ChannelService;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.io.IRequest;
import com.creants.creants_2x.socket.io.Request;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Class tiếp nhận message từ client. Xử lý business logic.<br>
 * Share giữa các channel giúp giảm thiểu resource (chú ý Channel Handler phải
 * là stateless).<br>
 * inbound là data từ ứng dụng đến server(remote peer)<br>
 * outbound là data từ server(remote peer) đến ứng dụng (ví dụ như hành động
 * write) com.smartfoxserver.bitswarm.io.protocols.AbstractProtocolCodec
 * 
 * @author LamHa
 */
@Sharable
public class MessageHandler extends SimpleChannelInboundHandler<IQAntObject> {
	private static final String CONTROLLER_ID = "c";
	private static final String ACTION_ID = "a";
	private static final String PARAM_ID = "p";

	private static final AtomicLong nextSessionId = new AtomicLong(System.currentTimeMillis());
	private static final ChannelService channelService = ChannelService.getInstance();

	protected IControllerManager controllerManager;


	public MessageHandler() {
		
	}


	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		synchronized (nextSessionId) {
			long sessionId = nextSessionId.getAndIncrement();
			QAntTracer.debug(this.getClass(), "- create session: " + sessionId);
			QAntUser user = channelService.connect(sessionId, channel);
			send(user, DefaultMessageFactory.createConnectMessage(sessionId));
		}

	}
	
	public void init(){
		controllerManager = getControllerManager();
	}


	/**
	 * @return
	 */
	private IControllerManager getControllerManager() {
		DefaultControllerManager controllerManager = new DefaultControllerManager();
		controllerManager.init(null);
		return controllerManager;
	}


	/*
	 * Chú ý khi xử lý message là có nhiều thread xử lý IO, do đó cố gắng không
	 * Block IO Thread có thể có vấn đề về performance vì phải duyệt sâu đối với
	 * những môi trường throughout cao. Netty hỗ trợ EventExecutorGroup để giải
	 * quyết vấn đề này khi add vào ChannelHandlers. Nó sẽ sử dụng EventExecutor
	 * thực thi tất các phương thức của ChannelHandler. EventExecutor sẽ sử dụng
	 * một thread khác để xử lý IO sau đó giải phóng EventLoop.
	 */
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final IQAntObject message) throws Exception {
		Channel channel = ctx.channel();
		if (message.isNull(CONTROLLER_ID)) {
			throw new IllegalStateException("Request rejected: No Controller ID in request!");
		}

		if (message.isNull(ACTION_ID)) {
			throw new IllegalStateException("Request rejected: No Action ID in request!");
		}

		if (message.isNull(PARAM_ID)) {
			throw new IllegalStateException("Request rejected: Missing parameters field!");
		}

		IRequest request = new Request();
		request.setId(message.getShort(ACTION_ID));
		request.setContent(message.getQAntObject(PARAM_ID));
		request.setSender(channel);

		this.dispatchRequestToController(request, message.getByte(CONTROLLER_ID));

	}


	protected void dispatchRequestToController(IRequest request, byte controllerId) {
		try {
			IController controller = controllerManager.getControllerById(controllerId);
			controller.enqueueRequest(request);
		} catch (Exception err) {
			QAntTracer.warn(this.getClass(), "Can't handle this request! The related controller is not found: "
					+ controllerId + ", Request: " + request);
		}
	}


	/**
	 * @param receiver
	 *            người nhận
	 * @param message
	 */
	public void send(IQAntUser receiver, final IQAntObject message) {
		Channel channel = channelService.getChannel(receiver.getSessionId());
		if (channel != null) {
			ChannelFuture future = channel.writeAndFlush(message);

			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					String cmdId = message.getUtfString(SystemNetworkConstant.KEYS_COMMAND_ID);
					QAntTracer.debug(this.getClass(), "- Send command:" + cmdId);
				}
			});
		}
	}


	/**
	 * Send cho nhóm user
	 * 
	 * @param receivers
	 *            danh sách người nhận
	 * @param message
	 */
	public void send(List<QAntUser> receivers, final IQAntObject message) {
		for (IQAntUser receiver : receivers) {
			send(receiver, message);
		}
	}


	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// flush tất cả những message trước đó (những message đang pending) đến
		// remote peer, và đóng channel sau khi write hoàn thành
		// ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}


	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
	}


	public void removeUser(QAntUser user) {
		channelService.getChannel(user.getSessionId()).close();
		channelService.disconnect(user);
	}

}
