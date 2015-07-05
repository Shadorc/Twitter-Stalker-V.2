package me.shadorc.twitterstalker.statistics;

import java.text.DecimalFormat;

import me.shadorc.twitterstalker.graphics.Storage;
import twitter4j.Status;
import twitter4j.TwitterException;

public class WordInfo {

	private static DecimalFormat df = new DecimalFormat("#.#");

	private Status status;
	private String word;

	private long num;
	private double total; 
	private StatInfo si;

	WordInfo(String word, long num, double total) {
		this.word = word;
		this.num = num;
		this.total = total;
	}

	WordInfo(String word, StatInfo si) {
		this.word = word;
		this.si = si;
		this.num = 0;
		this.total = 1;
	}

	WordInfo(Status status) {
		this.status = status;
		this.num = status.getRetweetCount() + status.getFavoriteCount();
	}

	public void increment() {
		this.num++;
	}

	public void increment(int num) {
		this.num += num;
	}

	//Used by FIRST_TALK to set the first talking date
	public void setNum(long num) {
		this.num = num;
	}

	public String getInfo() {
		return df.format(num/total) + " " + word;
	}

	public String getPercenInfo() {
		if(si != null) total = si.getTotal();
		return  word + " (" + df.format(100*num/total) + "%)";
	}

	public String getUserInfo() throws TwitterException {
		return "<img src=" + new TwitterUser(word).getImageUrl() + " border=1 align=middle> " + " @" + this.getPercenInfo();
	}

	public String getStatusInfo() {
		return status.getRetweetCount() + Storage.tra(" RT | ") + status.getFavoriteCount() + Storage.tra(" FAV");
	}

	public Status getStatus() {
		return status;
	}

	public String getWord() {
		return word;
	}

	public long getNum() {
		return num;
	}

	public double getRatio() {
		return Double.parseDouble(df.format(num/total).replaceAll(",", "."));
	}

	public String getStatusUrl() {
		return "http://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
	}
}