package me.shadorc.twitterstalker.graphics.panel;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

import me.shadorc.twitterstalker.graphics.TweetPreview;
import me.shadorc.twitterstalker.statistics.Stats;
import me.shadorc.twitterstalker.statistics.TwitterUser;
import me.shadorc.twitterstalker.storage.Data.Category;
import me.shadorc.twitterstalker.storage.Data.NumbersEnum;
import me.shadorc.twitterstalker.storage.Data.Options;
import me.shadorc.twitterstalker.storage.Data.UsersEnum;
import me.shadorc.twitterstalker.storage.Data.WordsEnum;
import me.shadorc.twitterstalker.storage.Storage;
import me.shadorc.twitterstalker.utility.Ressources;

public class StatsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Stats stats1, stats2;
	private TwitterUser user1, user2;
	private boolean isArchive;

	//Account Stats Panel
	protected StatsPanel(Stats stats, boolean isArchive) {
		super();
		this.setOpaque(false);

		this.stats1 = stats;
		this.isArchive = isArchive;

		this.generate();
	}

	//Comparison Stats Panel
	protected StatsPanel(Stats stats1, Stats stats2, TwitterUser user1, TwitterUser user2) {
		super();
		this.setOpaque(false);

		this.stats1 = stats1;
		this.stats2 = stats2;
		this.user1 = user1;
		this.user2 = user2;
		this.isArchive = false;

		this.generate();
	}

	public void generate() {
		for(Object enumsArray : new Object[] {Category.values(), WordsEnum.values(), UsersEnum.values()}) {
			for(Enum<?> stat : (Enum<?>[]) enumsArray) {

				if(stat == Category.REPUTE && isArchive) continue;
				if(stat == UsersEnum.FIRST_TALK && !isArchive) continue;
				if(stat == Category.SUMMARY && user2 == null) continue;

				if(OptionsPanel.isSelected(stat)) {
					JEditorPane pane = this.createEditorPane(stat, Storage.tra(stat));
					if(pane != null) this.add(pane);
				}
			}
		}
	}

	private JEditorPane createEditorPane(Enum<?> stat, String desc) {
		JEditorPane jep = new JEditorPane();
		jep.setName(desc);
		jep.setContentType("text/html");
		jep.setOpaque(false);
		jep.setEditable(false);
		/*Allow to change font with HTML content*/
		jep.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		jep.setFont(Ressources.getFont("SEGOEUI.TTF", 26));

		String text = "<font color=#212121>" + desc + "<font color=#727272><style=\"font-size:23\";>";

		if(stat instanceof Category) {
			String indent = "<br>&nbsp;&nbsp;- ";

			if(stat == Category.SUMMARY) {
				jep.setText(this.getDesc());
				return jep;
			}

			else if(stat == Category.TWEETS) {
				text += indent + stats1.get(NumbersEnum.WORDS_PER_TWEET);
				text += indent + stats1.get(NumbersEnum.LETTERS_PER_TWEET);
				text += indent + stats1.get(NumbersEnum.LETTERS_PER_WORD);
			}

			else if(stat == Category.TIMELINE) {
				text += indent + stats1.get(NumbersEnum.PURETWEETS_COUNT);
				text += indent + stats1.get(NumbersEnum.MENTIONS_COUNT);
				text += indent + stats1.get(NumbersEnum.RETWEET_BY_ME);
			}

			else if(stat == Category.REPUTE) {
				text += indent + stats1.get(NumbersEnum.LIKE);
				text += indent + stats1.get(NumbersEnum.RETWEET);
			}

		} else {
			List<?> list = null;
			if(stat.getClass().equals(WordsEnum.class))			list = stats1.get((WordsEnum) stat);
			else if(stat.getClass().equals(UsersEnum.class))	list = stats1.get((UsersEnum) stat);

			if(list == null || list.isEmpty()) return null;

			for(int i = 0; i < OptionsPanel.get(Options.LIST_LENGHT); i++) {
				text += "<br>";
				if(i < list.size()) {
					text += "&nbsp;&nbsp;- " + list.get(i).toString();
				}
			}

			if(stat.equals(WordsEnum.POPULAR) || stat.equals(UsersEnum.FIRST_TALK)) {
				jep.addHyperlinkListener(new TweetPreview(jep, stats1));
			}
		}

		jep.setText(text);

		return jep;
	}

	private String getDesc() {
		ArrayList <String> phrases = new ArrayList <String> ();

		if(stats1.get(NumbersEnum.TWEETS_PER_DAY).getNum() > stats2.get(NumbersEnum.TWEETS_PER_DAY).getNum())														phrases.add(Storage.tra("tweetMore"));
		if(stats1.get(NumbersEnum.RETWEET_BY_ME).getNum()/user1.getTweetsAnalyzed() > stats2.get(NumbersEnum.RETWEET_BY_ME).getNum()/user2.getTweetsAnalyzed()) 	phrases.add(Storage.tra("retweetMore"));
		if(stats1.get(NumbersEnum.MENTIONS_COUNT).getNum()/user1.getTweetsAnalyzed() > stats2.get(NumbersEnum.MENTIONS_COUNT).getNum()/user2.getTweetsAnalyzed()) 	phrases.add(Storage.tra("moreMentions"));
		if(stats1.get(NumbersEnum.URL).getNum()/user1.getTweetsAnalyzed() > stats2.get(NumbersEnum.URL).getNum()/user2.getTweetsAnalyzed()) 						phrases.add(Storage.tra("moreUrl"));
		if(stats1.get(NumbersEnum.MEDIA).getNum()/user1.getTweetsAnalyzed() > stats2.get(NumbersEnum.MEDIA).getNum()/user2.getTweetsAnalyzed()) 					phrases.add(Storage.tra("moreMedias"));

		String sunday = new DateFormatSymbols(OptionsPanel.getLocaleLang()).getWeekdays()[1];
		if(stats1.get(WordsEnum.DAYS).get(0).getObject().toString().equalsIgnoreCase(sunday))											phrases.add(Storage.tra("sundayTweet"));
		if(stats1.get(WordsEnum.SOURCE).size() == 1 && stats1.get(WordsEnum.SOURCE).get(0).getObject().equals("Twitter Web Client"))	phrases.add(Storage.tra("onlyTwitterWeb"));
		if(stats1.get(WordsEnum.HOURS).get(0).getNum() > stats2.get(WordsEnum.HOURS).get(0).getNum())									phrases.add(Storage.tra("tweetLater"));
		if(stats1.get(NumbersEnum.TWEETS_PER_DAY).getNum() >= 200)																		phrases.add(Storage.tra("tweetALot"));

		if(user1.getTwitterMoney(stats1) > user2.getTwitterMoney(stats2))	 															phrases.add(Storage.tra("morePopular"));
		if(user1.getFollowingCount()/2 > user1.getFollowersCount())																		phrases.add(Storage.tra("followALot"));
		if(user1.getAge() > 1825) 																										phrases.add(Storage.tra("oldOnTwitter"));

		Collections.shuffle(phrases);

		String text = "<font color=#212121>" + user1.getName() + "<font color=#727272><style=\"font-size:23\";><ul>";
		for(int i = 0; i < phrases.size() && i < OptionsPanel.get(Options.LIST_LENGHT); i++) {
			text += "<li>" + phrases.get(i) + "</li>";
		}
		text += "</lu>";

		return text;
	}
}