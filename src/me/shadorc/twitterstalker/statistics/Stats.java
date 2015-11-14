package me.shadorc.twitterstalker.statistics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;

import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.storage.Data.Statistics;
import me.shadorc.twitterstalker.storage.Storage;
import twitter4j.HashtagEntity;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

public class Stats {

	public static boolean stop;

	private HashMap <Statistics, StatInfo> stats;
	private DecimalFormat df;
	private boolean isArchive;

	public Stats(TwitterUser user, JButton bu, List <Status> statusList) throws TwitterException {
		stop = false;

		this.stats = new HashMap <Statistics, StatInfo> ();
		this.df = new DecimalFormat("#.#");
		this.isArchive = (statusList != null);

		int tweetsToAnalyze = (statusList != null) ? statusList.size() : user.getTweetsPosted();

		if(tweetsToAnalyze > OptionsPanel.getMaxTweetsNumber()) {
			tweetsToAnalyze = OptionsPanel.getMaxTweetsNumber() - 200; //-200 : avoid going beyond 3000
		}

		bu.setEnabled(false);
		bu.setText("0%");

		stats.put(Statistics.TWEETS, new StatInfo());
		stats.put(Statistics.WORDS_PER_TWEET, new StatInfo());
		stats.put(Statistics.LETTERS_PER_TWEET, new StatInfo());
		stats.put(Statistics.LETTERS_PER_WORD, new StatInfo());
		stats.put(Statistics.TIMELINE, new StatInfo());
		stats.put(Statistics.PURETWEETS_COUNT, new StatInfo());
		stats.put(Statistics.MENTIONS_COUNT, new StatInfo());
		stats.put(Statistics.RETWEET_BY_ME, new StatInfo());
		stats.put(Statistics.REPUTE, new StatInfo());
		stats.put(Statistics.FAVORITE, new StatInfo());
		stats.put(Statistics.RETWEET, new StatInfo());
		stats.put(Statistics.SOURCE, new StatInfo());
		stats.put(Statistics.DAYS, new StatInfo());
		stats.put(Statistics.HOURS, new StatInfo());
		stats.put(Statistics.WORDS, new StatInfo());
		stats.put(Statistics.HASHTAG, new StatInfo());
		stats.put(Statistics.LANG, new StatInfo());
		stats.put(Statistics.POPULARE, new StatInfo());
		stats.put(Statistics.MENTIONS_SENT, new StatInfo());
		stats.put(Statistics.MENTIONS_RECEIVED, new StatInfo());
		stats.put(Statistics.WORDS_COUNT, new StatInfo());
		stats.put(Statistics.HASHTAG_COUNT, new StatInfo());
		stats.put(Statistics.LETTERS, new StatInfo());
		stats.put(Statistics.MEDIA, new StatInfo());
		stats.put(Statistics.URL, new StatInfo());
		stats.put(Statistics.TWEETS_PER_DAY, new StatInfo());
		stats.put(Statistics.FIRST_TALK, new StatInfo());

		long timeTweet = 1;
		long timeFirstTweet = 1;

		for(int i = 1; user.getTweetsAnalyzed() <= tweetsToAnalyze; i++) {

			RateLimitStatus rls = Frame.getTwitter().getRateLimitStatus().get("/statuses/user_timeline");
			System.out.println("[User timeline] Remaining requests : " + rls.getRemaining() + "/" + rls.getLimit() + ". Reset in " + (rls.getSecondsUntilReset()/60) + "min " + (rls.getSecondsUntilReset()%60) + "s");

			List <Status> timeline = (statusList == null) ? Frame.getTwitter().getUserTimeline(user.getName(), new Paging(i, 200)) : statusList;

			for(Status status : timeline) {

				if(stop) return;

				user.incremenAnalyzedTweets();

				//Number of milliseconds since this tweet was posted
				timeTweet = new Date().getTime() - status.getCreatedAt().getTime();
				if(timeTweet > timeFirstTweet) timeFirstTweet = timeTweet;

				this.setStats(status);
			}

			double progress = (100.0 * user.getTweetsAnalyzed()) / tweetsToAnalyze;
			if(progress > 100) progress = 100;
			bu.setText(df.format(progress) + "%");
		}

		if(user.getName().equals(Frame.getTwitter().getScreenName())) {
			for(int i = 1; user.getMentionsAnalyzed() < OptionsPanel.getMaxMentionsNumber(); i++) {

				int secure = user.getMentionsAnalyzed();

				RateLimitStatus rls = Frame.getTwitter().getRateLimitStatus().get("/statuses/mentions_timeline");
				System.out.println("[Mentions timeline] Remaining requests : " + rls.getRemaining() + "/" + rls.getLimit() + ". Reset in " + (rls.getSecondsUntilReset()/60) + "min " + (rls.getSecondsUntilReset()%60) + "s");

				try {
					for(Status status : Frame.getTwitter().getMentionsTimeline(new Paging(i, 200))) {

						if(stop) return;

						user.incremenAnalyzedMentions();
						stats.get(Statistics.MENTIONS_RECEIVED).add(status.getUser().getScreenName());
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

		stats.put(Statistics.TWEETS_PER_DAY, new StatInfo(Storage.tra("numTweetsPerDay"), user.getTweetsAnalyzed(), timeFirstTweet));
		stats.put(Statistics.WORDS_PER_TWEET, new StatInfo(Storage.tra("wordsPerTweet"), this.getUnique(Statistics.WORDS_COUNT).getNum(), user.getTweetsAnalyzed()));
		stats.put(Statistics.LETTERS_PER_TWEET, new StatInfo(Storage.tra("lettersPerTweet"), this.getUnique(Statistics.LETTERS).getNum(), user.getTweetsAnalyzed()));
		stats.put(Statistics.LETTERS_PER_WORD, new StatInfo(Storage.tra("lettersPerWord"), this.getUnique(Statistics.LETTERS).getNum(), this.getUnique(Statistics.WORDS_COUNT).getNum()));
		stats.put(Statistics.PURETWEETS_COUNT, new StatInfo(Storage.tra("puretweet"), (user.getTweetsAnalyzed() - this.getUnique(Statistics.MENTIONS_COUNT).getNum() - this.getUnique(Statistics.RETWEET_BY_ME).getNum()), user.getTweetsAnalyzed()));
		stats.put(Statistics.MENTIONS_COUNT, new StatInfo(Storage.tra("mentions"), this.getUnique(Statistics.MENTIONS_COUNT).getNum(), user.getTweetsAnalyzed()));
		stats.put(Statistics.RETWEET_BY_ME, new StatInfo(Storage.tra("retweets"), this.getUnique(Statistics.RETWEET_BY_ME).getNum(), user.getTweetsAnalyzed()));
		stats.put(Statistics.FAVORITE, new StatInfo(Storage.tra("favored"), this.getUnique(Statistics.FAVORITE).getNum(), user.getTweetsAnalyzed()));
		stats.put(Statistics.RETWEET, new StatInfo(Storage.tra("retweeted"), this.getUnique(Statistics.RETWEET).getNum(), user.getTweetsAnalyzed()));
		stats.put(Statistics.MEDIA, new StatInfo(Storage.tra("medias"), this.getUnique(Statistics.MEDIA).getNum(), user.getTweetsAnalyzed()));
		stats.put(Statistics.URL, new StatInfo(Storage.tra("url"), this.getUnique(Statistics.URL).getNum(), user.getTweetsAnalyzed()));

		//Set the number by which they will be divided to provide a ratio/percentage
		stats.get(Statistics.SOURCE).setTotal(user.getTweetsAnalyzed());
		stats.get(Statistics.DAYS).setTotal(user.getTweetsAnalyzed());
		stats.get(Statistics.HOURS).setTotal(user.getTweetsAnalyzed());
		stats.get(Statistics.LANG).setTotal(user.getTweetsAnalyzed());
		stats.get(Statistics.MENTIONS_RECEIVED).setTotal(user.getMentionsAnalyzed());
		stats.get(Statistics.MENTIONS_SENT).setTotal((int) this.getUnique(Statistics.MENTIONS_COUNT).getNum());
		stats.get(Statistics.WORDS).setTotal((int) this.getUnique(Statistics.WORDS_COUNT).getNum());
		stats.get(Statistics.HASHTAG).setTotal((int) this.getUnique(Statistics.HASHTAG_COUNT).getNum());
	}

	private void setStats(Status status) throws TwitterException {

		if(status.isRetweet()) {
			this.getUnique(Statistics.RETWEET_BY_ME).increment();

		} else {

			for(UserMentionEntity mention : status.getUserMentionEntities()) {
				stats.get(Statistics.MENTIONS_SENT).add(mention.getScreenName());
				stats.get(Statistics.FIRST_TALK).add(mention.getScreenName(), status.getCreatedAt(), status);
			}
			for(HashtagEntity hashtag : status.getHashtagEntities()) {
				stats.get(Statistics.HASHTAG).add("#" + hashtag.getText().toLowerCase());
				this.getUnique(Statistics.HASHTAG_COUNT).increment();
			}

			if(status.getRetweetCount() + status.getFavoriteCount() > 0)	stats.get(Statistics.POPULARE).add(new WordInfo(status));
			if(status.getRetweetCount() > 0)								this.getUnique(Statistics.RETWEET).increment();
			if(status.getFavoriteCount() > 0)								this.getUnique(Statistics.FAVORITE).increment();
			if(status.getText().startsWith("@"))							this.getUnique(Statistics.MENTIONS_COUNT).increment();
			if(status.getMediaEntities().length > 0)						this.getUnique(Statistics.MEDIA).increment();
			if(status.getURLEntities().length > 0)							this.getUnique(Statistics.URL).increment();

			//Regex removes HTML tag
			stats.get(Statistics.SOURCE).add(status.getSource().replaceAll("<[^>]*>", ""));

			//If it's an archive that is analyzed, lang doesn't exist and throws null
			if(!isArchive) {
				//Lang is two-letter iso language code
				String lang = new Locale(status.getLang()).getDisplayLanguage(OptionsPanel.getLocaleLang());
				String capLang = lang.substring(0, 1).toUpperCase() + lang.substring(1);
				stats.get(Statistics.LANG).add(capLang);
			}

			//Get day and add capitalize
			String day = new SimpleDateFormat("EEEE", OptionsPanel.getLocaleLang()).format(status.getCreatedAt());
			String capDay = day.substring(0, 1).toUpperCase() + day.substring(1);
			stats.get(Statistics.DAYS).add(capDay);

			//Gets the hour as 24
			stats.get(Statistics.HOURS).add(new SimpleDateFormat("H").format(status.getCreatedAt()) + "h");

			//Split spaces and line breaks ("|\\" equals "and")
			for(String word : status.getText().split(" |\\\n")) {

				//Delete all letters except a-z/A-Z/0-9/@/# and accents and lowercase
				word = word.replaceAll("[^a-zA-ZÀ-ÿ0-9^@#]", "").toLowerCase();

				this.getUnique(Statistics.WORDS_COUNT).increment();
				this.getUnique(Statistics.LETTERS).increment(word.length());

				if(word.length() >= OptionsPanel.getMinLettersWord() && !word.startsWith("#") && !word.startsWith("@")) {
					stats.get(Statistics.WORDS).add(word);
				}
			}
		}
	}

	public WordInfo get(Statistics type, int i) {
		return stats.get(type).sort().get(i);
	}

	public List <WordInfo> get(Statistics type) {
		return stats.get(type).sort();
	}

	public WordInfo getUnique(Statistics type) {
		return stats.get(type).getWordInfo();
	}

	public static void stop() {
		stop = true;
	}
}