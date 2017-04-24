package com.creants.creants_2x.core.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author LamHa
 *
 */
public class AppConfig {
	private static String socketIp;
	private static int socketPort;
	private static String websocketIp;
	private static int websocketPort;


	public static void init(String configPath) {
		Properties prop = new Properties();
		try (InputStream input = new FileInputStream(configPath)) {
			prop.load(input);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		socketIp = prop.getProperty("socket.ip", "localhost");
		socketPort = Integer.parseInt(prop.getProperty("socket.port", "8888"));
		websocketIp = prop.getProperty("websocket.ip", "localhost");
		websocketPort = Integer.parseInt(prop.getProperty("websocket.port", "8889"));
	}


	public static String getSocketIp() {
		return socketIp;
	}


	public static int getSocketPort() {
		return socketPort;
	}


	public static String getWebsocketIp() {
		return websocketIp;
	}


	public static int getWebsocketPort() {
		return websocketPort;
	}

}
