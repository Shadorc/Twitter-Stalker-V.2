package me.shadorc.twitterstalker.statistics;

import java.text.DecimalFormat;

import me.shadorc.twitterstalker.graphics.Storage;
import twitter4j.Status;
import twitter4j.TwitterException;

public class WordInfo {

	private Status status;
	private String word;
	private double num;
	private double total; 

	private DecimalFormat df = new DecimalFormat("#.#");

	WordInfo(String word, double num, double total) {
		this.word = word;
		this.num = num;
		this.total = total;
	}

	WordInfo(String word) {
		this.word = word;
		this.num = 0;
	}

	WordInfo(Status status) {
		this.status = status;
		this.num = status.getRetweetCount() + status.getFavoriteCount();
	}

	public void increment() {
		num++;
	}

	//Used by FIRST_TALK to set the first talking date
	public void setNum(double num) {
		this.num = num;
	}

	public String getInfo() {
		return df.format(100*num/total) + "% " + word + " (" + num + ")";
	}

	public String getUserInfo() throws TwitterException {
		return "<img src=" + new TwitterUser(word).getImageUrl() + " border=1 align=middle> " + " @" + this.getInfo();
	}

	public String getStatusInfo() {
		return status.getRetweetCount() + Storage.tra(" RT | ") + status.getFavoriteCount() + Storage.tra(" FAV (") + this.getNum() + ")";
	}

	public String getWord() {
		return word;
	}

	public Status getStatus() {
		return status;
	}

	public double getNum() {
		return num;
	}

	public String getStatusUrl() {
		return "http://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
	}

	public void setTotal(double total) {
		this.total = total;
	}
}