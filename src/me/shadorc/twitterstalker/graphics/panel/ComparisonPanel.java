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
import me.shadorc.twitterstalker.graphics.Ressources;
import me.shadorc.twitterstalker.graphics.ScrollbarUI;
import me.shadorc.twitterstalker.graphics.ScrollbarUI.Position;
import me.shadorc.twitterstalker.graphics.TextField.Text;
import me.shadorc.twitterstalker.statistics.Stats;
import me.shadorc.twitterstalker.statistics.TwitterUser;
import me.shadorc.twitterstalker.storage.Data;
import me.shadorc.twitterstalker.storage.Storage;
import twitter4j.TwitterException;

public class ComparisonPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton back, upload;
	private JPanel statsPanel;
	private TwitterUser user1, user2;
	private Stats stats1, stats2;
	private ArrayList <String> ignored;

	ComparisonPanel(String name1, String name2, JButton button) throws TwitterException {
		super(new BorderLayout());

		user1 = new TwitterUser(name1);
		user2 = new TwitterUser(name2);

		stats1 = new Stats(user1, button, null);
		stats2 = new Stats(user2, button, null);

		if(Stats.stop == true) return;

		button.setText(Storage.tra("interface"));

		this.setBackground(new Color(179, 229, 252));

		JPanel top = new JPanel(new GridLayout(0, 2));
		top.setOpaque(false);

		top.add(this.createUserJPanel(user1, stats1));
		top.add(this.createUserJPanel(user2, stats2));

		this.add(top, BorderLayout.PAGE_START);

		JPanel center = new JPanel(new GridLayout(0, 2));
		center.setOpaque(false);

		ignored = new ArrayList <String> ();

		JPanel statsPanel1 = this.createStatsJPanel(user1, stats1);
		JPanel statsPanel2 = this.createStatsJPanel(user2, stats2);

		for(Component comp : statsPanel1.getComponents()) {
			if(ignored.contains(comp.getName())) {
				statsPanel1.remove(comp);
				((GridLayout) statsPanel1.getLayout()).setRows(((GridLayout) statsPanel1.getLayout()).getRows()-1);
				System.out.println("'" + comp.getName() + "' ignored in panel 1");
			}
		}

		for(Component comp : statsPanel2.getComponents()) {
			if(ignored.contains(comp.getName())) {
				statsPanel2.remove(comp);
				((GridLayout) statsPanel2.getLayout()).setRows(((GridLayout) statsPanel2.getLayout()).getRows()-1);
				System.out.println(comp.getName() + " ignored in panel 2.");
			}
		}

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
		upload.setToolTipText(Storage.tra("shareStat"));
		buttonsPanel.add(upload);

		this.add(buttonsPanel, BorderLayout.PAGE_END);
	}

	private JPanel createUserJPanel(TwitterUser user, Stats stats) {
		JPanel descPanel = new JPanel(new BorderLayout());
		descPanel.setBackground(new Color(68, 138, 255));
		descPanel.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(183,183,183)), BorderFactory.createEmptyBorder(5, 10, 5, 30)));

		descPanel.add(user.getProfileImage(), BorderLayout.WEST);
		JLabel name = new JLabel("@" + user.getName());
		name.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		name.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 40));
		name.setForeground(Color.WHITE);

		//Reduce name's size according to the difference in width
		if(name.getPreferredSize().getWidth() > 200) {
			name.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", (int) (40-(name.getPreferredSize().getWidth()-200)/10)));
		}

		descPanel.add(name, BorderLayout.CENTER);

		JPanel infosPanel = new JPanel(new GridLayout(7, 0));
		infosPanel.setOpaque(false);
		infosPanel.add(new JLabel());
		infosPanel.add(this.createInfoLabel(Storage.tra("follower") + user.getFollowersCount()));
		infosPanel.add(this.createInfoLabel(Storage.tra("following") + user.getFollowingCount()));
		infosPanel.add(this.createInfoLabel(Storage.tra("memberSince") + user.getAge() + Storage.tra("days")));
		infosPanel.add(this.createInfoLabel(Storage.tra("tweetsAnalyzed") + user.getTweetsAnalyzed() + "/" + user.getTweetsPosted()));
		infosPanel.add(this.createInfoLabel(user.getTweetsPerDay(stats)));
		descPanel.add(infosPanel, BorderLayout.EAST);

		return descPanel;
	}

	private JLabel createInfoLabel(String infos) {
		JLabel tweetsPerDay = new JLabel(infos, JLabel.RIGHT);
		tweetsPerDay.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 18));
		tweetsPerDay.setForeground(Color.WHITE);
		return tweetsPerDay;
	}

	private JPanel createStatsJPanel(TwitterUser user, Stats stats) {
		statsPanel = new JPanel();
		statsPanel.setOpaque(false);

		if(stats == stats1) {
			statsPanel.add(new EditorPane(stats1, stats2, user1, user2));
		} else {
			statsPanel.add(new EditorPane(stats2, stats1, user2, user1));
		}

		if(OptionsPanel.isSelected(Data.TWEETS)   
				&& !EditorPane.get(statsPanel, stats, Storage.tra("tweetsStat"), Data.WORDS_PER_TWEET, Data.LETTERS_PER_TWEET, Data.LETTERS_PER_WORD)) 
			ignored.add(Storage.tra("tweetsStat"));

		if(OptionsPanel.isSelected(Data.TIMELINE) 
				&& !EditorPane.get(statsPanel, stats, Storage.tra("timelineStat"), Data.PURETWEETS_COUNT, Data.MENTIONS_COUNT, Data.RETWEET_BY_ME)) 
			ignored.add(Storage.tra("timelineStat"));

		if(OptionsPanel.isSelected(Data.REPUTE)   
				&& !EditorPane.get(statsPanel, stats, Storage.tra("reputeStat"), Data.FAVORITE, Data.RETWEET)) 
			ignored.add(Storage.tra("reputeStat"));

		if(OptionsPanel.isSelected(Data.SOURCE)   
				&& !EditorPane.get(statsPanel, stats, Storage.tra("sourceStat"), Data.SOURCE)) 
			ignored.add(Storage.tra("sourceStat"));

		if(OptionsPanel.isSelected(Data.DAYS)     
				&& !EditorPane.get(statsPanel, stats, Storage.tra("daysStat"), Data.DAYS))
			ignored.add(Storage.tra("daysStat"));

		if(OptionsPanel.isSelected(Data.HOURS)    
				&& !EditorPane.get(statsPanel, stats, Storage.tra("hoursStat"), Data.HOURS)) 
			ignored.add(Storage.tra("hoursStat"));

		if(OptionsPanel.isSelected(Data.WORDS)    
				&& !EditorPane.get(statsPanel, stats, Storage.tra("wordsStat"), Data.WORDS))
			ignored.add(Storage.tra("wordsStat"));

		if(OptionsPanel.isSelected(Data.HASHTAG)  
				&& !EditorPane.get(statsPanel, stats, Storage.tra("hashtagStat"), Data.HASHTAG))
			ignored.add(Storage.tra("hashtagStat"));

		if(OptionsPanel.isSelected(Data.POPULARE) 
				&& !EditorPane.get(statsPanel, stats, Storage.tra("popularStat"), Data.POPULARE)) 
			ignored.add(Storage.tra("popularStat"));

		if(OptionsPanel.isSelected(Data.LANG)     
				&& !EditorPane.get(statsPanel, stats, Storage.tra("languageStat"), Data.LANG))
			ignored.add(Storage.tra("languageStat"));

		if(OptionsPanel.isSelected(Data.MENTIONS_SENT) 
				&& !EditorPane.get(statsPanel, stats, Storage.tra("mentionsSent"), Data.MENTIONS_SENT)) 
			ignored.add(Storage.tra("mentionsSent"));

		if(statsPanel.getComponents().length == 0) {
			statsPanel.setLayout(new BorderLayout());
			JLabel error = new JLabel(Storage.tra("noStatError"), JLabel.CENTER);
			error.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 30));
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