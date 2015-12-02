package me.shadorc.twitterstalker.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import me.shadorc.twitterstalker.storage.Data.UsersEnum;
import twitter4j.Status;

public class UsersMap {

	private HashMap <String, UserStats> map;
	private int total;

	public UsersMap() {
		map = new HashMap<>();
	}

	public void add(String name) {
		if(!map.containsKey(name)) {
			map.put(name, new UserStats(name, this));
		}
		map.get(name).increment();
	}

	//Used by FIRST_TALK
	public void add(String name, Status status) {
		if(!map.containsKey(name) || status.getCreatedAt().getTime() < map.get(name).getNum()) {
			map.put(name, new UserStats(name, status));
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
