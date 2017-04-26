package com.creants.creants_2x.core.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LamHM
 *
 */
public class ZoneSettings implements Serializable {
	private static final long serialVersionUID = 1129722650457265928L;
	public String name;
	public boolean isCustomLogin;
	public boolean isEncrypted;
	public int maxUsers;
	public List<RoomSettings> rooms;
	private static final transient AtomicInteger idGenerator;
	private transient Integer id;
	public String guestUserNamePrefix;
	public ExtensionSettings extension;

	static {
		idGenerator = new AtomicInteger(0);
	}


	public ZoneSettings() {
		this.name = "";
		this.isCustomLogin = true;
		this.isEncrypted = false;
		this.maxUsers = Integer.MAX_VALUE;
		this.guestUserNamePrefix = "Guest#";
		this.rooms = new ArrayList<RoomSettings>();
		this.extension = new ExtensionSettings();
		this.initId();

	}


	public int getId() {
		return this.id;
	}


	public void setGuestUserNamePrefix(String guestUserNamePrefix) {
		this.guestUserNamePrefix = guestUserNamePrefix;
	}


	public String getGuestUserNamePrefix() {
		return guestUserNamePrefix;
	}


	protected synchronized void initId() {
		this.id = ZoneSettings.idGenerator.getAndIncrement();
	}

	public static final class RoomSettings implements Serializable {
		private static final long serialVersionUID = 1L;
		public static final String EVENTS = "USER_ENTER_EVENT,USER_EXIT_EVENT,USER_COUNT_CHANGE_EVENT,USER_VARIABLES_UPDATE_EVENT";
		private static final AtomicInteger idGenerator;
		private transient Integer id;
		public String name;
		public String groupId;
		public String password;
		public int maxUsers;
		public int maxSpectators;
		public boolean isGame;
		public boolean isHidden;
		public String autoRemoveMode;
		public String events;
		public ExtensionSettings extension;

		static {
			idGenerator = new AtomicInteger();
		}


		public RoomSettings() {
			this.name = null;
			this.groupId = "default";
			this.password = null;
			this.maxUsers = Integer.MAX_VALUE;
			this.maxSpectators = 0;
			this.isGame = false;
			this.isHidden = false;
			this.autoRemoveMode = "DEFAULT";
			this.events = "USER_ENTER_EVENT,USER_EXIT_EVENT,USER_COUNT_CHANGE_EVENT,USER_VARIABLES_UPDATE_EVENT";
			this.getId();
		}


		public RoomSettings(final String name) {
			this();
			this.name = name;
			this.password = "";
			this.extension = new ExtensionSettings();
		}


		public int getId() {
			if (id == null) {
				id = getUniqueId();
			}
			return id;
		}


		private static int getUniqueId() {
			return RoomSettings.idGenerator.getAndIncrement();
		}


		public String getAvailableEvents() {
			return "USER_ENTER_EVENT,USER_EXIT_EVENT,USER_COUNT_CHANGE_EVENT,USER_VARIABLES_UPDATE_EVENT";
		}
	}

	public static final class ExtensionSettings implements Serializable {
		private static final long serialVersionUID = 1L;
		public String name;
		public String type;
		public String file;
		public String propertiesFile;
		public String reloadMode;


		public ExtensionSettings() {
			this.name = "";
			this.type = "JAVA";
			this.file = "";
			this.propertiesFile = "";
			this.reloadMode = "AUTO";
		}
	}

}
