package me.shadorc.twitterstalker.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import me.shadorc.twitterstalker.storage.Data.UsersEnum;
import twitter4j.Status;

public class UsersMap {

	private HashMap <Long, UserStats> map;
	private int total;

	public UsersMap() {
		map = new HashMap<>();
	}

	public void add(long id) {
		if(!map.containsKey(id)) {
			map.put(id, new UserStats(id, this));
		}
		map.get(id).increment();
	}

	//Used by FIRST_TALK
	public void add(long id, Status status) {
		if(!map.containsKey(id) || status.getCreatedAt().getTime() < map.get(id).getNum()) {
			map.put(id, new UserStats(id, status));
		}
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public float getTotal() {
		return total;
	}

	public List<UserStats> sort() {
		List <UserStats> list = new ArrayList <UserStats> (map.values());

		//Comparator who compares each words value
		Comparator <UserStats> comparator = new Comparator <UserStats>() {
			@Override
			public int compare(UserStats w1, UserStats w2) {
				return Double.compare(w1.getNum(), w2.getNum());
			}
		};

		//Sort the words in ascending order
		Collections.sort(list, comparator);

		//Arranges them in descending order
		Collections.reverse(list);

		return list;
	}

	public static HashMap<UsersEnum, UsersMap> init() {
		HashMap <UsersEnum, UsersMap> userStatsMap = new HashMap<>();
		for(UsersEnum stat : UsersEnum.values()) {
			userStatsMap.put(stat, new UsersMap());
		}
		return userStatsMap;
	}
}
