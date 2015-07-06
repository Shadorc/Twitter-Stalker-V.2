package me.shadorc.twitterstalker.graphics;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.statistics.Stats;
import me.shadorc.twitterstalker.statistics.TweetPreview;
import me.shadorc.twitterstalker.statistics.TwitterUser;
import me.shadorc.twitterstalker.statistics.WordInfo;
import twitter4j.TwitterException;

public class EditorPane extends JEditorPane {

	private static final long serialVersionUID = 1L;

	private Stats stats1, stats2;
	private TwitterUser user1, user2;
	private ArrayList <String> phrases;

	public static void get(JPanel pane, Stats stats, String desc, Data... types) throws TwitterException {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setOpaque(false);
		editorPane.setEditable(false);
		/*Allow to set font with HTML content*/
		editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		editorPane.setFont(Frame.getFont("SEGOEUI.TTF", 26));

		String text = "<font color=#212121>" + desc + "<font color=#727272><style=\"font-size:23\";>";
		//If it's stats contains in array
		if(types.length == 1) {
			Data type = types[0];
			for(int i = 0; i < OptionsPanel.getMaxListLenght(); i++) {
				try {
					text += "<br>&nbsp;&nbsp;";
					if(type.equals(Data.MENTIONS_RECEIVED) || type.equals(Data.MENTIONS_SENT)) {
						try {
							text += "- " + stats.get(type, i).getUserInfo();
						} catch(TwitterException e) {
							text += "- " + Storage.tra("nonExistentUser");
						}

					} else if(type.equals(Data.POPULARE)) {
						text += "- " + "<a href=" + stats.get(type, i).getStatusUrl() + ">" + stats.get(type, i).getStatusInfo() + "</a>";

					} else if(type.equals(Data.FIRST_TALK)) {
						ArrayList <WordInfo> copy = new ArrayList <WordInfo> (stats.get(type));
						Collections.reverse(copy);
						text += "- " + copy.get(i).getFirstTalkInfo();

					} else {
						text += "- " + stats.get(type, i).getPercenInfo();
					}
				} catch (IndexOutOfBoundsException e) {
					if(i == 0) {
						System.err.println("Info : " + Arrays.asList(types) + " ignored.");
						return;
					}
					text += "<br>";
				}
			}
		} else {
			for(Data type : types) {
				if(type == Data.WORDS_PER_TWEET || type == Data.LETTERS_PER_TWEET || type ==  Data.LETTERS_PER_WORD) {
					text += "<br>&nbsp;&nbsp;- " + stats.getUnique(type).getInfo();
				} else {
					text += "<br>&nbsp;&nbsp;- " + stats.getUnique(type).getPercenInfo();
				}
			}
		}
		editorPane.setText(text);

		if(types[0].equals(Data.POPULARE)) 		editorPane.addHyperlinkListener(new TweetPreview(editorPane, stats, Data.POPULARE));
		if(types[0].equals(Data.FIRST_TALK))	editorPane.addHyperlinkListener(new TweetPreview(editorPane, stats, Data.FIRST_TALK));

		pane.add(editorPane);
	}

	public EditorPane(Stats stats1, Stats stats2, TwitterUser user1, TwitterUser user2) throws TwitterException {
		super();
		this.setContentType("text/html");
		this.setOpaque(false);
		this.setEditable(false);
		/*Allow to set font with HTML content*/
		this.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		this.setFont(Frame.getFont("SEGOEUI.TTF", 26));

		this.stats1 = stats1;
		this.stats2 = stats2;
		this.user1 = user1;
		this.user2 = user2;

		phrases = new ArrayList <String> ();

		this.compare(Data.TWEET_PER_DAYS, Storage.tra("tweetMore"), false);
		this.compare(Data.RETWEET_BY_ME, Storage.tra("retweetMore"), true);
		this.compare(Data.MENTIONS_COUNT, Storage.tra("moreMentions"), true);
		this.compare(Data.POPULARE, Storage.tra("morePopular"), false);
		this.compare(Data.URL, Storage.tra("moreUrl"), true);
		this.compare(Data.MEDIA, Storage.tra("moreMedias"), true);

		String sunday = new DateFormatSymbols(OptionsPanel.getLocaleLang()).getWeekdays()[1];
		if(stats1.get(Data.SOURCE).size() == 1 && stats1.get(Data.SOURCE).get(0).getWord().equals("Twitter Web Client"))	Storage.tra("onlyTwitterWeb");
		if(stats1.get(Data.HOURS).get(0).getNum() > stats2.get(Data.HOURS).get(0).getNum())									Storage.tra("tweetLater");
		if(stats1.get(Data.DAYS).get(0).getWord().equalsIgnoreCase(sunday))													Storage.tra("sundayTweet");
		if(user1.getFollowingCount()/2 > user1.getFollowersCount())															Storage.tra("followALot");
		if(stats1.getUnique(Data.TWEET_PER_DAYS).getRatio() >= 200)															Storage.tra("tweetALot");
		if(user1.getAge() > 1825) 																							Storage.tra("oldOnTwitter");

		String text = "<font color=#212121>" + user1.getName() + "<font color=#727272><style=\"font-size:23\";><ul>";
		Collections.shuffle(phrases);
		for(int i = 0; i < phrases.size() && i < OptionsPanel.getMaxListLenght(); i++) {
			text += "<li>" + phrases.get(i) + "</li>";
		}
		text += "</lu>";

		this.setText(text);
	}

	private void compare(Data data, String line, boolean proportional) {
		double stat1, stat2;
		try {
			stat1 = stats1.get(data).get(0).getNum();
			stat2 = stats2.get(data).get(0).getNum();
		} catch(NullPointerException e) {
			stat1 = stats1.getUnique(data).getNum();
			stat2 = stats2.getUnique(data).getNum();
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