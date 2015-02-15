package me.shadorc.twitterstalker.statistics;

import java.util.Date;

import twitter4j.Status;
import twitter4j.TwitterException;

public class WordInfo {

	private Status status;
	private String word;
	private int count;

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

	public String getInfo() {
		if(status != null) {
			return this.getRetweet() + " RT | " + this.getFavorite() + " FAV (" + this.getCount() + ")";
		}
		return word + " (" + count + ")";
	}

	public String getUserInfo() throws TwitterException {
		return this.getImageHTML() + " @" + this.getInfo();
	}

	public String getImageHTML() throws TwitterException {
		return "<img src=" + new TwitterUser(word).getImageUrl() + " height=35 width=35> ";
	}

	public String getText() {
		return status.getText();
	}

	public String getStatusUrl() {
		return "http://twitter.com/" + status.getUser().getScreenName() + "/status/" + this.getId();
	}

	public String getImage() {
		return "<img src=" + this.getStatusUrl() + " height=35 width=35>";
	}

	public int getCount() {
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