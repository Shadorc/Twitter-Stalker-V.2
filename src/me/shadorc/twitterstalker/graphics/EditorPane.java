package me.shadorc.twitterstalker.graphics;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.statistics.Stats;
import me.shadorc.twitterstalker.statistics.TweetPreview;
import me.shadorc.twitterstalker.statistics.TwitterUser;
import me.shadorc.twitterstalker.statistics.WordInfo;
import me.shadorc.twitterstalker.storage.Data.Statistics;
import me.shadorc.twitterstalker.storage.Storage;
import twitter4j.TwitterException;

public class EditorPane extends JEditorPane {

	private static final long serialVersionUID = 1L;

	private Stats stats1, stats2;
	private TwitterUser user1, user2;
	private ArrayList <String> phrases;

	public static boolean get(JPanel pane, Stats stats, String desc, Statistics... types) {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setName(desc);
		editorPane.setContentType("text/html");
		editorPane.setOpaque(false);
		editorPane.setEditable(false);
		/*Allow to set font with HTML content*/
		editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		editorPane.setFont(Ressources.getFont("SEGOEUI.TTF", 26));

		String text = "<font color=#212121>" + desc + "<font color=#727272><style=\"font-size:23\";>";
		//If it's stats contains in array
		if(types.length == 1) {
			Statistics type = types[0];

			//No stat to show
			if(stats.get(type).isEmpty()) {
				return false;
			}

			for(int i = 0; i < OptionsPanel.getMaxListLenght(); i++) {

				//Index out of bound
				if(stats.get(type).size() <= i) {
					text += "<br>";
					continue;
				}

				text += "<br>&nbsp;&nbsp;- ";
				if(type.equals(Statistics.MENTIONS_RECEIVED) || type.equals(Statistics.MENTIONS_SENT)) {
					try {
						text += stats.get(type, i).getUserInfo();
					} catch(TwitterException e) {
						text += Storage.tra("nonExistentUser");
					}

				} else if(type.equals(Statistics.POPULARE)) {
					text += "<a href=" + stats.get(type, i).getStatusUrl() + ">" + stats.get(type, i).getStatusInfo() + "</a>";

				} else if(type.equals(Statistics.FIRST_TALK)) {
					ArrayList <WordInfo> copy = new ArrayList <WordInfo> (stats.get(type));
					Collections.reverse(copy);
					text += copy.get(i).getFirstTalkInfo();

				} else {
					text += stats.get(type, i).getPercenInfo();
				}
			}
		} else {
			for(Statistics type : types) {
				text += "<br>&nbsp;&nbsp;- ";
				if(type == Statistics.WORDS_PER_TWEET || type == Statistics.LETTERS_PER_TWEET || type ==  Statistics.LETTERS_PER_WORD) {
					text += stats.getUnique(type).getInfo();
				} else {
					text += stats.getUnique(type).getPercenInfo();
				}
			}
		}
		editorPane.setText(text);

		//Add Hyperlink listener to pane if needed
		if(types[0].equals(Statistics.POPULARE)) 	editorPane.addHyperlinkListener(new TweetPreview(editorPane, stats, Statistics.POPULARE));
		if(types[0].equals(Statistics.FIRST_TALK))	editorPane.addHyperlinkListener(new TweetPreview(editorPane, stats, Statistics.FIRST_TALK));

		pane.add(editorPane);

		return true;
	}

	public EditorPane(Stats stats1, Stats stats2, TwitterUser user1, TwitterUser user2) {
		super();
		this.setContentType("text/html");
		this.setOpaque(false);
		this.setEditable(false);
		/*Allow to set font with HTML content*/
		this.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		this.setFont(Ressources.getFont("SEGOEUI.TTF", 26));

		this.stats1 = stats1;
		this.stats2 = stats2;
		this.user1 = user1;
		this.user2 = user2;

		phrases = new ArrayList <String> ();

		this.compare(Statistics.TWEETS_PER_DAY, Storage.tra("tweetMore"), false);
		this.compare(Statistics.RETWEET_BY_ME, Storage.tra("retweetMore"), true);
		this.compare(Statistics.MENTIONS_COUNT, Storage.tra("moreMentions"), true);
		this.compare(Statistics.POPULARE, Storage.tra("morePopular"), false);
		this.compare(Statistics.URL, Storage.tra("moreUrl"), true);
		this.compare(Statistics.MEDIA, Storage.tra("moreMedias"), true);

		String sunday = new DateFormatSymbols(OptionsPanel.getLocaleLang()).getWeekdays()[1];
		if(stats1.get(Statistics.SOURCE).size() == 1 && stats1.get(Statistics.SOURCE).get(0).getWord().equals("Twitter Web Client"))	Storage.tra("onlyTwitterWeb");
		if(stats1.get(Statistics.HOURS).get(0).getNum() > stats2.get(Statistics.HOURS).get(0).getNum())									Storage.tra("tweetLater");
		if(stats1.get(Statistics.DAYS).get(0).getWord().equalsIgnoreCase(sunday))													Storage.tra("sundayTweet");
		if(user1.getFollowingCount()/2 > user1.getFollowersCount())															Storage.tra("followALot");
		if(stats1.getUnique(Statistics.TWEETS_PER_DAY).getRatio() >= 200)															Storage.tra("tweetALot");
		if(user1.getAge() > 1825) 																							Storage.tra("oldOnTwitter");

		String text = "<font color=#212121>" + user1.getName() + "<font color=#727272><style=\"font-size:23\";><ul>";
		Collections.shuffle(phrases);
		for(int i = 0; i < phrases.size() && i < OptionsPanel.getMaxListLenght(); i++) {
			text += "<li>" + phrases.get(i) + "</li>";
		}
		text += "</lu>";

		this.setText(text);
	}

	private void compare(Statistics data, String line, boolean proportional) {
		double stat1, stat2;

		if(data == Statistics.TWEETS_PER_DAY) {
			stat1 = stats1.getUnique(Statistics.TWEETS_PER_DAY).getRatio();
			stat2 = stats2.getUnique(Statistics.TWEETS_PER_DAY).getRatio();

		}else if(data == Statistics.POPULARE) {
			stat1 = user1.getTwitterMoney(stats1);
			stat2 = user2.getTwitterMoney(stats2);

		} else {
			try {
				stat1 = stats1.get(data).get(0).getNum();
				stat2 = stats2.get(data).get(0).getNum();
			} catch(NullPointerException e) {
				stat1 = stats1.getUnique(data).getNum();
				stat2 = stats2.getUnique(data).getNum();
			}
		}

		if(proportional) {
			stat1 /= user1.getTweetsAnalyzed();
			stat2 /= user2.getTweetsAnalyzed();
		}

		if(stat1 > stat2) {
			phrases.add(line);
		}
	}
}