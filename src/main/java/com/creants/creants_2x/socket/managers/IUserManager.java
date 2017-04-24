package com.creants.creants_2x.socket.managers;

import java.util.Collection;
import java.util.List;

import com.creants.creants_2x.socket.gate.wood.QAntUser;

import io.netty.channel.Channel;

/**
 * @author LamHM
 *
 */
public interface IUserManager {
	QAntUser getUserByName(String name);


	QAntUser getUserById(int qAntUserId);


	QAntUser getUserByChannel(Channel channel);


	List<QAntUser> getAllUsers();


	Collection<QAntUser> getDirectUserList();


	List<Channel> getAllChannels();


	Collection<Channel> getDirectChannelList();


	void addUser(QAntUser user);


	void removeUser(QAntUser user);


	void removeUser(String name);


	void removeUser(int userId);


	void removeUser(Channel channel);


	void disconnectUser(QAntUser user);


	void disconnectUser(String name);


	void disconnectUser(int userId);


	void disconnectUser(Channel channel);


	boolean containsId(int userId);


	boolean containsName(String name);


	boolean containsChannel(Channel channel);


	boolean containsUser(QAntUser user);


	int getUserCount();


	int getNPCCount();


	int getHighestCCU();


	List<Channel> channelsFromNames(List<String> names);
}
