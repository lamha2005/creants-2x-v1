package com.creants.creants_2x.core.extension;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.IQAntEvent;
import com.creants.creants_2x.core.IQAntEventListener;
import com.creants.creants_2x.core.QAntEventType;
import com.creants.creants_2x.core.api.IQAntApi;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.exception.QAntRuntimeException;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHM
 *
 */
public abstract class BaseQAntExtension implements IQAntExtension, IQAntEventListener {
	private String name;
	private String fileName;
	private String configFileName;
	private ExtensionLevel level;
	private ExtensionType type;
	private Room parentRoom;
	private Zone parentZone;
	private volatile boolean active;
	private final QAntServer qant;
	private Properties configProperties;
	private ExtensionReloadMode reloadMode;
	private String currentPath;
	protected volatile int lagSimulationMillis;
	protected volatile int lagOscillation;
	private Random rnd;
	protected final IQAntApi qantApi;

	public BaseQAntExtension() {
		this.parentRoom = null;
		this.parentZone = null;
		this.lagSimulationMillis = 0;
		this.lagOscillation = 0;
		this.active = true;
		this.qant = QAntServer.getInstance();
		this.qantApi = qant.getAPIManager().getQAntApi();
	}

	public String getCurrentFolder() {
		return this.currentPath;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		if (this.name != null) {
			throw new QAntRuntimeException("Cannot redefine name of extension: " + this.toString());
		}

		this.name = name;
		this.currentPath = "extensions/" + name + "/";
	}

	@Override
	public String getExtensionFileName() {
		return this.fileName;
	}

	@Override
	public Properties getConfigProperties() {
		return this.configProperties;
	}

	@Override
	public String getPropertiesFileName() {
		return this.configFileName;
	}

	@Override
	public void setPropertiesFileName(String fileName) throws IOException {
		if (configFileName != null) {
			throw new QAntRuntimeException("Cannot redefine properties file name of an extension: " + this.toString());
		}

		boolean isDefault = false;
		if (fileName == null || fileName.length() == 0 || fileName.equals("config.properties")) {
			isDefault = true;
			configFileName = "config.properties";
		} else {
			configFileName = fileName;
		}
		String fileToLoad = "extensions/" + this.name + "/" + this.configFileName;
		if (isDefault) {
			loadDefaultConfigFile(fileToLoad);
		} else {
			loadCustomConfigFile(fileToLoad);
		}
	}

	public IQAntApi getApi() {
		return qantApi;
	}

	@Override
	public void handleServerEvent(IQAntEvent event) throws Exception {
	}

	@Override
	public Object handleInternalMessage(String cmdName, Object params) {
		return null;
	}

	private void loadDefaultConfigFile(String fileName) {
		this.configProperties = new Properties();
		try {
			this.configProperties.load(new FileInputStream(fileName));
		} catch (IOException ex) {
		}
	}

	private void loadCustomConfigFile(String fileName) throws IOException {
		(this.configProperties = new Properties()).load(new FileInputStream(fileName));
	}

	@Override
	public void setExtensionFileName(String fileName) {
		if (this.fileName != null) {
			throw new QAntRuntimeException("Cannot redefine file name of an extension: " + this.toString());
		}

		this.fileName = fileName;
	}

	@Override
	public Room getParentRoom() {
		return this.parentRoom;
	}

	@Override
	public void setParentRoom(final Room room) {
		if (this.parentRoom != null) {
			throw new QAntRuntimeException("Cannot redefine parent room of extension: " + this.toString());
		}

		this.parentRoom = room;
	}

	@Override
	public Zone getParentZone() {
		return this.parentZone;
	}

	@Override
	public void setParentZone(Zone zone) {
		if (this.parentZone != null) {
			throw new QAntRuntimeException("Cannot redefine parent zone of extension: " + this.toString());
		}
		this.parentZone = zone;
	}

	@Override
	public void addEventListener(QAntEventType eventType, IQAntEventListener listener) {
		if (this.level == ExtensionLevel.ZONE) {
			qant.getExtensionManager().addZoneEventListener(eventType, listener, this.parentZone);
		} else if (this.level == ExtensionLevel.ROOM) {
			qant.getExtensionManager().addRoomEventListener(eventType, listener, this.parentRoom);
		}
	}

	@Override
	public void removeEventListener(QAntEventType eventType, IQAntEventListener listener) {
		if (this.level == ExtensionLevel.ZONE) {
			qant.getExtensionManager().removeZoneEventListener(eventType, listener, this.parentZone);
		} else if (this.level == ExtensionLevel.ROOM) {
			qant.getExtensionManager().removeRoomEventListener(eventType, listener, this.parentRoom);
		}
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public void setActive(boolean flag) {
		this.active = flag;
	}

	@Override
	public ExtensionLevel getLevel() {
		return this.level;
	}

	@Override
	public void setLevel(ExtensionLevel level) {
		if (this.level != null) {
			throw new QAntRuntimeException("Cannot change level for extension: " + this.toString());
		}
		this.level = level;
	}

	@Override
	public ExtensionType getType() {
		return this.type;
	}

	@Override
	public void setType(ExtensionType type) {
		if (this.type != null) {
			throw new QAntRuntimeException("Cannot change type for extension: " + this.toString());
		}
		this.type = type;
	}

	@Override
	public ExtensionReloadMode getReloadMode() {
		return this.reloadMode;
	}

	@Override
	public void setReloadMode(ExtensionReloadMode mode) {
		if (this.reloadMode != null) {
			throw new QAntRuntimeException("Cannot change reloadMode for extension: " + this.toString());
		}

		this.reloadMode = mode;
	}

	@Override
	public void send(String cmdName, IQAntObject params, List<QAntUser> recipients) {
		checkLagSimulation();
		Room room = (this.level == ExtensionLevel.ROOM) ? this.parentRoom : null;
		qantApi.sendExtensionResponse(cmdName, params, recipients, room);
	}

	@Override
	public void send(String cmdName, IQAntObject params, QAntUser recipient) {
		checkLagSimulation();
		Room room = (this.level == ExtensionLevel.ROOM) ? this.parentRoom : null;
		qantApi.sendExtensionResponse(cmdName, params, recipient, room);
	}

	@Override
	public String toString() {
		return String.format("{ Ext: %s, Type: %s, Lev: %s, %s, %s }", this.name, this.type, this.level,
				this.parentZone, (this.parentRoom == null) ? "{}" : this.parentRoom);
	}

	private String getTraceMessage(Object[] args) {
		StringBuilder traceMsg = new StringBuilder().append("{").append(this.name).append("}: ");
		for (final Object o : args) {
			traceMsg.append(o.toString()).append(" ");
		}
		return traceMsg.toString();
	}

	protected void removeEventsForListener(IQAntEventListener listener) {
		if (this.level == ExtensionLevel.ZONE) {
			qant.getExtensionManager().removeListenerFromZone(listener, this.parentZone);
		} else if (this.level == ExtensionLevel.ROOM) {
			qant.getExtensionManager().removeListenerFromRoom(listener, this.parentRoom);
		}
	}

	private void checkLagSimulation() {
		if (this.lagSimulationMillis > 0) {
			try {
				long lagValue = this.lagSimulationMillis;
				if (this.lagOscillation > 0) {
					if (this.rnd == null) {
						this.rnd = new Random();
					}
					final int sign = (this.rnd.nextInt(100) > 49) ? 1 : -1;
					lagValue += sign * this.rnd.nextInt(this.lagOscillation);
				}
				QAntTracer.debug(this.getClass(), "Lag simulation, sleeping for: " + lagValue + "ms.");
				Thread.sleep(lagValue);
			} catch (InterruptedException e) {
				QAntTracer.warn(this.getClass(), "Interruption during lag simulation: " + e);
			}
		}
	}
}
