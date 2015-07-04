package me.shadorc.twitterstalker.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StatInfo {

	private HashMap <String, WordInfo> map;
	private WordInfo wi;

	StatInfo() {
		this.map = new HashMap <String, WordInfo> ();
		this.wi = new WordInfo("");
	}

	StatInfo(String desc, double num, double total) {
		this.wi = new WordInfo(desc, num, total);
	}

	public void setTotal(double total) {
		this.wi.setTotal(total);
	}

	public WordInfo getWordInfo() {
		return wi;
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

	//Used by FIRST_TALK
	public void add(String name, Date date) {
		if(map.containsKey(name) && new Date((long) wi.getNum()).before(date)) {
			map.get(name).setNum(date.getTime());
		} else if(!map.containsKey(name)) {
			this.add(name);
			map.get(name).setNum(date.getTime());
		}
	}

	public void add(WordInfo wi) {
		map.put(Long.toString(wi.getStatus().getId()), wi);
	}

	public List <WordInfo> sort() {

		List <WordInfo> list = new ArrayList <WordInfo> (map.values());

		//Comparator who compares each words value
		Comparator <WordInfo> comparator = new Comparator <WordInfo>() {
			@Override
			public int compare(WordInfo w1, WordInfo w2) {
				return Double.compare(w1.getNum(), w2.getNum());
			}
		};

		//Sort the words in ascending order
		Collections.sort(list, comparator);

		//Arranges them in descending order
		Collections.reverse(list);

		return list;
	}
}