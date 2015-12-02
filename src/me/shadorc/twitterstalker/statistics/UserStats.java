package me.shadorc.twitterstalker.statistics;

import java.text.DateFormat;
import java.util.Date;

import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.utility.Ressources;
import twitter4j.Status;


public class UserStats {

	private String name;
	private UsersMap userStatsMap;
	private long num;
	private Status status;

	public UserStats(String name, UsersMap userStatsMap) {
		this.name = name;
		this.userStatsMap = userStatsMap;
		this.num = 0;
	}

	//Used by FIRST_TALK
	public UserStats(String name, Status status) {
		this.name = name;
		this.status = status;
		this.num = status.getCreatedAt().getTime();
	}

	public String getName() {
		return name;
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
		String img = "<img src=" + "https://twitter.com/" + this.getName() + "/profile_image?size=normal" + " border=1 align=middle>";

		if(userStatsMap != null) {
			return img + " " + this.getNum() + " " + "<b>@" + this.getName() + "</b> (" + Ressources.format(this.getNum()/userStatsMap.getTotal()*100.0) + "%)";
		} 

		//FIRST_TALK
		else {
			String date = DateFormat.getDateInstance(DateFormat.SHORT, OptionsPanel.getLocaleLang()).format(new Date(this.getNum()));
			return img + " <a href=" + Ressources.getStatusURL(status) + "> <b>@" + this.getName() + "</b></a> (" + date + ")";
		}
	}
}