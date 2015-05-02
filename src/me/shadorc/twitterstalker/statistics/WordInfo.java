package me.shadorc.twitterstalker.statistics;

import java.util.Date;

import me.shadorc.twitterstalker.graphics.Storage;
import twitter4j.Status;
import twitter4j.TwitterException;

public class WordInfo {

	private Status status;
	private String word;
	private long count;

	WordInfo(String word) {
		this.word = word;
		this.count = 0;
	}

	WordInfo(Status status) {
		this.status = status;
		this.count = status.getRetweetCount() + status.getFavoriteCount();
	}

	public void increment() {
		count++;
	}

	public void setNum(long count) {
		this.count = count;
	}

	public String getInfo() {
		if(status != null) {
			return this.getRetweet() + Storage.tra(" RT | ") + this.getFavorite() + Storage.tra(" FAV (") + this.getCount() + ")";
		}
		return word + " (" + count + ")";
	}

	public String getUserInfo() throws TwitterException {
		return "<img src=" + new TwitterUser(word).getImageUrl() + " border=1 align=middle> " + " @" + this.getInfo();
	}

	public String getWord() {
		return word;
	}

	public String getText() {
		return status.getText();
	}

	public String getStatusUrl() {
		return "http://twitter.com/" + status.getUser().getScreenName() + "/status/" + this.getId();
	}

	public long getCount() {
		return count;
	}

	public int getRetweet() {
		return status.getRetweetCount();
	}

	public int getFavorite() {
		return status.getFavoriteCount();
	}

	public Date getDate() {
		return status.getCreatedAt();
	}

	public long getId() {
		return status.getId();
	}
}