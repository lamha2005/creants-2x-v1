package com.creants.creants_2x.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.creants.creants_2x.core.exception.QAntErrorCode;
import com.creants.creants_2x.core.exception.QAntErrorData;
import com.creants.creants_2x.core.exception.QAntJoinRoomException;
import com.creants.creants_2x.core.extension.IQAntExtension;
import com.creants.creants_2x.core.util.IPlayerIdGenerator;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.creants_2x.socket.managers.IUserManager;
import com.creants.creants_2x.socket.managers.UserManager;

import io.netty.channel.Channel;

/**
 * @author LamHM
 *
 */
public class QAntRoom implements Room {
	private static AtomicInteger autoID;
	private int id;
	private String groupId;
	private String name;
	private String password;
	private boolean passwordProtected;
	private int maxUsers;
	private int maxSpectators;
	private QAntUser owner;
	private IUserManager userManager;
	private boolean game;
	private boolean hidden;
	private volatile boolean active;
	private volatile IQAntExtension extension;
	private int maxRoomVariablesAllowed;
	private Zone zone;

	static {
		QAntRoom.autoID = new AtomicInteger(0);
	}

	private static int getNewID() {
		return QAntRoom.autoID.getAndIncrement();
	}

	public QAntRoom(String name) {
		this(name, null);
	}

	public QAntRoom(String name, Class<?> customPlayerIdGeneratorClass) {
		this.id = getNewID();
		this.name = name;
		this.active = false;
		this.userManager = new UserManager();
	}

	@Override
	public String getGroupId() {
		if (groupId != null && groupId.length() > 0) {
			return groupId;
		}
		return "default";
	}

	@Override
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public void setZone(Zone zone) {
		this.zone = zone;
		instantiateRoomIdGenerator();
	}

	private void instantiateRoomIdGenerator() {
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
		passwordProtected = (password != null && password.length() > 0);
	}

	@Override
	public boolean isPasswordProtected() {
		return passwordProtected;
	}

	@Override
	public boolean isPublic() {
		return !passwordProtected;
	}

	@Override
	public int getMaxUsers() {
		return maxUsers;
	}

	@Override
	public void setMaxUsers(int maxUsers) {
		this.maxUsers = maxUsers;
		// TODO nếu là game thì max sẽ là số lượng player
		// if (isGame() && this.playerIdGenerator != null) {
		// this.playerIdGenerator.onRoomResize();
		// }
	}

	@Override
	public int getMaxSpectators() {
		return maxSpectators;
	}

	@Override
	public void setMaxSpectators(int maxSpectators) {
		this.maxSpectators = maxSpectators;
	}

	@Override
	public QAntUser getOwner() {
		return owner;
	}

	@Override
	public void setOwner(QAntUser owner) {
		this.owner = owner;
	}

	@Override
	public IUserManager getUserManager() {
		return userManager;
	}

	@Override
	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	@Override
	public boolean isGame() {
		return game;
	}

	public void setGame(final boolean game, final Class<? extends IPlayerIdGenerator> customPlayerIdGeneratorClass) {
		this.game = game;
		if (game) {
			// try {
			// // TODO nếu là game thì sẽ tạo instance để sinh ra Id cho player
			// // (this.playerIdGenerator = (IPlayerIdGenerator)
			// // customPlayerIdGeneratorClass.newInstance())
			// // .setParentRoom(this);
			// // this.playerIdGenerator.init();
			// } catch (InstantiationException err) {
			// QAntTracer.warn(this.getClass(),
			// String.format(
			// "Cannot instantiate Player ID Generator: %s, Reason: %s -- Room
			// might not function correctly.",
			// customPlayerIdGeneratorClass, err));
			// } catch (IllegalAccessException err2) {
			// QAntTracer.warn(this.getClass(),
			// String.format(
			// "Illegal Access to Player ID Generator Class: %s, Reason: %s --
			// Room might not function correctly.",
			// customPlayerIdGeneratorClass, err2));
			// }
		}
	}

	@Override
	public void setGame(boolean game) {
		this.setGame(game, null);
	}

	@Override
	public boolean isHidden() {
		return this.hidden;
	}

	@Override
	public void setHidden(final boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public void setActive(final boolean flag) {
		this.active = flag;
	}

	@Override
	public List<QAntUser> getPlayersList() {
		final List<QAntUser> playerList = new ArrayList<QAntUser>();
		for (QAntUser user : userManager.getAllUsers()) {
			if (user.isPlayer(this)) {
				playerList.add(user);
			}
		}
		return playerList;
	}

	@Override
	public RoomSize getSize() {
		int uCount = 0;
		int sCount = 0;
		if (game) {
			for (QAntUser user : userManager.getAllUsers()) {
				if (user.isSpectator(this)) {
					++sCount;
				} else {
					++uCount;
				}
			}
		} else {
			uCount = userManager.getUserCount();
		}
		return new RoomSize(uCount, sCount);
	}

	@Override
	public List<QAntUser> getSpectatorsList() {
		final List<QAntUser> specList = new ArrayList<QAntUser>();
		for (QAntUser user : userManager.getAllUsers()) {
			if (user.isSpectator(this)) {
				specList.add(user);
			}
		}

		return specList;
	}

	@Override
	public QAntUser getUserById(int id) {
		return userManager.getUserById(id);
	}

	@Override
	public QAntUser getUserByName(String name) {
		return userManager.getUserByName(name);
	}

	@Override
	public QAntUser getUserByChannel(Channel channel) {
		return userManager.getUserByChannel(channel);
	}

	@Override
	public QAntUser getUserByPlayerId(int playerId) {
		QAntUser user = null;
		for (QAntUser u : userManager.getAllUsers()) {
			if (u.getPlayerId(this) == playerId) {
				user = u;
				break;
			}
		}

		return user;
	}

	@Override
	public List<QAntUser> getUserList() {
		return userManager.getAllUsers();
	}

	@Override
	public List<Channel> getChannelList() {
		return this.userManager.getAllChannels();
	}

	@Override
	public int getCapacity() {
		return maxUsers + maxSpectators;
	}

	@Override
	public void setCapacity(int maxUser, int maxSpectators) {
		this.maxUsers = maxUser;
		this.maxSpectators = maxSpectators;
	}

	@Override
	public void destroy() {
	}

	@Override
	public boolean containsUser(String name) {
		return userManager.containsName(name);
	}

	@Override
	public boolean containsUser(QAntUser user) {
		return userManager.containsUser(user);
	}

	@Override
	public void addUser(QAntUser user) throws QAntJoinRoomException {
		addUser(user, false);
	}

	@Override
	public void addUser(QAntUser user, boolean asSpectator) throws QAntJoinRoomException {
		if (userManager.containsId(user.getUserId())) {
			String message = String.format("User already joined: %s, Room: %s, Zone: ?", user, this);
			QAntErrorData data = new QAntErrorData(QAntErrorCode.JOIN_ALREADY_JOINED);
			data.addParameter(name);
			throw new QAntJoinRoomException(message, data);
		}

		boolean okToAdd = false;
		synchronized (this) {
			RoomSize roomSize = getSize();
			if (isGame() && asSpectator) {
				okToAdd = (roomSize.getSpectatorCount() < maxSpectators);
			} else {
				okToAdd = (roomSize.getUserCount() < maxUsers);
			}
			if (!okToAdd) {
				String message2 = String.format("Room is full: %s, Zone: ? - Can't add User: %s ", name, user);
				QAntErrorData data2 = new QAntErrorData(QAntErrorCode.JOIN_ROOM_FULL);
				data2.addParameter(name);
				throw new QAntJoinRoomException(message2, data2);
			}

			userManager.addUser(user);
		}

		user.addJoinedRoom(this);
		if (isGame()) {
			if (asSpectator) {
				user.setPlayerId(-1, this);
			} else {
				// user.setPlayerId(this.playerIdGenerator.getPlayerSlot(),
				// this);
			}
		} else {
			user.setPlayerId(0, this);
		}
	}

	@Override
	public void removeUser(QAntUser user) {
		if (isGame()) {
			// this.playerIdGenerator.freePlayerSlot(user.getPlayerId(this));
		}
		userManager.removeUser(user);
		user.removeJoinedRoom(this);
	}

	@Override
	public boolean isEmpty() {
		return userManager.getUserCount() == 0;
	}

	@Override
	public boolean isFull() {
		if (isGame()) {
			return getSize().getUserCount() == maxUsers;
		}
		return userManager.getUserCount() == maxUsers;
	}

	@Override
	public String toString() {
		return String.format("[ Room: %s, Id: %s, Group: %s, isGame: %s ]", this.name, this.id, this.groupId,
				this.game);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Room)) {
			return false;
		}
		Room room = (Room) obj;
		boolean isEqual = false;
		if (room.getId() == id) {
			isEqual = true;
		}
		return isEqual;
	}

	@Override
	public IQAntExtension getExtension() {
		return this.extension;
	}

	@Override
	public void setExtension(IQAntExtension extension) {
		this.extension = extension;
	}

	@Override
	public String getDump() {
		final StringBuilder sb = new StringBuilder("/////////////// Room Dump ////////////////").append("\n");
		sb.append("\tName: ").append(this.name).append("\n").append("\tId: ").append(this.id).append("\n")
				.append("\tGroupId: ").append(this.groupId).append("\n").append("\tPassword: ").append(this.password)
				.append("\n").append("\tOwner: ").append((this.owner == null) ? "[[ SERVER ]]" : this.owner.toString())
				.append("\n").append("\tisGame: ").append(this.game).append("\n").append("\tisHidden: ")
				.append(this.hidden).append("\n").append("\tsize: ").append(this.getSize()).append("\n")
				.append("\tMaxUser: ").append(this.maxUsers).append("\n").append("\tMaxSpect: ")
				.append(this.maxSpectators).append("\n").append("\tPlayerIdGen: ").append("\tSettings: ").append("\n");
		if (this.extension != null) {
			// sb.append("\tExtension: ").append("\n");
			// sb.append("\t\t").append("Name:
			// ").append(extension.getName()).append("\n");
			// sb.append("\t\t").append("Class:
			// ").append(extension.getExtensionFileName()).append("\n");
			// sb.append("\t\t").append("Type:
			// ").append(extension.getType()).append("\n");
			// sb.append("\t\t").append("Props:
			// ").append(extension.getPropertiesFileName()).append("\n");
		}
		sb.append("/////////////// End Dump /////////////////").append("\n");
		return sb.toString();
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getMaxRoomVariablesAllowed() {
		return maxRoomVariablesAllowed;
	}

	@Override
	public void setMaxRoomVariablesAllowed(int max) {
		this.maxRoomVariablesAllowed = max;
	}

	@Override
	public Zone getZone() {
		// TODO Auto-generated method stub
		return null;
	}

}
