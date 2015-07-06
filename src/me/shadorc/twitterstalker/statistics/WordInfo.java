package me.shadorc.twitterstalker.statistics;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import me.shadorc.twitterstalker.graphics.Storage;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
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

	//StatInfo is used to get the total
	WordInfo(String word, StatInfo si) {
		this.word = word;
		this.si = si;
		this.num = 0;
		this.total = 1;
	}

	//Used by Data.POPULARE
	WordInfo(Status status) {
		this.status = status;
		this.num = status.getRetweetCount() + status.getFavoriteCount();
	}

	//Used by Data.FIRST_TALK
	WordInfo(String word, long num, Status status) {
		this.word = word;
		this.num = num;
		this.status = status;
	}

	public void increment() {
		this.num++;
	}

	public void increment(int num) {
		this.num += num;
	}

	public String getInfo() {
		return df.format(num/total) + " " + word;
	}

	public String getPercenInfo() {
		if(si != null) total = si.getTotal();
		return  word + " (" + df.format(100*num/total) + "%)";
	}

	public String getFirstTalkInfo() {
		String date = DateFormat.getDateInstance(DateFormat.SHORT, OptionsPanel.getLocaleLang()).format(new Date(this.getNum()));
		String image = "";
		try {
			image = this.getUserImage();
		} catch(TwitterException ignore) {
			//User doesn't exist anymore
		}
		return image + " <a href=" + this.getStatusUrl() + "> @" + this.getWord() + "</a> (" + date + ")";
	}

	public String getUserInfo() throws TwitterException {
		return this.getUserImage() + " " + " @" + this.getPercenInfo();
	}

	public String getUserImage() throws TwitterException {
		return "<img src=" + new TwitterUser(word).getImageUrl() + " border=1 align=middle>";
	}

	public String getStatusInfo() {
		return status.getRetweetCount() + Storage.tra("rt") + status.getFavoriteCount() + Storage.tra("fav");
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