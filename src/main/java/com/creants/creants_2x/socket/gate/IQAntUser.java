package com.creants.creants_2x.socket.gate;

import java.util.Iterator;

/**
 * @author LamHa
 *
 */
public interface IQAntUser {

	<V> V getAttribute(Object key, Class<V> clazz);


	Iterator<Object> getAttributeKeys();


	long getCreatedTime();


	byte getDeviceType();


	long getSessionId();


	String getLocale();


	String getPlatformInformation();


	byte getProtocolVersion();


	String getScreenSize();


	int getUserId();


	String getUserName();


	String getVersion();


	void initialize(String version, long sessionId, long clientId, byte deviceType, long createTime);


	void removeAttribute(Object key);


	void setAttribute(Object key, Object value);


	void setVersion(String version);


	byte getCurrentGameId();


	void setCurrentGameId(byte currentGameId);

}
