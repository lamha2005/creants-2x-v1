package com.creants.creants_2x.core.util;

import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

import io.netty.util.AttributeKey;

/**
 * @author LamHa
 *
 */
public class UsersUtil {
	private static final AttributeKey<Long> LAST_SEARCH_TIME = AttributeKey.valueOf("LastSearchTime");
	private static QAntUser fakeAdminUser;
	private static QAntUser fakeModUser;
	private static volatile boolean isInited;

	static {
		UsersUtil.isInited = false;
	}

	public static boolean usersSeeEachOthers(QAntUser sender, QAntUser recipient) {
		boolean seeEachOthers = false;
		for (Room room : recipient.getJoinedRooms()) {
			if (room.containsUser(sender)) {
				seeEachOthers = true;
				break;
			}
		}

		return seeEachOthers;
	}

	public static QAntUser getServerAdmin() {
		if (!UsersUtil.isInited) {
			initialize();
		}
		return UsersUtil.fakeAdminUser;
	}

	public static QAntUser getServerModerator() {
		if (!UsersUtil.isInited) {
			initialize();
		}

		return UsersUtil.fakeModUser;
	}

	public static boolean isAllowedToPerformNewSearch(QAntUser user) {
		boolean ok = false;
		Long lastSearchTime = user.getChannel().attr(LAST_SEARCH_TIME).get();
		if (lastSearchTime == null) {
			ok = true;
		} else if (System.currentTimeMillis() - lastSearchTime > 1000L) {
			ok = true;
		}
		if (ok) {
			user.getChannel().attr(LAST_SEARCH_TIME).set(System.currentTimeMillis());
		}

		return ok;
	}

	private static synchronized void initialize() {
	}
}
