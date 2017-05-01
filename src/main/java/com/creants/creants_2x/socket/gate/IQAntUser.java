package com.creants.creants_2x.socket.gate;

import java.util.Iterator;

import com.creants.creants_2x.core.entities.Zone;

/**
 * @author LamHa
 *
 */
public interface IQAntUser {

	<V> V getAttribute(Object key, Class<V> clazz);

	Iterator<Object> getAttributeKeys();

	long getCreatedTime();

	Zone getZone();

	void setZone(Zone currentZone);

	byte getDeviceType();

	long getSessionId();

	String getLocale();

	String getPlatformInformation();

	byte getProtocolVersion();

	String getScreenSize();

	int getUserId();

	String getVersion();

	void initialize(String version, long sessionId, long clientId, byte deviceType, long createTime);

	void removeAttribute(Object key);

	void setAttribute(Object key, Object value);

	void setVersion(String version);

	byte getCurrentGameId();

	void setCurrentGameId(byte currentGameId);

}
