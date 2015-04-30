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
import me.shadorc.twitterstalker.graphics.EditorPane;
import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.ScrollbarUI;
import me.shadorc.twitterstalker.graphics.ScrollbarUI.Position;
import me.shadorc.twitterstalker.graphics.Storage;
import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.TextField.Text;
import me.shadorc.twitterstalker.statistics.Stats;
import me.shadorc.twitterstalker.statistics.TwitterUser;
import twitter4j.Status;
import twitter4j.TwitterException;

public class StatisticsPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton back, upload;
	private JPanel textPanel;
	private TwitterUser user;
	private Stats stats;

	StatisticsPanel(String name, JButton button, List <Status> statusList) throws TwitterException {
		super(new BorderLayout());

		try {
			user = new TwitterUser(name);
		} catch (TwitterException e) {
			e.printStackTrace();
			throw new TwitterException(Storage.tra("L'utilisateur n'existe pas."), new Exception(name), 604);
		}

		stats = new Stats(user, button, statusList);

		if(Stats.stop == true) return;

		button.setText(Storage.tra("Interface"));

		this.setBackground(new Color(179, 229, 252));

		JPanel top = new JPanel(new BorderLayout());
		top.setBackground(new Color(68,138,255));
		top.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(183,183,183)), BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		JPanel userInfos = new JPanel(new GridLayout(0, 3));
		userInfos.setOpaque(false);

		Font font = Frame.getFont("RobotoCondensed-Regular.ttf", 40);

		JPanel labelsPane = new JPanel(new GridLayout(2, 0));
		labelsPane.setOpaque(false);

		JLabel followers = new JLabel(Storage.tra("Followers : ") + user.getFollowersCount());
		followers.setFont(font);
		followers.setForeground(Color.WHITE);
		labelsPane.add(followers);

		JLabel followings = new JLabel(Storage.tra("Followings : ") + user.getFollowingCount());
		followings.setFont(font);
		followings.setForeground(Color.WHITE);
		labelsPane.add(followings);

		userInfos.add(labelsPane);

		JLabel image = user.getProfileImage();
		image.setHorizontalAlignment(JLabel.CENTER);
		userInfos.add(image);

		font = font.deriveFont(72f);

		JLabel username = new JLabel("@" + user.getName(), JLabel.LEFT);
		username.setForeground(Color.WHITE);
		username.setFont(font);
		userInfos.add(username);

		top.add(userInfos, BorderLayout.CENTER);

		font = font.deriveFont(16f);

		JPanel userInfosStats = new JPanel(new GridLayout(0, 5));
		userInfosStats.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		userInfosStats.setOpaque(false);

		userInfosStats.add(new JLabel());
		userInfosStats.add(new JLabel());

		JLabel tweets = new JLabel(Storage.tra("Tweets analysés : ") + user.getTweetsAnalysed() + "/" + user.getTweetsPosted());
		tweets.setForeground(Color.WHITE);
		tweets.setFont(font);
		userInfosStats.add(tweets);

		JLabel tweetsDays = new JLabel(Storage.tra("Nombre de tweets/jour : ") + user.getTweetsPerDay(stats));
		tweetsDays.setForeground(Color.WHITE);
		tweetsDays.setFont(font);
		userInfosStats.add(tweetsDays);

		JLabel age = new JLabel(Storage.tra("Inscrit le : ") + user.getCreatedAt() + " (" + user.getAge() + Storage.tra(" jours") + ")");
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

		if(OptionsPanel.isSelected(Data.TWEETS))	EditorPane.get(textPanel, stats, "Tweets", Data.WORDS_PER_TWEET, Data.LETTERS_PER_TWEET, Data.LETTERS_PER_WORD);
		if(OptionsPanel.isSelected(Data.TIMELINE))	EditorPane.get(textPanel, stats, "Timeline", Data.PURETWEETS, Data.MENTIONS, Data.RETWEET_BY_ME);
		if(OptionsPanel.isSelected(Data.REPUTE) && statusList == null)		EditorPane.get(textPanel, stats, "Renommée", Data.FAVORITE, Data.RETWEET);
		if(OptionsPanel.isSelected(Data.SOURCE))	EditorPane.get(textPanel, stats, "Sources", Data.SOURCE);
		if(OptionsPanel.isSelected(Data.DAYS))		EditorPane.get(textPanel, stats, "Jours", Data.DAYS);
		if(OptionsPanel.isSelected(Data.HOURS))		EditorPane.get(textPanel, stats, "Heures", Data.HOURS);
		if(OptionsPanel.isSelected(Data.WORDS))		EditorPane.get(textPanel, stats, "Mots", Data.WORDS);
		if(OptionsPanel.isSelected(Data.HASHTAG))	EditorPane.get(textPanel, stats, "Hashtags", Data.HASHTAG);
		if(OptionsPanel.isSelected(Data.POPULARE) && statusList == null)	EditorPane.get(textPanel, stats, "Populaires", Data.POPULARE);
		if(OptionsPanel.isSelected(Data.LANG) && statusList == null)		EditorPane.get(textPanel, stats, "Langues", Data.LANG);
		if(OptionsPanel.isSelected(Data.MENTIONS_SENT))	EditorPane.get(textPanel, stats, "Utilisateurs mentionnés", Data.MENTIONS_SENT);
		if(OptionsPanel.isSelected(Data.MENTIONS_RECEIVED))	EditorPane.get(textPanel, stats, "Utilisateurs mentionnant", Data.MENTIONS_RECEIVED);

		if(textPanel.getComponents().length == 0) {
			textPanel.setLayout(new BorderLayout());
			JLabel error = new JLabel(Storage.tra("Aucune statistique n'a été sélectionnée. Désolé, mais le bug est dans un autre château."), JLabel.CENTER);
			error.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 30));
			textPanel.add(error, JLabel.CENTER);
		} else {
			textPanel.setLayout(new GridLayout((int) Math.ceil(textPanel.getComponents().length/3.0), 3, 15, 15));
		}

		this.add(jsp, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new GridLayout(0, 14));
		buttonsPanel.setOpaque(false);

		back = new Button("Retour", new int[] {10, 0, 10, 20}, true, this);
		buttonsPanel.add(back);

		for(int i = 0; i < 12; i++) {
			buttonsPanel.add(new JLabel());
		}

		upload = new Button("Upload", new int[] {10, 20, 10, 0}, true, this);
		upload.setToolTipText(Storage.tra("Partager les statistiques"));
		buttonsPanel.add(upload);

		this.add(buttonsPanel, BorderLayout.PAGE_END);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton bu = (JButton) e.getSource();

		if(bu == back) {
			Frame.setPanel(new ConnectionPanel(Storage.tra(Text.USERNAME)));
		} else if(bu == upload) {
			Frame.upload("@" + user.getName() + "'s stats");
		}
	}
}
