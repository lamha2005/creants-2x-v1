package com.creants.creants_2x.core.setting;

import java.util.Map;
import java.util.Set;

import com.creants.creants_2x.core.util.IPlayerIdGenerator;


/**
 * @author LamHM
 *
 */
public class CreateRoomSettings {
	private String name;
	private String groupId;
	private String password;
	private int maxUsers;
	private int maxSpectators;
	private int maxVariablesAllowed;
	private boolean isGame;
	private boolean isHidden;
	private RoomExtensionSettings extension;
	private Set<QAntRoomSettings> roomSettings;
	private Class<? extends IPlayerIdGenerator> customPlayerIdGeneratorClass;
	private Map<Object, Object> roomProperties;


	public CreateRoomSettings() {
		this.name = null;
		this.groupId = "default";
		this.password = null;
		this.maxUsers = 20;
		this.maxSpectators = 0;
		this.maxVariablesAllowed = 5;
		this.isGame = false;
		this.isHidden = false;
	}


	public String getName() {
		return this.name;
	}


	public void setName(final String name) {
		this.name = name;
	}


	public String getGroupId() {
		return this.groupId;
	}


	public void setGroupId(final String groupId) {
		this.groupId = groupId;
	}


	public String getPassword() {
		return this.password;
	}


	public void setPassword(final String password) {
		this.password = password;
	}


	public int getMaxUsers() {
		return this.maxUsers;
	}


	public void setMaxUsers(final int maxUsers) {
		this.maxUsers = maxUsers;
	}


	public int getMaxSpectators() {
		return this.maxSpectators;
	}


	public void setMaxSpectators(final int maxSpectators) {
		this.maxSpectators = maxSpectators;
	}


	public boolean isGame() {
		return this.isGame;
	}


	public void setGame(final boolean isGame) {
		this.isGame = isGame;
	}


	public boolean isHidden() {
		return this.isHidden;
	}


	public void setHidden(final boolean isHidden) {
		this.isHidden = isHidden;
	}


	public RoomExtensionSettings getExtension() {
		return this.extension;
	}


	public void setExtension(final RoomExtensionSettings extension) {
		this.extension = extension;
	}


	public int getMaxVariablesAllowed() {
		return this.maxVariablesAllowed;
	}


	public void setMaxVariablesAllowed(final int maxVariablesAllowed) {
		this.maxVariablesAllowed = maxVariablesAllowed;
	}


	public Class<? extends IPlayerIdGenerator> getCustomPlayerIdGeneratorClass() {
		return this.customPlayerIdGeneratorClass;
	}


	public void setCustomPlayerIdGeneratorClass(
			final Class<? extends IPlayerIdGenerator> customPlayerIdGeneratorClass) {
		this.customPlayerIdGeneratorClass = customPlayerIdGeneratorClass;
	}


	public Map<Object, Object> getRoomProperties() {
		return this.roomProperties;
	}


	public void setRoomProperties(final Map<Object, Object> roomProperties) {
		this.roomProperties = roomProperties;
	}


	@Override
	public String toString() {
		final String dump = "=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--\nName: "
				+ this.name + "\n" + "Group: " + this.groupId + "\n" + "Passw: " + this.password + "\n" + "\n"
				+ "isGame: " + this.isGame + "\n" + "isHidden: " + this.isHidden + "\n" + "MaxUsers: " + this.maxUsers
				+ "\n" + "MaxSpect: " + this.maxSpectators + "\n" + "MaxVars: " + this.maxVariablesAllowed + "\n"
				+ "Settings: " + this.roomSettings + "\n" + "Extension: " + this.extension + "\n" + "PlayerIdGen: "
				+ this.customPlayerIdGeneratorClass + "\n"
				+ "=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--=--\n";
		return dump;
	}

	public static final class RoomExtensionSettings {
		private String id;
		private String className;
		private String propertiesFile;


		public RoomExtensionSettings(final String id, final String className) {
			this.id = id;
			this.className = className;
		}


		public String getId() {
			return this.id;
		}


		public String getClassName() {
			return this.className;
		}


		public void setPropertiesFile(final String propertiesFile) {
			this.propertiesFile = propertiesFile;
		}


		public String getPropertiesFile() {
			return this.propertiesFile;
		}


		@Override
		public String toString() {
			return String.format("%s, %s, %s", this.id, this.className, this.propertiesFile);
		}
	}
}
