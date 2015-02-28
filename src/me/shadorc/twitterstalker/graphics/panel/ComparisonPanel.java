package me.shadorc.twitterstalker.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;

import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.ScrollbarUI;
import me.shadorc.twitterstalker.graphics.SmallButton;
import me.shadorc.twitterstalker.graphics.ScrollbarUI.Position;
import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.TextField.Text;
import me.shadorc.twitterstalker.statistics.PopularePreview;
import me.shadorc.twitterstalker.statistics.Stats;
import me.shadorc.twitterstalker.statistics.TwitterUser;
import twitter4j.TwitterException;

public class ComparisonPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton back, upload;
	private JPanel statsPanel;
	private String name1, name2;
	private Stats stats1, stats2;
	private int statsCase;

	ComparisonPanel(String name1, String name2, JButton button) throws TwitterException {
		super(new BorderLayout());
		this.setBackground(new Color(179, 229, 252));

		this.name1 = name1;
		this.name2 = name2;

		TwitterUser user1, user2;
		try {
			user1 = new TwitterUser(name1);
		} catch (TwitterException e) {
			throw new TwitterException("L'utilisateur n'existe pas.", new Exception("User 1"), 604);
		}

		try {
			user2 = new TwitterUser(name2);
		} catch (TwitterException e) {
			throw new TwitterException("L'utilisateur n'existe pas.", new Exception("User 2"), 604);
		}

		stats1 = new Stats(user1, button);
		stats2 = new Stats(user2, button);

		if(Stats.stop == true) return;

		button.setText("Interface");

		JPanel top = new JPanel(new GridLayout(0, 2));
		top.setOpaque(false);

		top.add(this.createUserJPanel(user1, stats1));
		top.add(this.createUserJPanel(user2, stats2));

		this.add(top, BorderLayout.PAGE_START);

		JPanel center = new JPanel(new GridLayout(0, 2));
		center.setOpaque(false);

		JPanel statsPanel1 = this.createStatsJPanel(user1, stats1);
		JPanel statsPanel2 = this.createStatsJPanel(user2, stats2);

		//All components names in first panel
		ArrayList <String> comp1 = new ArrayList <String> ();
		for(Component comp : statsPanel1.getComponents()) {
			comp1.add(comp.getName());
		}

		//All components names in second panel
		ArrayList <String> comp2 = new ArrayList <String> ();
		for(Component comp : statsPanel2.getComponents()) {
			comp2.add(comp.getName());
		}

		//Check all elements that aren't in the first list but are in the second and remove them
		ArrayList <String> diffComp1 = new ArrayList <String> (comp1);
		diffComp1.removeAll(new ArrayList <String> (comp2));
		for(String name : diffComp1) {
			for(Component comp : statsPanel1.getComponents()) {
				if(comp.getName().equals(name)) {
					statsPanel1.remove(comp);
					System.err.println("Info : " + comp.getName() + " deleted in panel 1.");
				}
			}
		}
		//Decrease rows by the numbers of deleted elements
		((GridLayout) statsPanel1.getLayout()).setRows(((GridLayout) statsPanel1.getLayout()).getRows()-diffComp1.size());

		//Check all elements that aren't in the second list but are in the first and remove them
		ArrayList <String> diffComp2 = new ArrayList <String> (comp2);
		diffComp2.removeAll(new ArrayList <String> (comp1));
		for(String name : diffComp2) {
			for(Component comp : statsPanel2.getComponents()) {
				if(comp.getName().equals(name)) {
					statsPanel2.remove(comp);
					System.err.println("Info : " + comp.getName() + " deleted in panel 2.");
				}
			}
		}
		//Decrease rows by the numbers of deleted elements
		((GridLayout) statsPanel2.getLayout()).setRows(((GridLayout) statsPanel2.getLayout()).getRows()-diffComp2.size());

		center.add(statsPanel1);
		center.add(statsPanel2);

		JScrollPane centerScroll = new JScrollPane(center, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		centerScroll.getVerticalScrollBar().setUnitIncrement(20);
		centerScroll.getViewport().setOpaque(false);
		centerScroll.setOpaque(false);
		centerScroll.setBorder(null);
		centerScroll.getVerticalScrollBar().setUI(new ScrollbarUI(Position.VERTICAL));
		centerScroll.getHorizontalScrollBar().setUI(new ScrollbarUI(Position.HORIZONTAL));

		this.add(centerScroll, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new GridLayout(0, 14));
		buttonsPanel.setOpaque(false);

		back = new SmallButton("Retour", BorderFactory.createEmptyBorder(0, 0, 10, 20));
		back.addActionListener(this);
		buttonsPanel.add(back);

		for(int i = 0; i < 12; i++) {
			buttonsPanel.add(new JLabel());
		}

		upload = new SmallButton("Upload", BorderFactory.createEmptyBorder(0, 20, 10, 0));
		upload.setToolTipText("Partager les statistiques");
		upload.addActionListener(this);
		buttonsPanel.add(upload);

		this.add(buttonsPanel, BorderLayout.PAGE_END);
	}

	private JPanel createUserJPanel(TwitterUser user, Stats stats) {
		JPanel descPanel = new JPanel(new BorderLayout());
		descPanel.setBackground(new Color(68, 138, 255));
		descPanel.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(183,183,183)), BorderFactory.createEmptyBorder(10, 10, 10, 50)));

		descPanel.add(user.getProfileImage(), BorderLayout.WEST);
		JLabel name = new JLabel("@" + user.getName());
		name.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		name.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 40f));
		name.setForeground(Color.WHITE);
		descPanel.add(name, BorderLayout.CENTER);

		JPanel infosPanel = new JPanel(new GridLayout(7, 0));
		infosPanel.setOpaque(false);
		for(int i = 0; i < 2; i++) {
			infosPanel.add(new JLabel());
		}

		infosPanel.add(this.createInfoLabel("Followers : " + user.getFollowersCount()));
		infosPanel.add(this.createInfoLabel("Following : " + user.getFollowingCount()));
		infosPanel.add(this.createInfoLabel("Membre depuis : " + user.getAge() + " jours"));
		infosPanel.add(this.createInfoLabel("Tweets analysés : " + user.getTweetsAnalysed() + "/" + user.getTweetsPosted()));
		infosPanel.add(this.createInfoLabel("Nombre de tweets/jour : " + user.getTweetsPerDay(stats)));
		descPanel.add(infosPanel, BorderLayout.EAST);

		return descPanel;
	}

	private JLabel createInfoLabel(String infos) {
		JLabel tweetsPerDay = new JLabel(infos);
		tweetsPerDay.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 15f));
		tweetsPerDay.setForeground(Color.WHITE);
		return tweetsPerDay;
	}

	private JPanel createStatsJPanel(TwitterUser user, Stats stats) throws TwitterException {
		statsPanel = new JPanel();
		statsPanel.setOpaque(false);

		statsCase = 0;

		if(OptionsPanel.isSelected(Data.TWEETS))	this.createStatField("Tweets", stats, Data.WORDS_PER_TWEET, Data.LETTERS_PER_TWEET, Data.LETTERS_PER_WORD);
		if(OptionsPanel.isSelected(Data.TIMELINE))	this.createStatField("Timeline", stats, Data.PURETWEETS, Data.MENTIONS, Data.RETWEET_BY_ME);
		if(OptionsPanel.isSelected(Data.REPUTE))	this.createStatField("Renommée", stats, Data.FAVORITE, Data.RETWEET);
		if(OptionsPanel.isSelected(Data.SOURCE))	this.createStatField("Sources", stats, Data.SOURCE);
		if(OptionsPanel.isSelected(Data.DAYS))		this.createStatField("Jours", stats, Data.DAYS);
		if(OptionsPanel.isSelected(Data.HOURS))		this.createStatField("Heures", stats, Data.HOURS);
		if(OptionsPanel.isSelected(Data.WORDS))		this.createStatField("Mots", stats, Data.WORDS);
		if(OptionsPanel.isSelected(Data.HASHTAG))	this.createStatField("Hashtags", stats, Data.HASHTAG);
		if(OptionsPanel.isSelected(Data.POPULARE))	this.createStatField("Populaires", stats, Data.POPULARE);
		if(OptionsPanel.isSelected(Data.MENTIONS_SENT))	this.createStatField("Utilisateurs mentionnés", stats, Data.MENTIONS_SENT);

		statsPanel.setLayout(new GridLayout(statsCase, 0, 15, 15));

		return statsPanel;
	}

	private void createStatField(String desc, Stats stats, Data... types) throws TwitterException {
		JEditorPane area = new JEditorPane();
		area.setContentType("text/html");
		area.setOpaque(false);
		area.setEditable(false);
		area.setName(desc);
		/*Allow to set font with HTML content*/
		area.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		area.setFont(Frame.getFont("SEGOEUI.TTF", 26));

		String text = "<font color=#212121>" + desc + "<font color=#727272><style=\"font-size:23\";>";
		//If it's stats contains in array
		if(types.length == 1) {
			Data type = types[0];
			for(int i = 0; i < OptionsPanel.getMaxListLenght(); i++) {
				try {
					text += "<br>&nbsp;&nbsp;";
					if(type.equals(Data.MENTIONS_RECEIVED) || type.equals(Data.MENTIONS_SENT)) {
						text += "- " + stats.get(type, i).getUserInfo();
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
		area.setText(text);

		if(Arrays.asList(types).contains(Data.POPULARE)) {
			area.addHyperlinkListener(new PopularePreview(stats, area));
		}

		statsPanel.add(area);
		statsCase++;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton bu = (JButton) e.getSource();

		if(bu == back) {
			Frame.setJPanel(new ConnectionPanel(Text.COMPARISON));
		} else if(bu == upload) {
			Frame.upload("Comparison between @" + name1 + " & @" + name2);
		}
	}
}