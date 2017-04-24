package com.creants.creants_2x.socket.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

import io.netty.channel.Channel;

/**
 * @author LamHM
 *
 */
public class UserManager implements IUserManager {
	private String name;
	private final ConcurrentMap<String, QAntUser> usersByName;
	private final ConcurrentMap<Channel, QAntUser> usersByChannel;
	private final ConcurrentMap<Integer, QAntUser> usersById;
	private int highestCCU;


	public UserManager() {
		name = "UserManagerService";
		highestCCU = 0;
		usersByChannel = new ConcurrentHashMap<Channel, QAntUser>();
		usersByName = new ConcurrentHashMap<String, QAntUser>();
		usersById = new ConcurrentHashMap<Integer, QAntUser>();
	}


	public String getName() {
		return name;
	}


	@Override
	public void addUser(QAntUser user) {
		if (containsId(user.getUserId())) {
			QAntTracer.warn(this.getClass(), "Can't add User: " + user.getName() + " - Already exists in Room: ");
			return;
		}

		usersById.put(user.getUserId(), user);
		usersByName.put(user.getName(), user);
		usersByChannel.put(user.getChannel(), user);
		if (usersById.size() > highestCCU) {
			highestCCU = usersById.size();
		}
	}


	@Override
	public QAntUser getUserById(int id) {
		return usersById.get(id);
	}


	@Override
	public QAntUser getUserByName(String name) {
		return usersByName.get(name);
	}


	@Override
	public QAntUser getUserByChannel(Channel channel) {
		return usersByChannel.get(channel);
	}


	@Override
	public void removeUser(int userId) {
		final QAntUser user = usersById.get(userId);
		if (user == null) {
			QAntTracer.warn(this.getClass(), "Can't remove user with ID: " + userId + ". User was not found.");
		} else {
			this.removeUser(user);
		}
	}


	@Override
	public void removeUser(String name) {
		QAntUser user = usersByName.get(name);
		if (user == null) {
			QAntTracer.warn(this.getClass(), "Can't remove user with name: " + name + ". User was not found.");
		} else {
			removeUser(user);
		}
	}


	@Override
	public void removeUser(Channel session) {
		QAntUser user = usersByChannel.get(session);
		if (user == null) {
			QAntTracer.warn(this.getClass(), "Can't remove user with session: " + session + ". User was not found.");
			return;
		}

		removeUser(user);
	}


	@Override
	public void removeUser(QAntUser user) {
		usersById.remove(user.getUserId());
		usersByName.remove(user.getName());
		usersByChannel.remove(user.getChannel());
	}


	@Override
	public boolean containsId(int userId) {
		return usersById.containsKey(userId);
	}


	@Override
	public boolean containsName(String name) {
		return usersByName.containsKey(name);
	}


	@Override
	public boolean containsChannel(Channel session) {
		return usersByChannel.containsKey(session);
	}


	@Override
	public boolean containsUser(QAntUser user) {
		return usersById.containsValue(user);
	}


	@Override
	public List<QAntUser> getAllUsers() {
		return new ArrayList<QAntUser>(usersById.values());
	}


	@Override
	public List<Channel> getAllChannels() {
		return new ArrayList<Channel>((Collection<? extends Channel>) usersByChannel.keySet());
	}


	@Override
	public Collection<QAntUser> getDirectUserList() {
		return Collections.unmodifiableCollection(usersById.values());
	}


	@Override
	public Collection<Channel> getDirectChannelList() {
		return Collections.unmodifiableCollection((Collection<? extends Channel>) usersByChannel.keySet());
	}


	@Override
	public int getUserCount() {
		return usersById.values().size();
	}


	@Override
	public int getNPCCount() {
		int npcCount = 0;
		for (QAntUser user : usersById.values()) {
			if (user.isNPC()) {
				++npcCount;
			}
		}
		return npcCount;
	}


	@Override
	public void disconnectUser(int userId) {
		QAntUser user = usersById.get(userId);
		if (user == null) {
			QAntTracer.warn(this.getClass(), "Can't disconnect user with id: " + userId + ". User was not found.");
		} else {
			disconnectUser(user);
		}
	}


	@Override
	public void disconnectUser(Channel channel) {
		QAntUser user = usersByChannel.get(channel);
		if (user == null) {
			QAntTracer.warn(this.getClass(),
					"Can't disconnect user with session: " + channel + ". User was not found.");
		} else {
			disconnectUser(user);
		}
	}


	@Override
	public void disconnectUser(String name) {
		QAntUser user = usersByName.get(name);
		if (user == null) {
			QAntTracer.warn(this.getClass(), "Can't disconnect user with name: " + name + ". User was not found.");
		} else {
			disconnectUser(user);
		}
	}


	@Override
	public void disconnectUser(QAntUser user) {
		this.removeUser(user);
	}


	@Override
	public int getHighestCCU() {
		return this.highestCCU;
	}


	@Override
	public List<Channel> channelsFromNames(List<String> names) {
		List<Channel> sessions = new LinkedList<Channel>();
		for (String name : names) {
			QAntUser user = getUserByName(name);
			if (user != null) {
				sessions.add(user.getChannel());
			}
		}

		return sessions;
	}


	/**
	 * Loại bỏ các user rác trên hệ thống
	 */
	public void purgeOrphanedUsers() {
	}

}
