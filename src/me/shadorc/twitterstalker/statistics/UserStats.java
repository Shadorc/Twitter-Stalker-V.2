package me.shadorc.twitterstalker.statistics;

import java.text.DateFormat;
import java.util.Date;

import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.storage.Storage;
import me.shadorc.twitterstalker.utility.Ressources;
import twitter4j.Status;
import twitter4j.TwitterException;


public class UserStats {

	private long id;
	private UsersMap userStatsMap;
	private long num;
	private Status status;

	public UserStats(long id, UsersMap userStatsMap) {
		this.id = id;
		this.userStatsMap = userStatsMap;
		this.num = 0;
	}

	//Used by FIRST_TALK
	public UserStats(long id, Status status) {
		this.id = id;
		this.status = status;
		this.num = status.getCreatedAt().getTime();
	}

	public long getId() {
		return id;
	}

	public long getNum() {
		return num;
	}

	public Status getStatus() {
		return status;
	}

	public void increment() {
		this.num++;
	}

	@Override
	public String toString() {
		try {
			TwitterUser user = new TwitterUser(id);
			String img = "<img src=" + user.getImageUrl() + " border=1 align=middle>";
			if(userStatsMap != null) {
				return img + " " + this.getNum() + " " + "@" + user.getName() + " (" + Ressources.format(this.getNum()/userStatsMap.getTotal()*100.0) + "%)";
			} else {
				//FIRST_TALK
				String date = DateFormat.getDateInstance(DateFormat.SHORT, OptionsPanel.getLocaleLang()).format(new Date(this.getNum()));
				return img + " <a href=" + Ressources.getStatusURL(status) + "> @" + user.getName() + "</a> (" + date + ")";
			}
		} catch (TwitterException e) {
			return Storage.tra("nonExistentUser");
		}
	}
}