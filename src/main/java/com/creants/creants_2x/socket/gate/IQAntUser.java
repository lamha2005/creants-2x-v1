package com.creants.creants_2x.socket.gate;

import com.creants.creants_2x.core.entities.Zone;

/**
 * @author LamHa
 *
 */
public interface IQAntUser {

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


	void setVersion(String version);


	byte getCurrentGameId();


	void setCurrentGameId(byte currentGameId);


	Object getProperty(Object key);


	void setProperty(Object key, Object val);


	boolean containsProperty(Object key);


	void removeProperty(Object key);

}
