package me.shadorc.twitterstalker.statistics;

import me.shadorc.twitterstalker.storage.Storage;
import me.shadorc.twitterstalker.utility.Ressources;
import twitter4j.Status;

public class WordStats {

	private WordsMap wordStatsMap;
	private Object obj;
	private int num;

	public WordStats(String word, WordsMap wordStatsMap) {
		this.obj = word;
		this.wordStatsMap = wordStatsMap;
		this.num = 0;
	}

	//Used by POPULARE
	public WordStats(Status status) {
		this.obj = status;
		this.num = status.getRetweetCount() + status.getFavoriteCount();
	}

	public Object getObject() {
		return obj;
	}

	public int getNum() {
		return num;
	}

	public void increment() {
		this.num++;
	}

	@Override
	public String toString() {
		if(wordStatsMap != null) {
			return this.getNum() + " " + this.getObject() + " (" + Ressources.format(this.getNum()/wordStatsMap.getTotal()*100.0) + "%)"; 
		} else {
			String url = Ressources.getStatusURL((Status) obj);
			String infos = ((Status) obj).getRetweetCount() + Storage.tra("rt") + ((Status) obj).getFavoriteCount() + Storage.tra("fav");
			return "<a href=" + url + ">" + infos + "</a>";
		}
	}
}
