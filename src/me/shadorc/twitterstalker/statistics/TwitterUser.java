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
import me.shadorc.twitterstalker.graphics.Storage;
import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.TextField.Text;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import twitter4j.TwitterException;
import twitter4j.User;

public class TwitterUser {

	private User user;
	private int tweetsAnalyzed;
	private int mentionsAnalyzed;

	public TwitterUser(String userName) throws TwitterException {
		try {
			this.user = Frame.getTwitter().showUser(userName);
		} catch (TwitterException e) {
			throw new TwitterException(Storage.tra(Text.INVALID_USER), new Exception(userName), 604);
		}

		if(this.getTweetsPosted() == 0) {
			throw new TwitterException(Storage.tra(Text.NO_TWEET), new Exception(userName), 600);
		}

		if(this.isPrivate() && !this.getName().equals(Frame.getTwitter().getScreenName())) {
			throw new TwitterException(Storage.tra(Text.PRIVATE), new Exception(userName), 401);
		}
	}

	public double getTwitterMoney(Stats stats) {
		double money = stats.getUnique(Data.TWEETS_PER_DAY).getRatio(); //Get tweets per day
		money *= (double) this.getFollowersCount(); 					//Potential views on tweet per day
		money = (double) ((money * 25) / 100);							//Estimated views per day
		money *= 9.12;													//9.12 = Twitter Value / Tweets per day on Twitter (centimes)
		money /= 100;													//Euro conversion
		return money;
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
		return stats.getUnique(Data.TWEETS_PER_DAY).getWord() + stats.getUnique(Data.TWEETS_PER_DAY).getRatio();
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