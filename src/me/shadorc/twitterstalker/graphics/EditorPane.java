package me.shadorc.twitterstalker.graphics;

import java.util.Arrays;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.statistics.PopularePreview;
import me.shadorc.twitterstalker.statistics.Stats;
import twitter4j.TwitterException;

public class EditorPane extends JEditorPane {

	private static final long serialVersionUID = 1L;

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

	public EditorPane(Stats stats1, Stats stats2, String name) throws TwitterException {
		super();
		this.setContentType("text/html");
		this.setOpaque(false);
		this.setEditable(false);
		/*Allow to set font with HTML content*/
		this.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		this.setFont(Frame.getFont("SEGOEUI.TTF", 26));

		String text = "<font color=#212121>" + name + "<font color=#727272><style=\"font-size:23\";>";
		text += "<ul>";

		if(isSuperior(stats1, stats2, Data.TWEET_PER_DAYS)) {
			text += "<li> " + Storage.tra("tweete plus") + "</li>";
		}
		if(isSuperior(stats1, stats2, Data.POPULARE)) {
			text += "<li> " + Storage.tra("est plus populaire") + "</li>";
		}
		if(isSuperior(stats1, stats2, Data.HOURS)) {
			text += "<li> " + Storage.tra("tweete plus tard") + "</li>";
		}
		if(isSuperior(stats1, stats2, Data.RETWEET_BY_ME)) {
			text += "<li> " + Storage.tra("retweete plus") + "</li>";
		}
		if(isSuperior(stats1, stats2, Data.MENTIONS)) {
			text += "<li> " + Storage.tra("mentionne plus") + "</li>";
		}
		if(stats1.get(Data.DAYS).get(0).getInfo().contains("Dimanche")) {
			text += "<li>" + Storage.tra("confond le jour du seigneur et le jour du flood") + "</li>";
		}
		if(stats1.get(Data.SOURCE).get(0).getInfo().contains("Twitter Web Client")) {
			text += "<li>" + Storage.tra("ne sait pas qu'il existe autre chose que Twitter Web") + "</li>";
		}
		if(stats1.getUnique(Data.TWEET_PER_DAYS).getNum() >= 200) {
			text += "<li>" + Storage.tra("tweete énormément") + "</li>";
		}

		text += "</lu>";

		this.setText(text);
	}

	private boolean isSuperior(Stats stats1, Stats stats2, Data data) {
		try {
			return stats1.get(data).get(0).getCount() > stats2.get(data).get(0).getCount();
		} catch(NullPointerException e) {
			return stats1.getUnique(data).getNum() > stats2.getUnique(data).getNum();
		}
	}
}