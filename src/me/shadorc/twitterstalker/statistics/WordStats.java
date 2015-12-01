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
			return this.getNum() + " <b>" + this.getObject() + "</b> (" + Ressources.format(this.getNum()/wordStatsMap.getTotal()*100.0) + "%)"; 
		} 
		//Popular
		else {
			Status status = (Status) obj;
			String url = Ressources.getStatusURL(status);
			String infos = "<b>" + status.getRetweetCount() + "</b>" +  Storage.tra("rt") + "<b>" + status.getFavoriteCount() + "</b>" + Storage.tra("fav");
			return "<a href=" + url + ">" + infos + "</a>";
		}
	}
}
