package com.creants.creants_2x;

import java.net.SocketAddress;

import org.apache.log4j.PropertyConfigurator;

import com.creants.creants_2x.core.IQAntEventManager;
import com.creants.creants_2x.core.IServiceProvider;
import com.creants.creants_2x.core.QAntEventManager;
import com.creants.creants_2x.core.ServiceProvider;
import com.creants.creants_2x.core.api.APIManager;
import com.creants.creants_2x.core.entities.invitation.InvitationManager;
import com.creants.creants_2x.core.event.handler.SystemHandlerManager;
import com.creants.creants_2x.core.managers.IExtensionManager;
import com.creants.creants_2x.core.managers.QAntExtensionManager;
import com.creants.creants_2x.core.service.IService;
import com.creants.creants_2x.core.util.AppConfig;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.channels.IChannelManager;
import com.creants.creants_2x.socket.codec.MessageDecoder;
import com.creants.creants_2x.socket.codec.MessageEncoder;
import com.creants.creants_2x.socket.gate.MessageHandler;
import com.creants.creants_2x.socket.managers.IUserManager;
import com.creants.creants_2x.socket.managers.UserManager;
import com.creants.creants_2x.websocket.codec.WebsocketDecoder;
import com.creants.creants_2x.websocket.codec.WebsocketEncoder;
import com.creants.creants_2x.websocket.gate.HttpRequestHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author LamHM
 *
 */
public class QAntServer {
	private static QAntServer instance;
	private MessageHandler messageHandler;
	private SystemHandlerManager systemHandlerManager;
	private APIManager apiManager;
	private IUserManager userManager;
	private IQAntEventManager eventManager;
	private IChannelManager channelManager;
	private IExtensionManager extensionManager;
	private final IServiceProvider services;
	private InvitationManager invitationManager;

	public static QAntServer getInstance() {
		if (instance == null) {
			instance = new QAntServer();
		}

		return instance;
	}

	private QAntServer() {
		messageHandler = new MessageHandler();
		services = new ServiceProvider();
	}

	private void start() throws InterruptedException {
		QAntTracer.debug(this.getClass(), "======================== QUEEN ANT SOCKET =====================");
		initialize();

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			// Channel có thể hiểu như một socket connection
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(buildSocketChannelInitializer())
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.option(ChannelOption.SO_BACKLOG, 100).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture future = bootstrap.bind(AppConfig.getSocketIp(), AppConfig.getSocketPort()).sync();
			future.addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						SocketAddress localAddress = future.channel().localAddress();
						QAntTracer.info(this.getClass(), "SOCKET SERVER INFO:" + localAddress.toString());
					} else {
						QAntTracer.error(this.getClass(), "Bound attempt failed! ", future.cause().toString());
					}
				}
			});

			ServerBootstrap websocketBoostrap = new ServerBootstrap();
			websocketBoostrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(buildWebsocketChannelInitializer())
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.option(ChannelOption.SO_BACKLOG, 100).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture websocketChannelFuture = websocketBoostrap
					.bind(AppConfig.getWebsocketIp(), AppConfig.getWebsocketPort()).sync();

			websocketChannelFuture.addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						SocketAddress localAddress = future.channel().localAddress();
						QAntTracer.info(this.getClass(), "WEBSOCKET SERVER INFO:" + localAddress.toString());
					} else {
						QAntTracer.error(this.getClass(), "WEBSOCKET Bound attempt failed! ",
								future.cause().toString());
					}
				}
			});

			QAntTracer.debug(this.getClass(),
					"======================== QUEEN ANT SOCKET STARTED =====================");
			// chờ cho đới khi server socket đóng
			future.channel().closeFuture().sync();
			websocketChannelFuture.channel().closeFuture().sync();

		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}

	}

	private void initialize() {
		messageHandler.init();
		(apiManager = new APIManager()).init(null);
		(eventManager = new QAntEventManager()).init(null);
		userManager = new UserManager();
		eventManager = new QAntEventManager();
		extensionManager = new QAntExtensionManager();
		invitationManager = getServiceProvider().getInvitationManager();
		((IService) invitationManager).init((Object) null);

	}

	private ChannelInitializer<SocketChannel> buildWebsocketChannelInitializer() {
		return new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new HttpServerCodec(), new HttpObjectAggregator(64 * 1024),
						new WebsocketDecoder(), new WebsocketEncoder());
				ch.pipeline().addLast(new HttpRequestHandler("/ws"), new WebSocketServerProtocolHandler("/ws"),
						messageHandler);

			}

		};
	}

	private ChannelInitializer<SocketChannel> buildSocketChannelInitializer() {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new MessageDecoder(), new MessageEncoder(), messageHandler);
			}

		};
	}

	public IUserManager getUserManager() {
		return userManager;
	}

	public IChannelManager getChannelManager() {
		return channelManager;
	}

	public SystemHandlerManager getSystemHandlerManager() {
		return systemHandlerManager;
	}

	public APIManager getAPIManager() {
		return apiManager;
	}

	public IQAntEventManager getEventManager() {
		return eventManager;
	}

	public IExtensionManager getExtensionManager() {
		return extensionManager;
	}

	public IServiceProvider getServiceProvider() {
		return this.services;
	}

	public InvitationManager getInvitationManager() {
		return this.invitationManager;
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("log4j.configurationFile", "resources/log4j2.xml");
		PropertyConfigurator.configure("resources/log4j.properties");
		AppConfig.init("resources/application.properties");
		QAntServer.getInstance().start();
	}
}
