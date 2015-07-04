package me.shadorc.twitterstalker.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.shadorc.twitterstalker.graphics.Storage;

public class StatInfo {

	private HashMap <String, WordInfo> map;
	private String desc;
	private double num;

	private DecimalFormat df = new DecimalFormat("#.#");

	StatInfo() {
		this.map = new HashMap <String, WordInfo> ();
	}

	StatInfo(double num, String desc) {
		this.num = num;
		this.desc = df.format(num) + " " + Storage.tra(desc);
	}

	StatInfo(double num, String desc, TwitterUser user) {
		this.num = num;
		this.desc = (df.format(100*num/user.getTweetsAnalyzed()) + "% " + desc + " (" + df.format(num) + ")");
	}

	public void increment() {
		num++;
	}

	public void increment(long num) {
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
		WordInfo wi = map.get(word);

		//If the word isn't in the map, create and add
		if(wi == null) {
			wi = new WordInfo(word);
			map.put(word, wi);
		}

		wi.increment();
	}

	public void add(String name, Date date) {
		if(map.containsKey(name) && new Date((long) this.getNum()).after(date)) {
			map.get(name).setNum(date.getTime());
		} else if(!map.containsKey(name)) {
			this.add(name);
			map.get(name).setNum(date.getTime());
		}
	}

	public void add(WordInfo status) {
		map.put(Long.toString(status.getId()), status);
	}

	public List <WordInfo> sort() {

		List <WordInfo> list = new ArrayList <WordInfo> (map.values());

		//Comparator who compares each words value
		Comparator <WordInfo> comparator = new Comparator <WordInfo>() {
			@Override
			public int compare(WordInfo w1, WordInfo w2) {
				return Long.compare(w1.getCount(), w2.getCount());
			}
		};

		//Sort the words in ascending order
		Collections.sort(list, comparator);

		//Arranges them in descending order
		Collections.reverse(list);

		return list;
	}
}