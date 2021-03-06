package me.shadorc.twitterstalker.statistics;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;

import org.apache.commons.io.FileUtils;

import com.sun.xml.internal.ws.util.StringUtils;

import me.shadorc.infonet.Infonet;
import me.shadorc.twitterstalker.Main;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.storage.Data.NumbersEnum;
import me.shadorc.twitterstalker.storage.Data.Options;
import me.shadorc.twitterstalker.storage.Data.UsersEnum;
import me.shadorc.twitterstalker.storage.Data.WordsEnum;
import me.shadorc.twitterstalker.storage.Storage;
import me.shadorc.twitterstalker.utility.Ressources;
import twitter4j.HashtagEntity;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.UserMentionEntity;

public class Stats {

	private TwitterUser user;
	private File archiveFile;
	private JButton bu;

	private long timeFirstTweet;

	private HashMap <WordsEnum, WordsMap> wordStatsMap;
	private HashMap <NumbersEnum, NumberStat> numStatsMap;
	private HashMap <UsersEnum, UsersMap> userStatsMap;

	public Stats(TwitterUser user, JButton bu, boolean analyzeMentions, File archiveFile) throws Exception {
		if(Ressources.stop) return;

		this.user = user;
		this.bu = bu;
		this.archiveFile = archiveFile;

		bu.setText("0%");

		this.wordStatsMap = WordsMap.init();
		this.numStatsMap = NumberStat.init();
		this.userStatsMap = UsersMap.init();

		this.timeFirstTweet = 1;

		if(this.archiveFile != null) {
			this.analyzeArchive();
		} else {
			this.analyzeTweets();
		}

		if(analyzeMentions && user.getName().equals(Main.getTwitter().getScreenName())) {
			this.analyzeMentions();
		}

		//'total' is the time in millisecond since the last tweet. The result is tweets/ms, 8.64*Math.pow(10,7) converts it to tweets/day
		numStatsMap.get(NumbersEnum.TWEETS_PER_DAY).setNum(((float) user.getTweetsAnalyzed()/timeFirstTweet * (float) (8.64*Math.pow(10,7))));
		numStatsMap.get(NumbersEnum.WORDS_PER_TWEET).setNum(numStatsMap.get(NumbersEnum.WORDS_COUNT).getNum()/user.getTweetsAnalyzed());
		numStatsMap.get(NumbersEnum.LETTERS_PER_TWEET).setNum(numStatsMap.get(NumbersEnum.LETTERS).getNum()/user.getTweetsAnalyzed());
		numStatsMap.get(NumbersEnum.LETTERS_PER_WORD).setNum(numStatsMap.get(NumbersEnum.LETTERS).getNum()/numStatsMap.get(NumbersEnum.WORDS_COUNT).getNum());
		numStatsMap.get(NumbersEnum.PURETWEETS_COUNT).setNum(user.getTweetsAnalyzed() - numStatsMap.get(NumbersEnum.MENTIONS_COUNT).getNum() - numStatsMap.get(NumbersEnum.RETWEET_BY_ME).getNum());

		numStatsMap.get(NumbersEnum.PURETWEETS_COUNT).setTotal(user.getTweetsAnalyzed());
		numStatsMap.get(NumbersEnum.MENTIONS_COUNT).setTotal(user.getTweetsAnalyzed());
		numStatsMap.get(NumbersEnum.RETWEET_BY_ME).setTotal(user.getTweetsAnalyzed());
		numStatsMap.get(NumbersEnum.LIKE).setTotal(user.getTweetsAnalyzed());
		numStatsMap.get(NumbersEnum.RETWEET).setTotal(user.getTweetsAnalyzed());

		//Set the number by which they will be divided to provide a percentage
		wordStatsMap.get(WordsEnum.SOURCE).setTotal(user.getTweetsAnalyzed());
		wordStatsMap.get(WordsEnum.DAYS).setTotal(user.getTweetsAnalyzed());
		wordStatsMap.get(WordsEnum.HOURS).setTotal(user.getTweetsAnalyzed());
		wordStatsMap.get(WordsEnum.LANG).setTotal(user.getTweetsAnalyzed());
		wordStatsMap.get(WordsEnum.WORDS).setTotal((int) numStatsMap.get(NumbersEnum.WORDS_COUNT).getNum());
		wordStatsMap.get(WordsEnum.HASHTAG).setTotal((int) numStatsMap.get(NumbersEnum.HASHTAG_COUNT).getNum());

		userStatsMap.get(UsersEnum.MENTIONS_RECEIVED).setTotal(user.getMentionsAnalyzed());
		userStatsMap.get(UsersEnum.MENTIONS_SENT).setTotal((int) numStatsMap.get(NumbersEnum.MENTIONS_COUNT).getNum());

		for(WordsEnum we : wordStatsMap.keySet()) {
			wordStatsMap.get(we).sort();
		}
		for(UsersEnum ue : userStatsMap.keySet()) {
			userStatsMap.get(ue).sort(!ue.equals(UsersEnum.FIRST_TALK));
		}
	}

	private void analyzeArchive() throws TwitterException, JSONException, IOException {
		ArrayList <File> jsonFiles = new ArrayList <File> (Arrays.asList(archiveFile.listFiles()));

		for(File file : jsonFiles) {
			bu.setText(Ressources.format((jsonFiles.indexOf(file)+1.0)*100.0/jsonFiles.size()) + "%");

			String rawJSON = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

			//Remove useless first line to get only JSON
			if(rawJSON.indexOf("[") != -1) {
				rawJSON = rawJSON.substring(rawJSON.indexOf("["));
			}

			//Create an array with all JSON objects in the file
			JSONArray json = new JSONArray(rawJSON);

			//Iterate the whole array of JSON objects
			for(int i = 0; i < json.length(); i++) {
				if(Ressources.stop) return;

				Status status = TwitterObjectFactory.createStatus(json.getJSONObject(i).toString());

				user.incremenAnalyzedTweets();

				//Number of milliseconds since this tweet was posted
				long timeTweet = new Date().getTime() - status.getCreatedAt().getTime();
				if(timeTweet > timeFirstTweet) timeFirstTweet = timeTweet;

				this.setStats(status);
			}
		}
	}

	private void analyzeTweets() throws TwitterException {
		int tweetsToAnalyze = user.getTweetsPosted();
		if(tweetsToAnalyze > OptionsPanel.get(Options.TWEETS_TO_ANALYZE)) {
			tweetsToAnalyze = OptionsPanel.get(Options.TWEETS_TO_ANALYZE);
		}

		int tweetsRequested = 0;
		for(int i = 1; tweetsRequested < tweetsToAnalyze; i++) {

			if(Ressources.showLogs) {
				RateLimitStatus rls = Main.getTwitter().getRateLimitStatus().get("/statuses/user_timeline");
				System.out.println("[User timeline] Remaining requests : " + rls.getRemaining() + "/" + rls.getLimit() + ". Reset in " + (rls.getSecondsUntilReset()/60) + "min " + (rls.getSecondsUntilReset()%60) + "s");
			}

			for(Status status : Main.getTwitter().getUserTimeline(user.getName(), new Paging(i, 200))) {
				if(Ressources.stop) return;

				user.incremenAnalyzedTweets();

				//Number of milliseconds since this tweet was posted
				long timeTweet = new Date().getTime() - status.getCreatedAt().getTime();
				if(timeTweet > timeFirstTweet) timeFirstTweet = timeTweet;

				this.setStats(status);
			}

			tweetsRequested += 200;

			float progress = 100f * tweetsRequested/tweetsToAnalyze;
			if(progress > 100) progress = 100;
			bu.setText(Ressources.format(progress) + "%");
		}
	}

	private void analyzeMentions() throws TwitterException {
		bu.setText(Storage.tra("loadingMentions"));

		for(int i = 1; user.getMentionsAnalyzed() < OptionsPanel.get(Options.MENTIONS_TO_ANALYZE); i++) {

			int secure = user.getMentionsAnalyzed();

			if(Ressources.showLogs) {
				RateLimitStatus rls = Main.getTwitter().getRateLimitStatus().get("/statuses/mentions_timeline");
				System.out.println("[Mentions timeline] Remaining requests : " + rls.getRemaining() + "/" + rls.getLimit() + ". Reset in " + (rls.getSecondsUntilReset()/60) + "min " + (rls.getSecondsUntilReset()%60) + "s");
			}

			try {
				for(Status status : Main.getTwitter().getMentionsTimeline(new Paging(i, 200))) {
					if(Ressources.stop) return;

					user.incremenAnalyzedMentions();
					userStatsMap.get(UsersEnum.MENTIONS_RECEIVED).add(status.getUser().getScreenName());
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

	private void setStats(Status status) throws TwitterException {

		if(status.isRetweet()) {
			numStatsMap.get(NumbersEnum.RETWEET_BY_ME).increment();

		} else {

			for(UserMentionEntity mentionUser : status.getUserMentionEntities()) {
				userStatsMap.get(UsersEnum.MENTIONS_SENT).add(mentionUser.getScreenName());
				userStatsMap.get(UsersEnum.FIRST_TALK).add(mentionUser.getScreenName(), status);
			}
			for(HashtagEntity hashtag : status.getHashtagEntities()) {
				wordStatsMap.get(WordsEnum.HASHTAG).add("#" + hashtag.getText().toLowerCase());
				numStatsMap.get(NumbersEnum.HASHTAG_COUNT).increment();
			}

			if(status.getRetweetCount() + status.getFavoriteCount() > 0)	wordStatsMap.get(WordsEnum.POPULAR).add(status);
			if(status.getRetweetCount() > 0)								numStatsMap.get(NumbersEnum.RETWEET).increment();
			if(status.getFavoriteCount() > 0)								numStatsMap.get(NumbersEnum.LIKE).increment();
			if(status.getUserMentionEntities().length > 0)					numStatsMap.get(NumbersEnum.MENTIONS_COUNT).increment();
			if(status.getMediaEntities().length > 0)						numStatsMap.get(NumbersEnum.MEDIA).increment();
			if(status.getURLEntities().length > 0)							numStatsMap.get(NumbersEnum.URL).increment();

			//Regex removes HTML tag
			wordStatsMap.get(WordsEnum.SOURCE).add(Infonet.removeHtmlTags(status.getSource()));

			//If it's an archive that is analyzed, lang doesn't exist and throws null
			if(status.getLang() != null) {
				//Lang is two-letter iso language code
				String lang = new Locale(status.getLang()).getDisplayLanguage(OptionsPanel.getLocaleLang());
				wordStatsMap.get(WordsEnum.LANG).add(StringUtils.capitalize(lang));
			}

			//Get day and add capitalize
			String day = new SimpleDateFormat("EEEE", OptionsPanel.getLocaleLang()).format(status.getCreatedAt());
			wordStatsMap.get(WordsEnum.DAYS).add(StringUtils.capitalize(day));

			//Gets the hour as 24
			wordStatsMap.get(WordsEnum.HOURS).add(new SimpleDateFormat("H").format(status.getCreatedAt()) + "h");

			//Split spaces and line breaks ("|\\" equals "and")
			for(String word : status.getText().split(" |\\\n")) {

				//Delete all letters except a-z/A-Z/0-9/@/# and accents and lowercase
				word = word.replaceAll("[^a-zA-ZÀ-ÿ0-9^@#]", "").toLowerCase();

				numStatsMap.get(NumbersEnum.WORDS_COUNT).increment();
				numStatsMap.get(NumbersEnum.LETTERS).increment(word.length());

				if(word.length() >= OptionsPanel.get(Options.LETTERS_PER_WORD_MIN) && !word.startsWith("#") && !word.startsWith("@")) {
					wordStatsMap.get(WordsEnum.WORDS).add(word);
				}
			}
		}
	}

	public List<WordStats> get(WordsEnum stat) {
		return wordStatsMap.get(stat).getSorted();
	}

	public List<UserStats> get(UsersEnum stat) {
		return userStatsMap.get(stat).getSorted();
	}

	public NumberStat get(NumbersEnum stat) {
		return numStatsMap.get(stat);
	}
}