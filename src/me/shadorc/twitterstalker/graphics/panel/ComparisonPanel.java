package me.shadorc.twitterstalker.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;

import me.shadorc.twitterstalker.graphics.Button;
import me.shadorc.twitterstalker.graphics.Button.Size;
import me.shadorc.twitterstalker.graphics.EditorPane;
import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.ScrollbarUI;
import me.shadorc.twitterstalker.graphics.ScrollbarUI.Position;
import me.shadorc.twitterstalker.graphics.Storage;
import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.TextField.Text;
import me.shadorc.twitterstalker.statistics.Stats;
import me.shadorc.twitterstalker.statistics.TwitterUser;
import twitter4j.TwitterException;

public class ComparisonPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton back, upload;
	private JPanel statsPanel;
	private TwitterUser user1, user2;
	private Stats stats1, stats2;

	ComparisonPanel(String name1, String name2, JButton button) throws TwitterException {
		super(new BorderLayout());

		try {
			user1 = new TwitterUser(name1);
		} catch (TwitterException e) {
			throw new TwitterException(Storage.tra("L'utilisateur n'existe pas."), new Exception("User 1"), 604);
		}

		try {
			user2 = new TwitterUser(name2);
		} catch (TwitterException e) {
			throw new TwitterException(Storage.tra("L'utilisateur n'existe pas."), new Exception("User 2"), 604);
		}

		stats1 = new Stats(user1, button, null);
		stats2 = new Stats(user2, button, null);

		if(Stats.stop == true) return;

		button.setText(Storage.tra("Interface"));

		this.setBackground(new Color(179, 229, 252));

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

		back = new Button("Retour", new int[] {0, 0, 10, 20}, Size.MEDIUM, this);
		buttonsPanel.add(back);

		for(int i = 0; i < 12; i++) {
			buttonsPanel.add(new JLabel());
		}

		upload = new Button("Upload", new int[] {0, 20, 10, 0}, Size.MEDIUM, this);
		upload.setToolTipText(Storage.tra("Partager les statistiques"));
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
		name.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 40));
		name.setForeground(Color.WHITE);
		descPanel.add(name, BorderLayout.CENTER);

		JPanel infosPanel = new JPanel(new GridLayout(7, 0));
		infosPanel.setOpaque(false);
		for(int i = 0; i < 2; i++) {
			infosPanel.add(new JLabel());
		}

		infosPanel.add(this.createInfoLabel(Storage.tra("Abonnées : ") + user.getFollowersCount()));
		infosPanel.add(this.createInfoLabel(Storage.tra("Abonnements : ") + user.getFollowingCount()));
		infosPanel.add(this.createInfoLabel(Storage.tra("Membre depuis : ") + user.getAge() + Storage.tra(" jours")));
		infosPanel.add(this.createInfoLabel(Storage.tra("Tweets analysés : ") + user.getTweetsAnalyzed() + "/" + user.getTweetsPosted()));
		infosPanel.add(this.createInfoLabel(user.getTweetsPerDay(stats)));
		descPanel.add(infosPanel, BorderLayout.EAST);

		return descPanel;
	}

	private JLabel createInfoLabel(String infos) {
		JLabel tweetsPerDay = new JLabel(infos);
		tweetsPerDay.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 16));
		tweetsPerDay.setForeground(Color.WHITE);
		return tweetsPerDay;
	}

	private JPanel createStatsJPanel(TwitterUser user, Stats stats) throws TwitterException {
		statsPanel = new JPanel();
		statsPanel.setOpaque(false);

		if(stats == stats1) {
			statsPanel.add(new EditorPane(stats1, stats2, user1, user2));
		} else {
			statsPanel.add(new EditorPane(stats2, stats1, user2, user1));
		}

		if(OptionsPanel.isSelected(Data.TWEETS))	EditorPane.get(statsPanel, stats, "Tweets", Data.WORDS_PER_TWEET, Data.LETTERS_PER_TWEET, Data.LETTERS_PER_WORD);
		if(OptionsPanel.isSelected(Data.TIMELINE))	EditorPane.get(statsPanel, stats, "Timeline", Data.PURETWEETS_COUNT, Data.MENTIONS_COUNT, Data.RETWEET_BY_ME);
		if(OptionsPanel.isSelected(Data.REPUTE))	EditorPane.get(statsPanel, stats, "Renommée", Data.FAVORITE, Data.RETWEET);
		if(OptionsPanel.isSelected(Data.SOURCE))	EditorPane.get(statsPanel, stats, "Sources", Data.SOURCE);
		if(OptionsPanel.isSelected(Data.DAYS))		EditorPane.get(statsPanel, stats, "Jours", Data.DAYS);
		if(OptionsPanel.isSelected(Data.HOURS))		EditorPane.get(statsPanel, stats, "Heures", Data.HOURS);
		if(OptionsPanel.isSelected(Data.WORDS))		EditorPane.get(statsPanel, stats, "Mots", Data.WORDS);
		if(OptionsPanel.isSelected(Data.HASHTAG))	EditorPane.get(statsPanel, stats, "Hashtags", Data.HASHTAG);
		if(OptionsPanel.isSelected(Data.POPULARE))	EditorPane.get(statsPanel, stats, "Populaires", Data.POPULARE);
		if(OptionsPanel.isSelected(Data.LANG))		EditorPane.get(statsPanel, stats, "Langues", Data.LANG);
		if(OptionsPanel.isSelected(Data.MENTIONS_SENT))	EditorPane.get(statsPanel, stats, "Mentions envoyées", Data.MENTIONS_SENT);

		if(statsPanel.getComponents().length == 0) {
			statsPanel.setLayout(new BorderLayout());
			JLabel error = new JLabel(Storage.tra("Aucune statistique n'a été sélectionnée. Désolé, mais le bug est dans un autre château."), JLabel.CENTER);
			error.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 30));
			statsPanel.add(error, JLabel.CENTER);
		} else {
			statsPanel.setLayout(new GridLayout(statsPanel.getComponents().length, 0, 15, 15));
		}

		return statsPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton bu = (JButton) e.getSource();

		if(bu == back) {
			Frame.setPanel(new ConnectionPanel(Storage.tra(Text.COMPARISON)));
		} else if(bu == upload) {
			Frame.upload("Comparison between @" + user1.getName() + " & @" + user2.getName());
		}
	}
}