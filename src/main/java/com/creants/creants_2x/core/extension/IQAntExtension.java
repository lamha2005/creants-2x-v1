package com.creants.creants_2x.core.extension;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.creants.creants_2x.core.IQAntEventListener;
import com.creants.creants_2x.core.QAntEventType;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.exception.QAntException;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHM
 *
 */
public interface IQAntExtension {
	void init();


	void destroy();


	String getName();


	void setName(String name);


	String getExtensionFileName();


	void setExtensionFileName(String extensionFileName);


	String getPropertiesFileName();


	void setPropertiesFileName(String propertiesFileName) throws IOException;


	Properties getConfigProperties();


	boolean isActive();


	void setActive(boolean isActive);


	void addEventListener(QAntEventType eventType, IQAntEventListener listener);


	void removeEventListener(QAntEventType eventType, IQAntEventListener event);


	void setLevel(ExtensionLevel extLevel);


	ExtensionLevel getLevel();


	ExtensionType getType();


	void setType(ExtensionType extType);


	Zone getParentZone();


	void setParentZone(Zone zone);


	Room getParentRoom();


	void setParentRoom(Room room);


	ExtensionReloadMode getReloadMode();


	void setReloadMode(ExtensionReloadMode reloadMode);


	void handleClientRequest(String cmdName, QAntUser user, IQAntObject params) throws QAntException;


	Object handleInternalMessage(String cmdNAme, Object obj);


	void send(String cmdName, IQAntObject params, QAntUser recipient);


	void send(String cmdName, IQAntObject params, List<QAntUser> user);
}
