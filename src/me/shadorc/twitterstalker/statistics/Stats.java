package me.shadorc.twitterstalker.statistics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;

import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.Storage;
import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import twitter4j.HashtagEntity;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

public class Stats {

	public static boolean stop;

	private HashMap <Data, StatInfo> stats;
	private DecimalFormat df;
	private boolean isArchive;

	public Stats(TwitterUser user, JButton bu, List <Status> statusList) throws TwitterException {
		stop = false;

		if(user.getTweetsPosted() == 0) {
			throw new TwitterException(Storage.tra("L'utilisateur n'a posté aucun tweet"), new Exception(user.getName()), 600);
		}

		this.stats = new HashMap <Data, StatInfo> ();
		this.df = new DecimalFormat("#.#");
		this.isArchive = (statusList != null);

		int tweetsToAnalyze = (statusList != null) ? statusList.size() : user.getTweetsPosted();

		if(tweetsToAnalyze > OptionsPanel.getMaxTweetsNumber()) {
			tweetsToAnalyze = OptionsPanel.getMaxTweetsNumber();
		}

		if(user.isPrivate() && !user.getName().equals(Frame.getTwitter().getScreenName())) {
			bu.setEnabled(true);
			bu.setText(null);
			throw new TwitterException(Storage.tra("L'utilisateur est protégé"), new Exception(user.getName()), 401);
		}

		bu.setEnabled(false);
		bu.setText("0%");

		stats.put(Data.TWEETS, new StatInfo());
		stats.put(Data.WORDS_PER_TWEET, new StatInfo());
		stats.put(Data.LETTERS_PER_TWEET, new StatInfo());
		stats.put(Data.LETTERS_PER_WORD, new StatInfo());
		stats.put(Data.TIMELINE, new StatInfo());
		stats.put(Data.PURETWEETS_COUNT, new StatInfo());
		stats.put(Data.MENTIONS_COUNT, new StatInfo());
		stats.put(Data.RETWEET_BY_ME, new StatInfo());
		stats.put(Data.REPUTE, new StatInfo());
		stats.put(Data.FAVORITE, new StatInfo());
		stats.put(Data.RETWEET, new StatInfo());
		stats.put(Data.SOURCE, new StatInfo());
		stats.put(Data.DAYS, new StatInfo());
		stats.put(Data.HOURS, new StatInfo());
		stats.put(Data.WORDS, new StatInfo());
		stats.put(Data.HASHTAG, new StatInfo());
		stats.put(Data.LANG, new StatInfo());
		stats.put(Data.POPULARE, new StatInfo());
		stats.put(Data.MENTIONS_SENT, new StatInfo());
		stats.put(Data.MENTIONS_RECEIVED, new StatInfo());
		stats.put(Data.WORDS_COUNT, new StatInfo());
		stats.put(Data.HASHTAG_COUNT, new StatInfo());
		stats.put(Data.LETTERS, new StatInfo());
		stats.put(Data.MEDIA, new StatInfo());
		stats.put(Data.URL, new StatInfo());
		stats.put(Data.TWEET_PER_DAYS, new StatInfo());
		stats.put(Data.FIRST_TALK, new StatInfo());

		double timeTweet = 1;
		double timeFirstTweet = 1;

		for(int i = 1; user.getTweetsAnalyzed() < tweetsToAnalyze; i++) {

			RateLimitStatus rls = Frame.getTwitter().getRateLimitStatus().get("/statuses/user_timeline");
			System.out.println("User timeline : " + rls.getRemaining() + " / " + rls.getLimit() + ", réinitialisation dans " + (rls.getSecondsUntilReset()/60) + "min " + (int) ((rls.getSecondsUntilReset()/60f - (int) (rls.getSecondsUntilReset()/60f))*60) + "s");

			List <Status> timeline = (statusList == null) ? Frame.getTwitter().getUserTimeline(user.getName(), new Paging(i, 200)) : statusList;

			for(Status status : timeline) {

				if(stop) return;

				//Number of days since this tweet was posted
				timeTweet = (new Date().getTime() - status.getCreatedAt().getTime()) / 86400000;
				if(timeTweet > timeFirstTweet) timeFirstTweet = timeTweet;

				user.incremenAnalyzedTweets();
				this.setStats(status);

				for(UserMentionEntity mention : status.getUserMentionEntities()) {
					stats.get(Data.MENTIONS_SENT).add(mention.getScreenName());
					stats.get(Data.FIRST_TALK).add(mention.getScreenName(), status.getCreatedAt());
				}
				for(HashtagEntity hashtag : status.getHashtagEntities()) {
					stats.get(Data.HASHTAG).add("#" + hashtag.getText().toLowerCase());
					this.getUnique(Data.HASHTAG_COUNT).increment();
				}
			}

			double progress = (100.0 * user.getTweetsAnalyzed()) / tweetsToAnalyze;
			if(progress > 100) progress = 100;
			bu.setText(df.format(progress) + "%");
		}

		if(user.getName().equals(Frame.getTwitter().getScreenName())) {
			for(int i = 1; user.getMentionsAnalyzed() < OptionsPanel.getMaxMentionsNumber(); i++) {

				int secure = user.getMentionsAnalyzed();

				RateLimitStatus rls = Frame.getTwitter().getRateLimitStatus().get("/statuses/mentions_timeline");
				System.out.println("Mentions timeline : " + rls.getRemaining() + " / " + rls.getLimit() + ", réinitialisation dans " + (rls.getSecondsUntilReset()/60) + "min " + (int) ((rls.getSecondsUntilReset()/60f - (int) (rls.getSecondsUntilReset()/60f))*60) + "s");

				try {
					for(Status status : Frame.getTwitter().getMentionsTimeline(new Paging(i, 200))) {

						if(stop) return;

						user.incremenAnalyzedMentions();
						stats.get(Data.MENTIONS_RECEIVED).add(status.getUser().getScreenName());
					}
				} catch(TwitterException e) {
					//API mentions limit reachs, ignore it.
					break;
				}

				//User has never tweeted mentions or tweeted fewer than 150 mentions
				if(secure == user.getMentionsAnalyzed() || user.getMentionsAnalyzed() < 150) {
					break;
				}
			}
		}

		stats.put(Data.TWEET_PER_DAYS, new StatInfo(Storage.tra("Nombre de tweets/jour : "), user.getTweetsAnalyzed(), timeFirstTweet));
		stats.put(Data.WORDS_PER_TWEET, new StatInfo(Storage.tra("mots par tweet"), this.getUnique(Data.WORDS_COUNT).getNum(), user.getTweetsAnalyzed()));
		stats.put(Data.LETTERS_PER_TWEET, new StatInfo(Storage.tra("lettres par tweet"), this.getUnique(Data.LETTERS).getNum(), user.getTweetsAnalyzed()));
		stats.put(Data.LETTERS_PER_WORD, new StatInfo(Storage.tra("lettres par mot"), this.getUnique(Data.LETTERS).getNum(), this.getUnique(Data.WORDS_COUNT).getNum()));
		stats.put(Data.PURETWEETS_COUNT, new StatInfo("Puretweets", (user.getTweetsAnalyzed() - this.getUnique(Data.MENTIONS_COUNT).getNum() - this.getUnique(Data.RETWEET_BY_ME).getNum()), user.getTweetsAnalyzed()));
		stats.put(Data.MENTIONS_COUNT, new StatInfo(Storage.tra("Mentions"), this.getUnique(Data.MENTIONS_COUNT).getNum(), user.getTweetsAnalyzed()));
		stats.put(Data.RETWEET_BY_ME, new StatInfo(Storage.tra("Retweets"), this.getUnique(Data.RETWEET_BY_ME).getNum(), user.getTweetsAnalyzed()));
		stats.put(Data.FAVORITE, new StatInfo(Storage.tra("Favorisés"), this.getUnique(Data.FAVORITE).getNum(), user.getTweetsAnalyzed()));
		stats.put(Data.RETWEET, new StatInfo(Storage.tra("Retweetés"), this.getUnique(Data.RETWEET).getNum(), user.getTweetsAnalyzed()));
		stats.put(Data.MEDIA, new StatInfo(Storage.tra("Médias"), this.getUnique(Data.MEDIA).getNum(), user.getTweetsAnalyzed()));
		stats.put(Data.URL, new StatInfo(Storage.tra("URL"), this.getUnique(Data.URL).getNum(), user.getTweetsAnalyzed()));

		//Set the number by which they will be divided to provide a ratio/percentage
		stats.get(Data.SOURCE).setTotal(user.getTweetsAnalyzed());
		stats.get(Data.DAYS).setTotal(user.getTweetsAnalyzed());
		stats.get(Data.HOURS).setTotal(user.getTweetsAnalyzed());
		stats.get(Data.LANG).setTotal(user.getTweetsAnalyzed());
		stats.get(Data.MENTIONS_RECEIVED).setTotal(user.getMentionsAnalyzed());
		stats.get(Data.MENTIONS_SENT).setTotal((int) this.getUnique(Data.MENTIONS_COUNT).getNum());
		stats.get(Data.WORDS).setTotal((int) this.getUnique(Data.WORDS_COUNT).getNum());
		stats.get(Data.HASHTAG).setTotal((int) this.getUnique(Data.HASHTAG_COUNT).getNum());
	}

	private void setStats(Status status) throws TwitterException {

		if(status.isRetweet()) {
			this.getUnique(Data.RETWEET_BY_ME).increment();

		} else {

			if(status.getRetweetCount() + status.getFavoriteCount() > 0)	stats.get(Data.POPULARE).add(new WordInfo(status));
			if(status.getRetweetCount() > 0)								this.getUnique(Data.RETWEET).increment();
			if(status.getFavoriteCount() > 0)								this.getUnique(Data.FAVORITE).increment();
			if(status.getText().startsWith("@"))							this.getUnique(Data.MENTIONS_COUNT).increment();
			if(status.getMediaEntities().length > 0)						this.getUnique(Data.MEDIA).increment();
			if(status.getURLEntities().length > 0)							this.getUnique(Data.URL).increment();

			//Regex removes HTML tag
			stats.get(Data.SOURCE).add(status.getSource().replaceAll("<[^>]*>", ""));

			//If it's an archive that is analyzed, lang doesn't exist and throws null
			if(!isArchive) {
				//Lang is two-letter iso language code
				String lang = new Locale(status.getLang()).getDisplayLanguage(OptionsPanel.getLocaleLang());
				String capLang = lang.substring(0, 1).toUpperCase() + lang.substring(1);
				stats.get(Data.LANG).add(capLang);
			}

			//Get day and add capitalize
			String day = new SimpleDateFormat("EEEE", OptionsPanel.getLocaleLang()).format(status.getCreatedAt());
			String capDay = day.substring(0, 1).toUpperCase() + day.substring(1);
			stats.get(Data.DAYS).add(capDay);

			//Gets the hour as 24
			stats.get(Data.HOURS).add(new SimpleDateFormat("H").format(status.getCreatedAt()) + "h");

			//Split spaces and line breaks ("|\\" equals "and")
			for(String word : status.getText().split(" |\\\n")) {

				//Delete all letters except a-z/A-Z/0-9/@/# and accents and lowercase
				word = word.replaceAll("[^a-zA-ZÀ-ÿ0-9^@#]", "").toLowerCase();

				this.getUnique(Data.WORDS_COUNT).increment();
				this.getUnique(Data.LETTERS).increment(word.length());

				if(word.length() >= OptionsPanel.getMinLettersWord() && !word.startsWith("#") && !word.startsWith("@")) {
					stats.get(Data.WORDS).add(word);
				}
			}
		}
	}

	public WordInfo get(Data type, int i) {
		return stats.get(type).sort().get(i);
	}

	public List <WordInfo> get(Data type) {
		return stats.get(type).sort();
	}

	public WordInfo getUnique(Data type) {
		return stats.get(type).getWordInfo();
	}

	public static void stop() {
		stop = true;
	}
}