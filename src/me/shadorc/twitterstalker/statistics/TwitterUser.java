package me.shadorc.twitterstalker.statistics;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import twitter4j.TwitterException;
import twitter4j.User;

public class TwitterUser {

	private User user;
	private int tweetsAnalyzed;
	private int mentionsAnalyzed;

	public TwitterUser(String user) throws TwitterException {
		this.user = Frame.getTwitter().showUser(user);
	}

	public boolean isPrivate() {
		return user.isProtected();
	}

	public void incremenAnalyzedTweets() {
		tweetsAnalyzed++;
	}

	public void incremenAnalyzedMentions() {
		mentionsAnalyzed++;
	}

	public String getName() {
		return user.getScreenName();
	}

	public String getImageUrl() {
		return user.getProfileImageURL();
	}

	public String getCreatedAt() {
		return DateFormat.getDateInstance(DateFormat.SHORT, OptionsPanel.getLocaleLang()).format(user.getCreatedAt());
	}

	public String getTweetsPerDay(Stats stats) {
		return stats.getUnique(Data.TWEET_PER_DAYS).getWord() + stats.getUnique(Data.TWEET_PER_DAYS).getRatio();
	}

	public int getTweetsAnalyzed() {
		return tweetsAnalyzed;
	}

	public int getMentionsAnalyzed() {
		return mentionsAnalyzed;
	}

	public int getTweetsPosted() {
		return user.getStatusesCount();
	}

	public int getFollowersCount() {
		return user.getFollowersCount();
	}

	public int getFollowingCount() {
		return user.getFriendsCount();
	}

	public long getAge() {
		return (new Date().getTime() - user.getCreatedAt().getTime()) / 86400000;
	}

	public JLabel getProfileImage() {

		Image image = new ImageIcon(this.getClass().getResource("/res/fictitious.png")).getImage();

		try {
			URL url = new URL(user.getOriginalProfileImageURL().replaceAll(".jpeg", "_400x400.jpeg"));
			image = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new UserImage(image);
	}
}