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
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

public class Stats {

	public static boolean stop;

	private HashMap <Data, StatInfo> stats;
	private DecimalFormat df;

	public Stats(TwitterUser user, JButton bu) throws TwitterException {
		stop = false;
		stats = new HashMap <> ();
		df = new DecimalFormat("#.#");

		int tweetsToAnalyse = user.getTweetsPosted();
		double lastTweet = 0;

		if(tweetsToAnalyse == 0) {
			throw new TwitterException(Storage.tra("L'utilisateur n'a posté aucun tweet"), new Exception(user.getName()), 600);
		} else if(tweetsToAnalyse > OptionsPanel.getMaxTweetsNumber()) {
			tweetsToAnalyse = OptionsPanel.getMaxTweetsNumber();
		}

		if(user.isPrivate() && !user.getName().equals(Frame.getTwitter().getScreenName())) {
			bu.setEnabled(true);
			bu.setText(null);
			throw new TwitterException(Storage.tra("L'utilisateur est protégé"), new Exception(user.getName()), 401);
		}

		bu.setEnabled(false);
		bu.setText("0%");

		stats.put(Data.MENTIONS_NUMBER, new StatInfo());
		stats.put(Data.TWEETS_NUMBER, new StatInfo());
		stats.put(Data.MENTIONS, new StatInfo());
		stats.put(Data.RETWEET_BY_ME, new StatInfo());
		stats.put(Data.RETWEET, new StatInfo());
		stats.put(Data.FAVORITE, new StatInfo());
		stats.put(Data.WORDS_COUNT, new StatInfo());
		stats.put(Data.LETTERS, new StatInfo());
		stats.put(Data.MENTIONS_RECEIVED, new StatInfo());
		stats.put(Data.MENTIONS_SENT, new StatInfo());
		stats.put(Data.HASHTAG, new StatInfo());
		stats.put(Data.POPULARE, new StatInfo());
		stats.put(Data.LANG, new StatInfo());
		stats.put(Data.WORDS, new StatInfo());
		stats.put(Data.DAYS, new StatInfo());
		stats.put(Data.HOURS, new StatInfo());
		stats.put(Data.SOURCE, new StatInfo());

		int secure = 0;

		for(int i = 1; user.getTweetsAnalysed() < tweetsToAnalyse; i++) {
			//If infinite loop, stop it.
			secure = user.getTweetsAnalysed();

			for(Status status : Frame.getTwitter().getUserTimeline(user.getName(), new Paging(i, 200))) {

				if(stop) {
					return;
				}

				//Last tweet in day
				lastTweet = (new Date().getTime() - status.getCreatedAt().getTime()) / 86400000;

				user.incremenAnalyzedTweets();
				this.setStats(status);

				for(UserMentionEntity mention : status.getUserMentionEntities()) {
					stats.get(Data.MENTIONS_SENT).add(mention.getScreenName());
				}
				for(HashtagEntity hashtag : status.getHashtagEntities()) {
					stats.get(Data.HASHTAG).add("#" + hashtag.getText().toLowerCase());
				}
			}

			double progress = (100.0 * user.getTweetsAnalysed()) / (tweetsToAnalyse);
			if(progress > 100) progress = 100;
			bu.setText(df.format(progress) + "%");

			if(secure == user.getTweetsAnalysed()) {
				System.err.println("ARRET D'URGENCE LOLILOL L'API TWITTER : Tweets");
				break;
			}
		}

		if(user.getName().equals(Frame.getTwitter().getScreenName())) {
			for(int i = 1; user.getMentionsAnalysed() < OptionsPanel.getMaxMentionsNumber(); i++) {
				//If infinite loop, stop it.
				secure = user.getMentionsAnalysed();

				try {
					for(Status status : Frame.getTwitter().getMentionsTimeline(new Paging(i, 200))) {

						if(stop) {
							return;
						}

						user.incremenAnalyzedMentions();
						stats.get(Data.MENTIONS_RECEIVED).add(status.getUser().getScreenName());
					}
				} catch(TwitterException e) {
					//API mentions limit reachs, ignore it.
					break;
				}

				if(secure == user.getMentionsAnalysed()) {
					System.err.println("ARRET D'URGENCE LOLILOL L'API TWITTER : Mentions");
					break;
				}
			}
		}

		stats.put(Data.TWEET_PER_DAYS, new StatInfo(((double) user.getTweetsAnalysed() / lastTweet), "tweets par jour"));
		stats.put(Data.WORDS_PER_TWEET, new StatInfo((stats.get(Data.WORDS_COUNT).getNum() / user.getTweetsAnalysed()), "mots par tweet"));
		stats.put(Data.LETTERS_PER_TWEET, new StatInfo((stats.get(Data.LETTERS).getNum() / user.getTweetsAnalysed()), "lettres par tweet"));
		stats.put(Data.LETTERS_PER_WORD, new StatInfo((stats.get(Data.LETTERS).getNum() / stats.get(Data.WORDS_COUNT).getNum()), "lettres par mot"));

		double puretweets = user.getTweetsAnalysed() - stats.get(Data.MENTIONS).getNum() - stats.get(Data.RETWEET_BY_ME).getNum();
		stats.put(Data.PURETWEETS, new StatInfo(puretweets, "Puretweets", user));

		stats.put(Data.MENTIONS, new StatInfo(stats.get(Data.MENTIONS).getNum(), Storage.tra("Mentions"), user));
		stats.put(Data.RETWEET_BY_ME, new StatInfo(stats.get(Data.RETWEET_BY_ME).getNum(), Storage.tra("Retweet"), user));
		stats.put(Data.RETWEET, new StatInfo(stats.get(Data.RETWEET).getNum(), Storage.tra("Retweetés"), user));
		stats.put(Data.FAVORITE, new StatInfo(stats.get(Data.FAVORITE).getNum(), Storage.tra("Favorisés"), user));
	}

	private void setStats(Status status) throws TwitterException {

		if(status.isRetweet()) {
			stats.get(Data.RETWEET_BY_ME).increment();

		} else {

			if(status.getText().startsWith("@")) {
				stats.get(Data.MENTIONS).increment();
			}
			if(status.getRetweetCount() > 0) {
				stats.get(Data.RETWEET).increment();
			}
			if(status.getFavoriteCount() > 0) {
				stats.get(Data.FAVORITE).increment();
			}

			stats.get(Data.POPULARE).add(new WordInfo(status));

			//Regex removes HTML tag
			stats.get(Data.SOURCE).add(status.getSource().replaceAll("<[^>]*>", ""));

			//Lang is two-letter iso language code
			String lang = new Locale(status.getLang()).getDisplayLanguage(OptionsPanel.getLocaleLang());
			String capLang = lang.substring(0, 1).toUpperCase() + lang.substring(1);
			stats.get(Data.LANG).add(capLang);

			//Get day and add capitalize
			String day = new SimpleDateFormat("EEEE", OptionsPanel.getLocaleLang()).format(status.getCreatedAt());
			String capDay = day.substring(0, 1).toUpperCase() + day.substring(1);
			stats.get(Data.DAYS).add(capDay);

			//Gets the hour as 24
			stats.get(Data.HOURS).add(new SimpleDateFormat("H").format(status.getCreatedAt()) + "h");

			//Split spaces and line breaks ("|\\" equals "and")
			for(String word : status.getText().split(" |\\\n")) {

				//Delete all letters except a-z/A-Z/0-9/@/# and accents lowercase them
				word = word.replaceAll("[^a-zA-ZÀ-ÿ0-9^@#]", "").toLowerCase();

				stats.get(Data.WORDS_COUNT).increment();
				stats.get(Data.LETTERS).increment(word.length());

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

	public StatInfo getUnique(Data type) {
		return stats.get(type);
	}

	public static void stop() {
		stop = true;
	}
}