package com.creants.creants_2x.core.entities.match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.creants.creants_2x.core.entities.Room;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public class MatchingUtils {
	private static MatchingUtils _instance;
	private ExpressionMatcher roomMatcher;
	private ExpressionMatcher userMatcher;

	static {
		_instance = new MatchingUtils();
	}

	private MatchingUtils() {
		// (this.roomMatcher = new
		// VariablesMatcher()).setProxyVariableResolver(new
		// RoomProxyVariableResolver());
		// (this.userMatcher = new
		// VariablesMatcher()).setProxyVariableResolver(new
		// UserProxyVariableMatcher());
	}

	public static MatchingUtils getInstance() {
		return MatchingUtils._instance;
	}

	public boolean matchUser(QAntUser user, MatchExpression conditions) {
		return false;
	}

	public boolean matchRoom(Room room, MatchExpression conditions) {
		return false;
	}

	public List<QAntUser> matchUsers(Collection<QAntUser> userList, MatchExpression conditions) {
		// return this.matchUsers(userList, conditions, Integer.MAX_VALUE);
		return null;
	}

	public List<QAntUser> matchUsers(Collection<QAntUser> userList, MatchExpression conditions, int limit) {
		if (limit <= 0) {
			limit = Integer.MAX_VALUE;
		}

		List<QAntUser> filteredUsers = new ArrayList<QAntUser>();
		for (QAntUser user : userList) {
			if (matchUser(user, conditions)) {
				if (filteredUsers.size() >= limit) {
					break;
				}
				filteredUsers.add(user);
			}
		}
		return filteredUsers;
	}

	public List<Room> matchRooms(final Collection<Room> roomList, final MatchExpression conditions) {
		return matchRooms(roomList, conditions, Integer.MAX_VALUE);
	}

	public List<Room> matchRooms(final Collection<Room> roomList, final MatchExpression conditions, int limit) {
		if (limit <= 0) {
			limit = Integer.MAX_VALUE;
		}
		final List<Room> filteredRooms = new ArrayList<Room>();
		for (final Room room : roomList) {
			if (this.matchRoom(room, conditions)) {
				if (filteredRooms.size() >= limit) {
					break;
				}
				filteredRooms.add(room);
			}
		}
		return filteredRooms;
	}
}
