package me.shadorc.twitterstalker.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class StatInfo {

	private HashMap <String, WordInfo> map;
	private String desc;
	private double num;

	StatInfo() {
		this.map = new HashMap <>();
	}

	StatInfo(double num, String desc, boolean isPercen) {
		this.num = num;
		this.desc = isPercen ? desc : new DecimalFormat("#.#").format(num) + " " + desc;
	}

	public void increment() {
		num++;
	}

	public void increment(int num) {
		this.num += num;
	}

	public String getDesc() {
		return desc;
	}

	public double getNum() {
		return num;
	}

	public void add(String word) {
		//Get the value of the word in the map
		WordInfo wih = map.get(word);

		//If the word isn't in the map, create and add
		if(wih == null) {
			wih = new WordInfo(word);
			map.put(word, wih);
		}

		wih.increment();
	}

	public void add(WordInfo status) {
		map.put(Long.toString(status.getId()), status);
	}

	public List <WordInfo> sort() {

		List <WordInfo> list = new ArrayList <>(map.values());

		//Comparator who compares each words value
		Comparator <WordInfo> comparator = new Comparator <WordInfo>() {
			@Override
			public int compare(WordInfo w1, WordInfo w2) {
				return Integer.compare(w1.getCount(), w2.getCount());
			}
		};

		//Sort the words in ascending order
		Collections.sort(list, comparator);

		//Arranges them in descending order
		Collections.reverse(list);

		return list;
	}
}