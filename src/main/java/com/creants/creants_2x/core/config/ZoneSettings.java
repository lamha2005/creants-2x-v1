package com.creants.creants_2x.core.config;

import java.io.Serializable;
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


	public ZoneSettings() {
		name = "";
		isCustomLogin = true;
		isEncrypted = false;
		maxUsers = Integer.MAX_VALUE;

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
			if (this.id == null) {
				this.id = getUniqueId();
			}
			return this.id;
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
