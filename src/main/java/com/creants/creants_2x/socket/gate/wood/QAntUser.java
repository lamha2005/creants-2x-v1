package com.creants.creants_2x.socket.gate.wood;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.socket.gate.IQAntUser;
import com.creants.creants_2x.socket.gate.entities.IQAntArray;
import com.creants.creants_2x.socket.gate.entities.QAntArray;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @author LamHa
 *
 */
public class QAntUser implements IQAntUser {
	private static final AttributeKey<Long> LAST_REQUEST_TIME = AttributeKey.valueOf("last.request.time");
	private long sessionId;
	private int userId;
	private long creantsUserId;
	private long createTime;
	private String name;
	private String fullName;
	private long money;
	private String avatar;
	private byte language;
	private boolean isJoiningARoom = false;
	private long loginMoney;
	private long loginTime;
	private byte currentGameId = -1;
	private Channel channel;
	private final LinkedList<Room> joinedRooms;
	private final Set<Room> createdRooms;
	private boolean isConnected;
	private final ConcurrentMap<Object, Object> properties;
	private Zone currentZone;


	public QAntUser(Channel channel) {
		this("", channel);
	}


	public QAntUser(String name, Channel channel) {
		userId = QAntServer.getInstance().getUIDGenerator().generateID();
		joinedRooms = new LinkedList<Room>();
		createdRooms = new HashSet<Room>();
		this.channel = channel;
		this.name = name;
		this.properties = new ConcurrentHashMap<Object, Object>();
	}


	public Room getLastJoinedRoom() {
		Room lastRoom = null;
		synchronized (joinedRooms) {
			if (joinedRooms.size() > 0) {
				lastRoom = joinedRooms.getLast();
			}
		}

		return lastRoom;
	}


	public List<Room> getJoinedRooms() {
		return joinedRooms;
	}


	@Override
	public Zone getZone() {
		return this.currentZone;
	}


	@Override
	public void setZone(Zone currentZone) {
		if (this.currentZone != null) {
			throw new IllegalStateException("The User Zone is already set. It cannot be modified at Runtime. " + this);
		}
		this.currentZone = currentZone;
	}


	public Map<Room, Integer> getPlayerIds() {
		return null;
	}


	public List<Room> getCreatedRooms() {
		List<Room> rooms = null;
		synchronized (this.createdRooms) {
			rooms = new LinkedList<Room>(this.createdRooms);
		}
		return rooms;
	}


	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}


	public boolean isConnected() {
		return isConnected;
	}


	public boolean isJoinedInRoom(Room room) {
		return true;
	}


	public IQAntArray toQAntArray() {
		return this.toQAntArray(getLastJoinedRoom());
	}


	public IQAntArray toQAntArray(Room room) {
		IQAntArray userObj = QAntArray.newInstance();
		userObj.addInt(userId);
		userObj.addUtfString(name);
		userObj.addShort((short) getPlayerId(room));
		// userObj.addQAntArray(this.getUserVariablesData());
		return userObj;
	}


	public void removeCreatedRoom(Room room) {

	}


	public long getCreantsUserId() {
		return creantsUserId;
	}


	public void setCreantsUserId(long creantsUserId) {
		this.creantsUserId = creantsUserId;
	}


	public String getFullName() {
		return fullName;
	}


	public void setFullName(String fullName) {
		this.fullName = fullName;
	}


	@Override
	public Object getProperty(final Object key) {
		return this.properties.get(key);
	}


	@Override
	public void setProperty(final Object key, final Object val) {
		this.properties.put(key, val);
	}


	@Override
	public boolean containsProperty(final Object key) {
		return this.properties.containsKey(key);
	}


	@Override
	public void removeProperty(final Object key) {
		this.properties.remove(key);
	}


	public void addCreatedRoom(Room room) {

	}


	public boolean isNPC() {
		return false;
	}


	public void removeJoinedRoom(Room room) {

	}


	public void addJoinedRoom(Room room) {

	}


	public void setPlayerId(int playerId, Room room) {

	}


	public boolean isPlayer(Room room) {
		return true;
	}


	public boolean isSpectator(Room room) {
		return false;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public long getCreatedTime() {
		return createTime;
	}


	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}


	public Channel getChannel() {
		return channel;
	}


	public void setChannel(Channel channel) {
		this.channel = channel;
	}


	@Override
	public byte getDeviceType() {
		return 0;
	}


	@Override
	public long getSessionId() {
		return sessionId;
	}


	public String getClientIp() {
		return null;
	}


	public void setClientIp(String clientIp) {
	}


	@Override
	public String getLocale() {
		return null;
	}


	@Override
	public String getPlatformInformation() {
		return null;
	}


	@Override
	public byte getProtocolVersion() {
		return 0;
	}


	@Override
	public String getScreenSize() {
		return null;
	}


	@Override
	public int getUserId() {
		return userId;
	}


	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}


	public int getPlayerId(Room room) {
		return -1;
	}


	@Override
	public String getVersion() {
		return null;
	}


	@Override
	public void initialize(String version, long sessionId, long clientId, byte deviceType, long createTime) {

	}


	@Override
	public void setVersion(String version) {

	}


	public long getMoney() {
		return money;
	}


	public void setMoney(long money) {
		this.money = money;
	}


	public String getAvatar() {
		return avatar;
	}


	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public byte getLanguage() {
		return language;
	}


	public void setLanguage(byte language) {
		this.language = language;
	}


	public synchronized boolean isJoining() {
		return isJoiningARoom;
	}


	public synchronized void setJoining(boolean flag) {
		isJoiningARoom = flag;
	}


	public long getLastRequestTime() {
		return channel.attr(LAST_REQUEST_TIME).get();
	}


	public synchronized void updateLastRequestTime() {
		setLastRequestTime(System.currentTimeMillis());
	}


	public void setLastRequestTime(long lastRequestTime) {
		channel.attr(LAST_REQUEST_TIME).set(lastRequestTime);
	}


	public void setLoginInfo(QAntUser user) {
		userId = user.getUserId();
		name = user.getName();
		avatar = user.getAvatar();
		language = user.getLanguage();
		money = user.getMoney();
		loginTime = System.currentTimeMillis();
	}


	public long getLoginMoney() {
		return loginMoney;
	}


	public void setLoginMoney(long loginMoney) {
		this.loginMoney = loginMoney;
	}


	public long getLoginTime() {
		return loginTime;
	}


	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}


	public long getTimeOnline() {
		return (System.currentTimeMillis() - getLoginTime()) / 1000;
	}


	@Override
	public byte getCurrentGameId() {
		return currentGameId;
	}


	@Override
	public void setCurrentGameId(byte currentGameId) {
		this.currentGameId = currentGameId;
	}


	@Override
	public String toString() {
		return String.format("[sessionId: %d, userId: %s, name: %s, currentGame: %s, money: %d]", sessionId, userId,
				name, currentGameId, money);
	}

}
