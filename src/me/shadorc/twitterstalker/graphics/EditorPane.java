package me.shadorc.twitterstalker.graphics;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.statistics.PopularePreview;
import me.shadorc.twitterstalker.statistics.Stats;
import me.shadorc.twitterstalker.statistics.TwitterUser;
import me.shadorc.twitterstalker.statistics.WordInfo;
import twitter4j.TwitterException;

public class EditorPane extends JEditorPane {

	private static final long serialVersionUID = 1L;

	private Stats stats1, stats2;
	private TwitterUser user1, user2;
	private ArrayList <String> phrases;

	public static void get(JPanel pane, Stats stats, String desc, Data... types) throws TwitterException {
		JEditorPane field = new JEditorPane();
		field.setContentType("text/html");
		field.setOpaque(false);
		field.setEditable(false);
		/*Allow to set font with HTML content*/
		field.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		field.setFont(Frame.getFont("SEGOEUI.TTF", 26));

		String text = "<font color=#212121>" + Storage.tra(desc) + "<font color=#727272><style=\"font-size:23\";>";
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
							text += "- " + Storage.tra("Utilisateur inexistant");
						}
					} else if(type.equals(Data.POPULARE)) {
						text += "- " + "<a href=" + stats.get(type, i).getStatusUrl() + ">" + stats.get(type, i).getInfo() + "</a>";
					} else if(type.equals(Data.FIRST_TALK)) {
						ArrayList <WordInfo> copy = new ArrayList <WordInfo> (stats.get(type));
						Collections.reverse(copy);
						String date = DateFormat.getDateInstance(DateFormat.LONG, OptionsPanel.getLocaleLang()).format(new Date(copy.get(i).getCount()));
						text += "- " + copy.get(i).getWord() + " (" + date + ")";
					} else {
						text += "- " + stats.get(type, i).getInfo();
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
				text += "<br>&nbsp;&nbsp;- " + stats.getUnique(type).getDesc();
			}
		}
		field.setText(text);

		if(Arrays.asList(types).contains(Data.POPULARE)) {
			field.addHyperlinkListener(new PopularePreview(field, stats));
		}

		pane.add(field);
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

		this.compare(Data.TWEET_PER_DAYS, "tweete plus", false);
		this.compare(Data.RETWEET_BY_ME, "retweete plus", true);
		this.compare(Data.MENTIONS, "mentionne plus", true);
		this.compare(Data.POPULARE, "est plus populaire", false);
		this.compare(Data.URL, "poste plus d'URL", true);
		this.compare(Data.MEDIA, "poste plus de médias", true);

		//new DateFormatSymbols(OptionsPanel.getLocaleLang()).getWeekdays()[1]) : translate Sunday to user language
		if(stats1.get(Data.DAYS).get(0).getInfo().toLowerCase().contains(new DateFormatSymbols(OptionsPanel.getLocaleLang()).getWeekdays()[1])) {
			phrases.add("confond le jour du seigneur et le jour du flood");
		}
		if(stats1.get(Data.SOURCE).get(0).getInfo().contains("Twitter Web Client")) {
			phrases.add("ne sait pas qu'il existe autre chose que Twitter Web");
		}
		if(stats1.getUnique(Data.TWEET_PER_DAYS).getNum() >= 200) {
			phrases.add("tweete énormément");
		}
		if(Integer.parseInt(stats1.get(Data.HOURS).get(0).getWord().replaceAll("h", "")) > Integer.parseInt(stats2.get(Data.HOURS).get(0).getWord().replaceAll("h",""))) {
			phrases.add("tweete plus tard");
		}

		String text = "<font color=#212121>" + user1.getName() + "<font color=#727272><style=\"font-size:23\";>";
		text += "<ul>";
		for(String s : phrases) {
			text += "<li>" + Storage.tra(s) + "</li>";
		}
		text += "</lu>";

		this.setText(text);
	}

	private void compare(Data data, String line, boolean proportional) {
		double stat1, stat2;
		try {
			stat1 = stats1.get(data).get(0).getCount();
			stat2 = stats2.get(data).get(0).getCount();
		} catch(NullPointerException e) {
			stat1 = stats1.getUnique(data).getNum();
			stat2 = stats2.getUnique(data).getNum();
		}

		if(proportional) {
			stat1 /= user1.getTweetsAnalysed();
			stat2 /= user2.getTweetsAnalysed();
		}
		if(stat1 > stat2) {
			phrases.add(line);
		}
	}
}