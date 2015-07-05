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
	private int total;

	StatInfo() {
		this.map = new HashMap <String, WordInfo> ();
		this.wi = new WordInfo("", this);
	}

	StatInfo(String desc, long num, double total) {
		this.wi = new WordInfo(desc, num, total);
	}

	public WordInfo getWordInfo() {
		return wi;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total; 
	}

	public void add(String word) {
		//Get the value of the word in the map
		WordInfo wi = map.get(word);

		//If the word isn't in the map, create and add
		if(wi == null) {
			wi = new WordInfo(word, this);
			map.put(word, wi);
		}

		wi.increment();
	}

	public void add(WordInfo wi) {
		map.put(Long.toString(wi.getStatus().getId()), wi);
	}

	//Used by FIRST_TALK
	public void add(String name, Date date) {
		if(!map.containsKey(name)) {
			this.add(name);
			map.get(name).setNum(date.getTime());
		} else if(map.containsKey(name) && new Date(wi.getNum()).after(date)) {
			map.get(name).setNum(date.getTime());
		}
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