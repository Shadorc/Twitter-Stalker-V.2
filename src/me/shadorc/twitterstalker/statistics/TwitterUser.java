package me.shadorc.twitterstalker.statistics;

import java.text.DateFormat;
import java.util.Date;

import javax.swing.ImageIcon;

import me.shadorc.twitterstalker.Main;
import me.shadorc.twitterstalker.graphics.SearchField.Text;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.statistics.RoundedImage.Scaling;
import me.shadorc.twitterstalker.storage.Data.NumbersEnum;
import me.shadorc.twitterstalker.storage.Storage;
import twitter4j.TwitterException;
import twitter4j.User;

public class TwitterUser {

	private User user;
	private int tweetsAnalyzed;
	private int mentionsAnalyzed;

	public TwitterUser(String name) throws TwitterException {
		try {
			this.user = Main.getTwitter().showUser(name);
		} catch (TwitterException e) {
			throw new TwitterException(Storage.tra(Text.INVALID_USER), new Exception(name));
		}

		if(user.getStatusesCount() == 0) {
			throw new TwitterException(Storage.tra(Text.NO_TWEET), new Exception(this.getName()));
		}

		//Test if user's timeline is available
		try {
			Main.getTwitter().getUserTimeline(user.getId());
		} catch(TwitterException e) {
			throw new TwitterException(Storage.tra(Text.PRIVATE), new Exception(this.getName()));
		}
	}

	public float getTwitterMoney(Stats stats) {
		float money = stats.get(NumbersEnum.TWEETS_PER_DAY).getNum();	//Get tweets per day
		money *= (float) this.getFollowersCount();						//Potential views on tweet per day
		money = (float) ((money * 25f) / 100f);							//Estimated views per day
		money *= 9.12;													//9.12 = Twitter Value / Tweets per day on Twitter (centimes)
		money /= 100;													//Euro conversion
		return money;
	}

	public boolean isPrivate() {
		return user.isProtected();
	}

	public void incremenAnalyzedTweets() {
		this.tweetsAnalyzed++;
	}

	public void incremenAnalyzedMentions() {
		this.mentionsAnalyzed++;
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

	public ImageIcon getProfileRoundedImage() {
		return RoundedImage.create(user.getOriginalProfileImageURL().replaceAll(".jpeg", "_400x400.jpeg"), Scaling.NORMAL);
	}

	public long getId() {
		return user.getId();
	}
}