package me.shadorc.twitterstalker.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;

import me.shadorc.twitterstalker.graphics.Button;
import me.shadorc.twitterstalker.graphics.Button.ButtonType;
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
import twitter4j.Status;
import twitter4j.TwitterException;

public class StatisticsPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton back, upload;
	private JPanel textPanel;
	private TwitterUser user;
	private Stats stats;
	private boolean isArchive;

	StatisticsPanel(String name, JButton button, List <Status> statusList) throws TwitterException {
		super(new BorderLayout());

		user = new TwitterUser(name);

		isArchive = (statusList != null);

		stats = new Stats(user, button, statusList);

		if(Stats.stop == true) return;

		button.setText(Storage.tra("interface"));

		this.setBackground(new Color(179, 229, 252));

		JPanel top = new JPanel(new BorderLayout());
		top.setBackground(new Color(68,138,255));
		top.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(183,183,183)), BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		JPanel userInfos = new JPanel(new BorderLayout());
		userInfos.setOpaque(false);

		Font font = Ressources.getFont("RobotoCondensed-Regular.ttf", 40);

		JPanel labelsPane = new JPanel(new GridLayout(2, 0));
		labelsPane.setOpaque(false);

		JLabel followers = new JLabel(Storage.tra("follower") + user.getFollowersCount());
		followers.setFont(font);
		followers.setForeground(Color.WHITE);
		labelsPane.add(followers);

		JLabel followings = new JLabel(Storage.tra("following") + user.getFollowingCount());
		followings.setFont(font);
		followings.setForeground(Color.WHITE);
		labelsPane.add(followings);

		userInfos.add(labelsPane, BorderLayout.WEST);

		JLabel image = user.getProfileImage();
		image.setHorizontalAlignment(JLabel.CENTER);
		userInfos.add(image, BorderLayout.CENTER);

		font = font.deriveFont(72f);

		JLabel username = new JLabel("@" + user.getName(), JLabel.RIGHT);
		username.setForeground(Color.WHITE);
		username.setFont(font);
		userInfos.add(username, BorderLayout.EAST);

		//Set the same width to the two panel to have the user image centered. Condition is to avoid to have truncated text
		if(labelsPane.getPreferredSize().getWidth() > username.getPreferredSize().getWidth()) {
			username.setPreferredSize(labelsPane.getPreferredSize());
		} else {
			labelsPane.setPreferredSize(username.getPreferredSize());
		}

		top.add(userInfos, BorderLayout.CENTER);

		font = font.deriveFont(16f);

		JPanel userInfosStats = new JPanel(new GridLayout(0, 5));
		userInfosStats.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		userInfosStats.setOpaque(false);

		userInfosStats.add(new JLabel());
		userInfosStats.add(new JLabel());

		JLabel tweets = new JLabel(Storage.tra("tweetsAnalyzed") + user.getTweetsAnalyzed() + "/" + user.getTweetsPosted());
		tweets.setForeground(Color.WHITE);
		tweets.setFont(font);
		userInfosStats.add(tweets);

		JLabel tweetsDays = new JLabel(user.getTweetsPerDay(stats));
		tweetsDays.setForeground(Color.WHITE);
		tweetsDays.setFont(font);
		userInfosStats.add(tweetsDays);

		JLabel age = new JLabel(Storage.tra("joined") + user.getCreatedAt() + " (" + user.getAge() + Storage.tra("days") + ")");
		age.setForeground(Color.WHITE);
		age.setFont(font);
		userInfosStats.add(age);

		top.add(userInfosStats, BorderLayout.PAGE_END);

		this.add(top, BorderLayout.PAGE_START);

		textPanel = new JPanel();
		textPanel.setOpaque(false);

		JScrollPane jsp = new JScrollPane(textPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.getVerticalScrollBar().setUnitIncrement(16);
		jsp.getViewport().setOpaque(false);
		jsp.setOpaque(false);
		jsp.setBorder(null);
		jsp.getVerticalScrollBar().setUI(new ScrollbarUI(Position.VERTICAL));
		jsp.getHorizontalScrollBar().setUI(new ScrollbarUI(Position.HORIZONTAL));

		if(OptionsPanel.isSelected(Data.TWEETS))					EditorPane.get(textPanel, stats, Storage.tra("tweetsStat"), Data.WORDS_PER_TWEET, Data.LETTERS_PER_TWEET, Data.LETTERS_PER_WORD);
		if(OptionsPanel.isSelected(Data.TIMELINE))					EditorPane.get(textPanel, stats, Storage.tra("timelineStat"), Data.PURETWEETS_COUNT, Data.MENTIONS_COUNT, Data.RETWEET_BY_ME);
		if(OptionsPanel.isSelected(Data.REPUTE) && !isArchive)		EditorPane.get(textPanel, stats, Storage.tra("reputeStat"), Data.FAVORITE, Data.RETWEET);
		if(OptionsPanel.isSelected(Data.SOURCE))					EditorPane.get(textPanel, stats, Storage.tra("sourceStat"), Data.SOURCE);
		if(OptionsPanel.isSelected(Data.DAYS))						EditorPane.get(textPanel, stats, Storage.tra("daysStat"), Data.DAYS);
		if(OptionsPanel.isSelected(Data.HOURS))						EditorPane.get(textPanel, stats, Storage.tra("hoursStat"), Data.HOURS);
		if(OptionsPanel.isSelected(Data.WORDS))						EditorPane.get(textPanel, stats, Storage.tra("wordsStat"), Data.WORDS);
		if(OptionsPanel.isSelected(Data.HASHTAG))					EditorPane.get(textPanel, stats, Storage.tra("hashtagStat"), Data.HASHTAG);
		if(OptionsPanel.isSelected(Data.POPULARE) && !isArchive)	EditorPane.get(textPanel, stats, Storage.tra("popularStat"), Data.POPULARE);
		if(OptionsPanel.isSelected(Data.LANG) && !isArchive)		EditorPane.get(textPanel, stats, Storage.tra("languageStat"), Data.LANG);
		if(OptionsPanel.isSelected(Data.MENTIONS_SENT))				EditorPane.get(textPanel, stats, Storage.tra("mentionsSent"), Data.MENTIONS_SENT);
		if(OptionsPanel.isSelected(Data.MENTIONS_RECEIVED))			EditorPane.get(textPanel, stats, Storage.tra("mentionsReceived"), Data.MENTIONS_RECEIVED);
		if(isArchive) 												EditorPane.get(textPanel, stats, Storage.tra("oldMentionsStats") + " (<a href=search>" + Storage.tra("search") + "</a>)", Data.FIRST_TALK);

		if(textPanel.getComponents().length == 0) {
			textPanel.setLayout(new BorderLayout());
			JLabel error = new JLabel(Storage.tra("noStatError"), JLabel.CENTER);
			error.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 30));
			textPanel.add(error, JLabel.CENTER);
		} else {
			textPanel.setLayout(new GridLayout((int) Math.ceil(textPanel.getComponents().length/3.0), 3, 15, 15));
		}

		this.add(jsp, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new GridLayout(0, 14));
		buttonsPanel.setOpaque(false);

		back = new Button(ButtonType.BACK, new int[] {10, 0, 10, 20}, Size.MEDIUM, this);
		buttonsPanel.add(back);

		for(int i = 0; i < 12; i++) {
			buttonsPanel.add(new JLabel());
		}

		upload = new Button(ButtonType.UPLOAD, new int[] {10, 20, 10, 0}, Size.MEDIUM, this);
		upload.setToolTipText(Storage.tra("shareStat"));
		buttonsPanel.add(upload);

		this.add(buttonsPanel, BorderLayout.PAGE_END);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton bu = (JButton) e.getSource();

		if(bu == back) {
			Frame.setPanel(isArchive ? new MenuPanel() : new ConnectionPanel(Storage.tra(Text.USERNAME)));
		} else if(bu == upload) {
			Frame.upload("@" + user.getName() + "'s stats");
		}
	}
}
