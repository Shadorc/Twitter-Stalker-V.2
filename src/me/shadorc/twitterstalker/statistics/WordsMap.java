package me.shadorc.twitterstalker.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import me.shadorc.twitterstalker.storage.Data.WordsEnum;
import twitter4j.Status;

public class WordsMap {

	private HashMap <Object, WordStats> map;
	private int total;

	public WordsMap() {
		map = new HashMap<>();
	}

	public void add(String word) {
		if(!map.containsKey(word)) {
			map.put(word, new WordStats(word, this));
		}
		map.get(word).increment();
	}

	public void add(Status status) {
		map.put(status, new WordStats(status));
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public float getTotal() {
		return total;
	}

	public List<WordStats> sort() {
		List <WordStats> list = new ArrayList <WordStats> (map.values());

		//Comparator who compares each words value
		Comparator <WordStats> comparator = new Comparator <WordStats>() {
			@Override
			public int compare(WordStats w1, WordStats w2) {
				return Double.compare(w1.getNum(), w2.getNum());
			}
		};

		//Sort the words in ascending order
		Collections.sort(list, comparator);

		//Arranges them in descending order
		Collections.reverse(list);

		return list;
	}

	public static HashMap<WordsEnum, WordsMap> init() {
		HashMap <WordsEnum, WordsMap> wordStatsMap = new HashMap<>();
		for(WordsEnum stat : WordsEnum.values()) {
			wordStatsMap.put(stat, new WordsMap());
		}
		return wordStatsMap;
	}
}